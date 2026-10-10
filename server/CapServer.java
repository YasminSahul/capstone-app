package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class CapServer {

    public static void main(String[] args)
            throws Exception {

        int port = 8080;

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(port),
                        0
                );

        server.createContext(
                "/",
                CapServer::handleRequest
        );

        server.setExecutor(null);

        System.out.println(
                "================================"
        );

        System.out.println(
                "       YASHZ MART SERVER"
        );

        System.out.println(
                "================================"
        );

        System.out.println(
                "Server started successfully."
        );

        System.out.println(
                "Open:"
        );

        System.out.println(
                "http://localhost:8080/caplogin.html"
        );

        System.out.println(
                "================================"
        );

        server.start();
    }


    private static void handleRequest(
            HttpExchange exchange)
            throws IOException {

        String path =
                exchange.getRequestURI()
                        .getPath();

        if (path.equals("/")) {

            path =
                    "/caplogin.html";
        }

        String fileName =
                path.substring(1);

        File file =
                new File(fileName);

        if (!file.exists()) {

            sendResponse(
                    exchange,
                    404,
                    "File not found."
            );

            return;
        }

        byte[] data =
                java.nio.file.Files
                        .readAllBytes(
                                file.toPath()
                        );

        String contentType =
                getContentType(fileName);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        contentType
                );

        exchange.sendResponseHeaders(
                200,
                data.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(data);

        output.close();
    }


    private static String getContentType(
            String fileName) {

        if (fileName.endsWith(".html")) {

            return "text/html; charset=UTF-8";
        }

        if (fileName.endsWith(".css")) {

            return "text/css; charset=UTF-8";
        }

        if (fileName.endsWith(".js")) {

            return
                "application/javascript; charset=UTF-8";
        }

        return "text/plain; charset=UTF-8";
    }


    private static void sendResponse(
            HttpExchange exchange,
            int status,
            String message)
            throws IOException {

        byte[] data =
                message.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.sendResponseHeaders(
                status,
                data.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(data);

        output.close();
    }
}