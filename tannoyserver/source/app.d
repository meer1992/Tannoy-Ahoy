import vibe.core.core;
import vibe.core.log;
import vibe.http.router;
import vibe.http.server;
import vibe.web.rest;
import std.datetime;
 
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
	ResponseQueue getQueue();
	@property void add(string add);
	@property string add();
}
 
class ResponseImplementation : ResponseAPI {
	protected ResponseQueue responseQueue;
 	
	ResponseQueue getQueue(){
		return responseQueue;
	}
 
	@property void add(string message){
		responseQueue.queue ~= Response(message);
	}
 
	@property string add(){ return null; }
}
 
shared static this()
{
	auto router = new URLRouter;
	router.registerRestInterface(new ResponseImplementation);
	auto settings = new HTTPServerSettings;
	settings.port = 8080;
	listenHTTP(settings, router);
}

