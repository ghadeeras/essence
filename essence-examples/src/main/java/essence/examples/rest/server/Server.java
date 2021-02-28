package essence.examples.rest.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import essence.examples.rest.dao.AccountDao;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Server {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws IOException, SQLException {
        var service = new RestService(new ServiceImpl(new AccountDao(getDataSource())));
        var server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
        var executor = Executors.newSingleThreadExecutor();
        server.setExecutor(executor);

        server.createContext("/service/save", exchange -> invoke(exchange, service.saveAccount));
        server.createContext("/service/delete", exchange -> invoke(exchange, service.deleteAccount));
        server.createContext("/service/find", exchange -> invoke(exchange, service.findAccount));

        server.start();

        System.in.read();
        server.stop(0);
        executor.shutdown();
    }

    private static void invoke(HttpExchange exchange, Function<String, String> function) throws IOException {
        var requestReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        var request = requestReader.lines().collect(Collectors.joining("\n"));
        System.out.println("Request: " + request);
        var response = function.apply(request);
        System.out.println("Response: " + response);
        var responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }

    public static DataSource getDataSource() throws SQLException {
        var dataSource = new JDBCDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:mymemdb");
        dataSource.setUser("SA");
        dataSource.setPassword("");
        dataSource.setLogWriter(new PrintWriter(System.out));
        return dataSource;
    }

}
