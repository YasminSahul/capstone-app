import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class YashzMartServer {

    public static void main(String[] args) throws Exception {

        int port = 8080;

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(port),
                        0
                );

        server.createContext(
                "/api/status",
                YashzMartServer::status
        );

        server.start();

        System.out.println(
                "================================="
        );

        System.out.println(
                "Yashz Mart Java Server Started"
        );

        System.out.println(
                "Open: http://localhost:8080"
        );

        System.out.println(
                "================================="
        );
    }


    private static void status(
            HttpExchange exchange)
            throws IOException {

        String response =
                "Yashz Mart Java Server is running successfully!";


        exchange.getResponseHeaders()
                .add(
                        "Content-Type",
                        "text/plain"
                );


        exchange.sendResponseHeaders(
                200,
                response.length()
        );


        OutputStream output =
                exchange.getResponseBody();


        output.write(
                response.getBytes()
        );


        output.close();
    }
}
