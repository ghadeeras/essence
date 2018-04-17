package essence.examples.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Server {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws IOException {
        ResourceConfig config = new ResourceConfig();
        config.register(new RestService(new ServiceImpl()));

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8080/"), config);
        server.start();

        System.in.read();
        server.shutdownNow();
    }

}
