module tannoy.server;

import std.datetime, core.time, std.stdio;
import std.range : array;
import std.algorithm : map, canFind;
import vibe.data.serialization;
import vibe.http.common;
import vibe.core.log;
import vibe.core.core;

//Messages for HTTPStatusException
enum ERROR_ID     = "Could not find the specified ID";
enum ERROR_SERVER = "Could not find the specified server";
enum ERROR_USER   = "Invalid credentials";

//Messages for logInfo
enum LOG_LIST     = "[%s] List requested";
enum LOG_QUEUE	  = "[%s] Queue requested: Server = %s";
enum LOG_VALID	  = "[%s] Credential checking: Server = %s - Username = %s";
enum LOG_ADD	  = "[%s] Adding message: Server = %s - Username = %s - Message = %s";
enum LOG_DIE	  = "[%s] Server going down: Username = %s";
enum LOG_REMOVE	  = "[%s] Removing message: Server = %s - ID = %s - Username = %s";

//Struct to represent an admin users
struct Admin {
	this(string username, string password){
		this.username = username;
		this.password = password.hash;
	}

	//Plaintext username	
	string username; 
	
	//Salted and hashed password
	@ignore string password;
}


//Struct representing a server object
struct Server {
	this(string server){ 
		this.server = server; 
	}
	
	//Track the server name	
	string server;

	//Hashmap of responses (ID maps to message)
	Response[int] queue;

	//Each server has an individual ID counter | Nonserializable
	@ignore protected int next_id = 0;

	//The users allowed to perform admin tasks (add/remove/die) | Nonserializable
	@ignore protected Admin[] admins;

	//Allow the API to check if an admin exists for this server
	bool inAdmins(Admin admin){ return admins.canFind(admin); }
}


//Struct representing a message
struct Response{
	this(SysTime time, int ID, string message){ 
		this.time = time; 
		this.ID = ID;
		this.message = message;  
	}
		
	this(int ID, string message){ 
		this(Clock.currTime, ID, message); 
	}

	//Time at which the response was created
	SysTime time;
	
	//The unique ID for this message
	int ID;

	//The content
	string message;
}
 

//Interface for the API (required for vibe to figure out route paths)
interface ResponseAPI {
	//GET: return a list of known servers
	//Accepts: GET https://server.url/list
	//Returns: `["Server 1", "Server 2", "Server 3"]`
	string[] getList();

	//GET: return the queue for a specific server
	//Accepts: GET https://server.url/queue?server=value
	//Returns: `[{"time":"2015-05-18T00:06:51.4043832","ID":0,"message":"This is a test message"},{...}]`
	Response[] getQueue(string server);

	//GET: check if a user/pass combo is valid for a server
	//Accepts: GET https://server.url/valid?server=value&username=value&password=value
	//Returns: `true` or `false`
	bool getValid(string server, string username, string password);

	//PUT: add an item to a servers queue
	//Accepts: PUT {"server":"value", "message":"value", "username":"value", "password":"value"} https://server.url/add
	//Returns: {}
	void putAdd(string server, string message, string username, string password);

	//PUT: kill the main server
	//Accepts: PUT {"username":"value", "password":"value"} https://server.url/die
	//Returns: {}	
	void putDie(string username, string password);

	//PUT: remove an item from a servers queue
	//Accepts: PUT {"server":"value", "ID":value, "username":"value", "password":"value"} https://server.url/remove
	//Returns: {}
	void putRemove(string server, int ID, string username, string password);
}
 
class API : ResponseAPI {
	enum delay = 15.seconds;	
	
	this(){
		setTimer(delay, &removeItems, true);
	}	

	void makeServer(string server, Admin[] admins...){
		serverList[server] = Server(server);
		serverList[server].admins = admins;
	}

 	protected Server[string] serverList;
	
	enum timeout = 30.minutes;
	private void removeItems(){
		size_t i = 0;
		auto now = Clock.currTime;
		logInfo("Starting cleanup at %s", now.toString);	
		auto keys = serverList.keys;
		foreach(key; keys){
			auto server = serverList[key];
			auto IDs = server.queue.keys;
			foreach(ID; IDs){
				if(now - server.queue[ID].time > timeout){
					logInfo("\t-> Removal - ID: %s", ID);
					server.queue.remove(ID);
				}
			}
		}
	}

	string[] getList(){
		logInfo(LOG_LIST, time);
		return serverList.keys;
	}

	Response[] getQueue(string server){
		logInfo(LOG_QUEUE, time, server);	
		if(server !in serverList){ 
			logInfo("\tCouldn't find server");
			throw new HTTPStatusException(400, ERROR_SERVER); 
		}
		logInfo("\tSuccess");
		return serverList[server].queue.values;
	}

	bool getValid(string server, string username, string password){
		logInfo(LOG_VALID, time, server, username);		
		if(server !in serverList){
			logInfo("\tCouldn't find server");
			throw new HTTPStatusException(400, ERROR_SERVER);
		}
		auto result = serverList[server].inAdmins( Admin(username, password) );
		logInfo("\tValid: %s", result);		
		return result;
	}
 
	void putAdd(string server, string message, string username, string password){
		logInfo(LOG_ADD, time, server, username, message);		
		if(server !in serverList){
			logInfo("\tCouldn't find server");
			throw new HTTPStatusException(400, ERROR_SERVER); 
		}	
		else if(!serverList[server].inAdmins( Admin(username, password) )){
			logInfo("\tInvalid user/pass");			
			throw new HTTPStatusException(403, ERROR_USER);
		}
		logInfo("\tSuccess");
		auto ID = serverList[server].next_id++;		
		serverList[server].queue[ID] = Response(ID, message);
		
	}

	void putDie(string username, string password){
		import std.c.stdlib;		
		if(username != "admin" || password != "admin"){
			logInfo("[%s] %s attempted to kill the server, but was rejected", time, username);		 	
			throw new HTTPStatusException(403, ERROR_USER);
		}
		logInfo(LOG_DIE, time, username);
		exit(0);
	}

	void putRemove(string server, int ID, string username, string password){		
		logInfo(LOG_REMOVE, time, server, ID, username);
		if(server !in serverList) { 
			logInfo("\tCouldn't find server");
			throw new HTTPStatusException(400, ERROR_SERVER); 
		}
		if(!serverList[server].inAdmins( Admin(username, password) )){
			logInfo("\tInvalid user/pass");			
			throw new HTTPStatusException(403, ERROR_USER);
		}
		if(ID !in serverList[server].queue){ 
			logInfo("\tCouldn't find ID");
			throw new HTTPStatusException(400, ERROR_ID); 
		}
		logInfo("\tSuccess");
		serverList[server].queue.remove(ID);
	}
}

///Get the current time as a string
string time(){
	return Clock.currTime.toString;
}

///Salt the input, hash it, transform to a hex string, return it.
string hash(string input){
	import std.digest.md, std.digest.digest;
	return (input~"salty").md5Of.toHexString.idup;
}
