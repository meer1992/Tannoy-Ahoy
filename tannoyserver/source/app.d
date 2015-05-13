import vibe.core.core;
import vibe.core.log;
import vibe.http.router;
import vibe.http.server;
import vibe.web.rest;
import vibe.data.json;
import std.datetime, core.time;

//TODO:
// - User/Password 
// 	- throw 401 or 418 if unauthorised to perform an action

struct ResponseQueue {
	this(string name){ this.name = name; }
	
	//Track the server name	
	string name;

	//Hashmap of responses (ID maps to message)
	Response[int] queue;

	//Each server has an individual ID counter
	int next_id = 0;
}

struct Response{
	this(SysTime time, int ID, string message, string sender){ 
		this.time = time; 
		this.ID = ID;
		this.message = message;  
		this.sender = sender;
	}
		
	this(int ID, string message, string sender){ 
		this(Clock.currTime, ID, message, sender); 
	}

	//Time at which the response was created
	SysTime time;
	
	//The unique ID for this message
	int ID;

	//The content
	string message;

	//Handle of the user who sent this response
	string sender;
}
 
interface ResponseAPI {
	//GET: return a list of known servers
	string[] getList();

	//GET: return the queue for a specific server
	ResponseQueue getQueue(string server);

	//PUT: add an item to a servers queue
	void putAdd(string server, string message, string sender);

	//PUT: make a new server
	void putMake(string name);

	//PUT: kill the main server
	void putDie();

	//PUT: remove an item from a servers queue
	void putRemove(string server, int ID);
}
 
class API : ResponseAPI {
	this(string name){
		servers[name] = ResponseQueue(name);
		setTimer(15.seconds, &removeItems, true);
	}	

 	protected ResponseQueue[string] servers;

	enum timeout = 30.minutes;
	private void removeItems(){
		size_t i = 0;
		auto now = Clock.currTime;
		logInfo("Starting cleanup at %s", now.toString);	

		auto keys = servers.keys;

		//Enumerate each server to check for cleanup		
		foreach(key; keys){
			auto server = servers[key];
			auto IDs = server.queue.keys;
			
			foreach(ID; IDs){
				if(now - server.queue[ID].time > timeout){
					logInfo("\tRemoval - ID: %s", ID);
					server.queue.remove(ID);
				}
			}
		}
	}

	override:

	string[] getList(){
		logInfo("Get list request\n\tSuccess");
		return servers.keys;
	}

	ResponseQueue getQueue(string server){
		logInfo("Get queue request: Server: %s, Time: %s", server, Clock.currTime.toString);
		if(server !in servers){ 
			logInfo("\tSpecified server does not exist");			
			throw new HTTPStatusException(400, "Specified server does not exist"); 
		}
		logInfo("\tSuccess");
		return servers[server];
	}
 
	void putAdd(string server, string message, string sender){
		logInfo("Add request - Server: %s, Message: %s, Sender: %s, Time: %s", server, message, sender, Clock.currTime.toString);		
		if(server !in servers){
			logInfo("\tSpecified server does not exist");	
			throw new HTTPStatusException(400, "Specified server does not exist"); 
		}		
		auto ID = servers[server].next_id++;		
		servers[server].queue[ID] = Response(ID, message, sender);
		logInfo("\tSuccessfully added");
	}

	void putMake(string server){
		logInfo("Make request - Server: %s, Time: %s", server, Clock.currTime.toString);
		if(server in servers){ 
			logInfo("\tSpecified server already exists");			
			throw new HTTPStatusException(400, "Specified server already exists"); 
		}
		servers[server] = ResponseQueue(server);
		logInfo("\tSuccessfully created");
	}

	void putDie(){
		import std.c.stdlib;
		logInfo("Server going down at %s", Clock.currTime.toString);
		exit(0);
	}

	void putRemove(string server, int ID){
		logInfo("Removal request - Server: %s, ID: %s, Time: %s", server, ID, Clock.currTime.toString);		
		if(server !in servers) { 
			logInfo("\tSpecified server does not exist");			
			throw new HTTPStatusException(400, "Specified server does not exist"); 
		}
		else if(ID !in servers[server].queue){
			logInfo("\tSpecified ID does not exist"); 
			throw new HTTPStatusException(400, "Specified ID does not exist"); 
		}
		servers[server].queue.remove(ID);
		logInfo("\tSuccessfully removed");
	}
}
 
shared static this()
{
	auto router = new URLRouter;
	router.registerRestInterface(new API("Test"));
	auto settings = new HTTPServerSettings;
	settings.port = 8080;
	listenHTTP(settings, router);
}

