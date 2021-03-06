module tannoy.main;

import vibe.vibe;
import vibe.http.router      : URLRouter;
import vibe.http.server      : HTTPServerSettings, listenHTTP;
import vibe.stream.ssl 	     : createSSLContext, SSLContextKind;
import vibe.web.rest 	     : registerRestInterface;
import std.file 	     : exists;
import tannoy.server 	     : API, Admin;
import core.sys.posix.unistd : fork;

void main(){
	//Check if we're OK to run
	if (!finalizeCommandLineOptions()) return;

	//Ensure SSL key and certificate is present
	assert(exists("./keys/cert.crt"), "Couldn't find the certificate. Run ./keys/make.sh");
	assert(exists("./keys/key.pem"), "Couldn't find the private key. Run ./keys/make.sh");

	//Create the router for the server
	auto router = new URLRouter;

	//Initialise the API
	auto api = new API();
	
	//Default admins, any combination can be added
	auto admins = [ Admin("admin", "secret") ];

	//Default servers
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

	//Process fork must be placed here due to the SSL key/cert loading
	auto id = fork();
	if(id == -1) assert(0, "Failed to fork");
	if(id != 0) return;
	
	//Begin listening
	listenHTTP(settings, router);
	lowerPrivileges();
	runEventLoop();
}
