package essence.examples.rest.server;

import essence.examples.rest.dao.AccountDao;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.SQLException;

public class Server {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws IOException, SQLException {
        ResourceConfig config = new ResourceConfig();
        config.register(new RestService(new ServiceImpl(new AccountDao(getDataSource()))));

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8080/"), config);
        server.start();

        System.in.read();
        server.shutdownNow();
    }

    public static DataSource getDataSource() throws SQLException {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:mymemdb");
        dataSource.setUser("SA");
        dataSource.setPassword("");
        dataSource.setLogWriter(new PrintWriter(System.out));
        return dataSource;
    }

}
