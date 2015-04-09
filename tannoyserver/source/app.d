import vibe.core.core;
import vibe.core.log;
import vibe.http.router;
import vibe.http.server;
import vibe.web.rest;
import std.datetime;
 
//Easy ways to populate the server with data using curl:
// $ curl -H "Content-Type: application/json" -X GET localhost:8080/queue
// $ curl -H "Content-Type: application/json" -X PUT -d '{"message":"this is a test message"}' localhost:8080/add

//Using Java:
// GET: http://stackoverflow.com/a/1485730
// PUT: http://stackoverflow.com/a/1051105


struct ResponseQueue {
	string sender;
	Response[] queue;
}
 
struct Response{
	this(SysTime time, string message){ this.time = time; this.message = message; }
	this(string message){ this(Clock.currTime, message); }
	SysTime time;
	string message;
}
 
interface ResponseAPI {
	//Accessible by: GET localhost:8080/queue -- Returns a JSON object
	ResponseQueue getQueue();

	//Accessible by: PUT localhost:8080/add {"message":"..."} -- Must be JSON formatted.
	@property void add(string message);
}
 
class ResponseImplementation : ResponseAPI {
	this(string sender){
		responseQueue = ResponseQueue(sender);
	}	

	protected ResponseQueue responseQueue;
 	
	ResponseQueue getQueue(){
		return responseQueue;
	}
 
	@property void add(string message){
		responseQueue.queue ~= Response(message);
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

