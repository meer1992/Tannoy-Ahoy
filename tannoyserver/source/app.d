import vibe.core.core;
import vibe.core.log;
import vibe.http.router;
import vibe.http.server;
import vibe.web.rest;
import std.datetime, core.time;
 
//Easy ways to populate the server with data using curl:
// $ curl -H "Content-Type: application/json" -X GET localhost:8080/queue
// $ curl -H "Content-Type: application/json" -X PUT -d '{"message":"this is a test message"}' localhost:8080/add

//Using Java:
// GET: http://stackoverflow.com/a/1485730
// PUT: http://stackoverflow.com/a/1051105

//For getting the next unique ID of a message
static int next_id = 0;

struct ResponseQueue {
	this(string server_name){ this.server_name = server_name; }
	string server_name;
	Response[] queue;
}

struct Response{
	this(SysTime time, string message, string sender){ 
		this.time = time; 
		this.message = message; 
		this.ID = next_id++; 
		this.sender = sender;
	}
		
	this(string message, string sender){ 
		this(Clock.currTime, message, sender); 
	}

	SysTime time;
	int ID;
	string message;
	string sender;
}
 
interface ResponseAPI {
	//Accessible by: GET localhost:8080/queue -- Returns a JSON object
	ResponseQueue getQueue();

	//Accessible by: PUT localhost:8080/add {"message":"..."} -- Must be JSON formatted.
	@property void add(string message, string sender);

	//For testing purposes (kill the server)
	@property void die();

	//Remove an element
	@property void remove(int ID);

	string getFoo();
}
 
class ResponseImplementation : ResponseAPI {
	this(string server_name){
		responseQueue = ResponseQueue(server_name);
		setTimer(15.seconds, &removeItems, true);
	}	

	protected ResponseQueue responseQueue;
 	
	ResponseQueue getQueue(){
		logInfo("Queue requested at %s", Clock.currTime.toString);
		return responseQueue;
	}

	string getFoo(){
		import vibe.data.json;
		return responseQueue.serializeToPrettyJson;
	}
 
	@property void add(string message, string sender){
		logInfo("%s added '%s' at %s", sender, message, Clock.currTime.toString);
		responseQueue.queue ~= Response(message, sender);
	}

	@property void die(){
		import std.c.stdlib;
		logInfo("Server going down at %s", Clock.currTime.toString);
		exit(0);
	}

	@property void remove(int ID){
		size_t j = -1;
		logInfo("Removal requested at %s", Clock.currTime.toString);
		foreach(i, r; responseQueue.queue){
			if(r.ID == ID){ j = i; break; }
		}

		if(j!=-1){
			responseQueue.queue = responseQueue.queue[0..j] ~ responseQueue.queue[j+1..$];
			logInfo("\tRemoval made at %s", j);
		}
		else{
			logInfo("\tNo removal made, couldn't find index");
		}
	}

	enum timeout = 30.minutes;
	private void removeItems(){
		import core.time;
		
		size_t i = 0;
		auto now = Clock.currTime;
		logInfo("Starting cleanup at %s", now.toString);
		while(i < responseQueue.queue.length){
			if(now - responseQueue.queue[i].time > timeout){
				responseQueue.queue = responseQueue.queue[0..i] ~ responseQueue.queue[i+1..$];
				logInfo("\tMade removal at index %s", i);
			}
			else i++;
		}
	}
}
 
shared static this()
{
	auto router = new URLRouter;
	router.registerRestInterface(new ResponseImplementation("Test Server"));
	auto settings = new HTTPServerSettings;
	settings.port = 8080;
	listenHTTP(settings, router);
}

