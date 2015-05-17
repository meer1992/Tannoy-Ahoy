module tannoy.main;

import vibe.core.core;
import vibe.core.log;
import vibe.http.router;
import vibe.http.server;
import vibe.stream.ssl;
import vibe.web.rest;
import std.file;
import tannoy.server;

shared static this()
{
	//Ensure SSL key and certificate is present
	assert(exists("./keys/cert.crt"), "Couldn't find the certificate. Run ./keys/make.sh");
	assert(exists("./keys/key.pem"), "Couldn't find the private key. Run ./keys/make.sh");

	//Create the router for the server
	auto router = new URLRouter;

	//Initialise the API
	auto api = new API();
	
	//Default admins, any combination can be added
	auto admins = [ Admin("admin", "admin") ];

	//Default servers. Feel free to add whatever you see fit
	api.makeServer("Auckland Airport", admins);
	api.makeServer("Wellington Airport", admins);
	api.makeServer("Christchurch Airport", admins);
	api.makeServer("Wellington Railway Station", admins);
	api.makeServer("Britomart Transport Centre", admins);

	//Register API
	router.registerRestInterface(api);

	//Create settings
	auto settings = new HTTPServerSettings;
	
	//Set port
	settings.port = 8080;
	
	//Set SSL
	settings.sslContext = createSSLContext(SSLContextKind.server);
	settings.sslContext.useCertificateChainFile("./keys/cert.crt");
	settings.sslContext.usePrivateKeyFile("./keys/key.pem");
	
	//Begin listening
	listenHTTP(settings, router);
}
