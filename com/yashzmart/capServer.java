package com.yashzmart;

import com.google.gson.JsonObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class CapServer {

    private static final int PORT = 8080;

    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        data.createTables();

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT),
                        0);

        server.createContext("/", CapServer::handleStatic);

        server.createContext(
                "/api/login",
                CapServer::login);

        server.createContext(
                "/api/signup",
                CapServer::signup);

        server.createContext(
                "/api/products",
                CapServer::products);

        server.createContext(
                "/api/product",
                CapServer::product);

        server.createContext(
                "/api/add-product",
                CapServer::addProduct);

        server.createContext(
                "/api/order",
                CapServer::createOrder);

        server.createContext(
                "/api/logout",
                CapServer::logout);

        server.createContext(
                "/api/customization",
                CapServer::customization);

        server.createContext(
                "/api/admin",
                CapServer::admin);

        server.setExecutor(null);

        System.out.println(
                "=================================");

        System.out.println(
                "       YASHZ MART SERVER");

        System.out.println(
                "=================================");

        System.out.println(
                "Open: http://localhost:8080/caplogin.html");

        System.out.println(
                "=================================");

        server.start();
    }

    // ==============================
    // STATIC FILE SERVER
    // ==============================

    private static void handleStatic(
            HttpExchange exchange) throws IOException {

        String path =
                exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/caplogin.html";
        }

        String resource =
                path.substring(1);

        InputStream input =
                CapServer.class
                        .getClassLoader()
                        .getResourceAsStream(resource);

        if (input == null) {

            send(
                    exchange,
                    404,
                    "File not found");

            return;
        }

        byte[] data = input.readAllBytes();

        String contentType =
                getContentType(resource);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        contentType);

        exchange.sendResponseHeaders(
                200,
                data.length);

        OutputStream out =
                exchange.getResponseBody();

        out.write(data);
        out.close();

        input.close();
    }

    private static String getContentType(
            String file) {

        if (file.endsWith(".html"))
            return "text/html; charset=UTF-8";

        if (file.endsWith(".css"))
            return "text/css; charset=UTF-8";

        if (file.endsWith(".js"))
            return "application/javascript; charset=UTF-8";

        if (file.endsWith(".json"))
            return "application/json; charset=UTF-8";

        if (file.endsWith(".png"))
            return "image/png";

        if (file.endsWith(".jpg") ||
                file.endsWith(".jpeg"))
            return "image/jpeg";

        return "text/plain; charset=UTF-8";
    }

    // ==============================
    // SIGN UP
    // ==============================

    private static void signup(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            send(exchange, 405, "POST required");
            return;
        }

        String body =
                readBody(exchange);

        JsonObject json =
                gson.fromJson(body, JsonObject.class);

        String name =
                json.get("name").getAsString();

        String email =
                json.get("email").getAsString();

        String password =
                json.get("password").getAsString();

        String role =
                json.has("role")
                        ? json.get("role").getAsString()
                        : "BUYER";

        String sql = """
                INSERT INTO users
                (name, email, password, role)
                VALUES (?, ?, ?, ?)
                """;

        Object data;
        try (Connection con =
                     ((Object) data).connect();
             PreparedStatement ps =
                     con.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);

            ps.executeUpdate();

            ResultSet rs =
                    ps.getGeneratedKeys();

            int userId = 0;

            if (rs.next()) {
                userId = rs.getInt(1);
            }

            ((Object) data).audit(
                    userId,
                    "ACCOUNT_CREATED",
                    "Role: " + role,
                    getIP(exchange));

            sendJson(
                    exchange,
                    Map.of(
                            "success", true,
                            "message",
                            "Account created successfully"
                    ));

        } catch (SQLException e) {

            sendJson(
                    exchange,
                    Map.of(
                            "success", false,
                            "message",
                            "Email already exists"
                    ));
        }
    }

    // ==============================
    // LOGIN
    // ==============================

    private static void login(
            HttpExchange exchange) throws IOException {

        String body =
                readBody(exchange);

        JsonObject json =
                gson.fromJson(body, JsonObject.class);

        String email =
                json.get("email").getAsString();

        String password =
                json.get("password").getAsString();

        String sql = """
                SELECT id, name, email, role
                FROM users
                WHERE email = ?
                AND password = ?
                """;

        try (Connection con =
                     data.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                int id =
                        rs.getInt("id");

                String name =
                        rs.getString("name");

                String role =
                        rs.getString("role");

                data.loginAttempt(
                        email,
                        true,
                        getIP(exchange));

                data.audit(
                        id,
                        "LOGIN_SUCCESS",
                        "Role: " + role,
                        getIP(exchange));

                sendJson(
                        exchange,
                        Map.of(
                                "success", true,
                                "id", id,
                                "name", name,
                                "email", email,
                                "role", role
                        ));

            } else {

                data.loginAttempt(
                        email,
                        false,
                        getIP(exchange));

                data.audit(
                        null,
                        "FAILED_LOGIN",
                        "Email: " + email,
                        getIP(exchange));

                sendJson(
                        exchange,
                        Map.of(
                                "success", false,
                                "message",
                                "Invalid email or password"
                        ));
            }

        } catch (SQLException e) {

            sendJson(
                    exchange,
                    Map.of(
                            "success", false,
                            "message",
                            "Database error"
                    ));
        }
    }

    // ==============================
    // GET PRODUCTS
    // ==============================

    private static void products(
            HttpExchange exchange) throws IOException {

        List<Product> list =
                new ArrayList<>();

        String sql =
                "SELECT * FROM products ORDER BY id DESC";

        try (Connection con =
                     data.connect();
             Statement st =
                     con.createStatement();
             ResultSet rs =
                     st.executeQuery(sql)) {

            while (rs.next()) {

                Product p =
                        new Product();

                p.id =
                        rs.getInt("id");

                p.sellerId =
                        rs.getInt("seller_id");

                p.name =
                        rs.getString("name");

                p.category =
                        rs.getString("category");

                p.description =
                        rs.getString("description");

                p.price =
                        rs.getDouble("price");

                p.image =
                        rs.getString("image");

                p.sizes =
                        rs.getString("sizes");

                p.color =
                        rs.getString("color");

                p.rating =
                        rs.getDouble("rating");

                p.trusted =
                        rs.getInt("trusted") == 1;

                p.customizable =
                        rs.getInt("customizable") == 1;

                list.add(p);
            }

            sendJson(
                    exchange,
                    list);

        } catch (SQLException e) {

            send(
                    exchange,
                    500,
                    "Database error");
        }
    }

    // ==============================
    // SINGLE PRODUCT
    // ==============================

    private static void product(
            HttpExchange exchange) throws IOException {

        Map<String, String> query =
                queryParams(
                        exchange.getRequestURI()
                                .getRawQuery());

        String idText =
                query.get("id");

        if (idText == null) {

            send(
                    exchange,
                    400,
                    "Product ID required");

            return;
        }

        String sql =
                "SELECT * FROM products WHERE id=?";

        try (Connection con =
                     data.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    Integer.parseInt(idText));

            ResultSet rs =
                    ps.executeQuery();

            if (!rs.next()) {

                send(
                        exchange,
                        404,
                        "Product not found");

                return;
            }

            Product p =
                    new Product();

            p.id =
                    rs.getInt("id");

            p.sellerId =
                    rs.getInt("seller_id");

            p.name =
                    rs.getString("name");

            p.category =
                    rs.getString("category");

            p.description =
                    rs.getString("description");

            p.price =
                    rs.getDouble("price");

            p.image =
                    rs.getString("image");

            p.sizes =
                    rs.getString("sizes");

            p.color =
                    rs.getString("color");

            p.rating =
                    rs.getDouble("rating");

            p.trusted =
                    rs.getInt("trusted") == 1;

            p.customizable =
                    rs.getInt("customizable") == 1;

            sendJson(
                    exchange,
                    p);

        } catch (Exception e) {

            send(
                    exchange,
                    500,
                    "Error");
        }
    }

    // ==============================
    // SELLER ADD PRODUCT
    // ==============================

    private static void addProduct(
            HttpExchange exchange) throws IOException {

        String body =
                readBody(exchange);

        JsonObject json =
                gson.fromJson(body, JsonObject.class);

        int sellerId =
                json.get("sellerId").getAsInt();

        String name =
                json.get("name").getAsString();

        String category =
                json.get("category").getAsString();

        String description =
                json.get("description")
                        .getAsString();

        double price =
                json.get("price").getAsDouble();

        String image =
                json.get("image")
                        .getAsString();

        String sizes =
                json.has("sizes")
                        ? json.get("sizes").getAsString()
                        : "";

        String color =
                json.has("color")
                        ? json.get("color").getAsString()
                        : "";

        int customizable =
                json.has("customizable")
                        && json.get("customizable")
                        .getAsBoolean()
                        ? 1 : 0;

        String sql = """
                INSERT INTO products
                (
                    seller_id,
                    name,
                    category,
                    description,
                    price,
                    image,
                    sizes,
                    color,
                    customizable
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con =
                     data.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ps.setString(2, name);
            ps.setString(3, category);
            ps.setString(4, description);
            ps.setDouble(5, price);
            ps.setString(6, image);
            ps.setString(7, sizes);
            ps.setString(8, color);
            ps.setInt(9, customizable);

            ps.executeUpdate();

            data.audit(
                    sellerId,
                    "PRODUCT_LISTED",
                    "Product: " + name +
                            ", Price: ₹" + price,
                    getIP(exchange));

            sendJson(
                    exchange,
                    Map.of(
                            "success", true,
                            "message",
                            "Product listed successfully"
                    ));

        } catch (SQLException e) {

            sendJson(
                    exchange,
                    Map.of(
                            "success", false,
                            "message",
                            "Could not list product"
                    ));
        }
    }

    // ==============================
    // CREATE ORDER
    // ==============================

    private static void createOrder(
            HttpExchange exchange)
            throws IOException {

        String body =
                readBody(exchange);

        JsonObject json =
                gson.fromJson(body, JsonObject.class);

        int buyerId =
                json.get("buyerId").getAsInt();

        double total =
                json.get("total").getAsDouble();

        String paymentMethod =
                json.get("paymentMethod")
                        .getAsString();

        String sql = """
                INSERT INTO orders
                (buyer_id, total, payment_method, status)
                VALUES (?, ?, ?, ?)
                """;

        Object data;
        try (Connection con =
                     data.connect();
             PreparedStatement ps =
                     con.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, buyerId);
            ps.setDouble(2, total);
            ps.setString(3, paymentMethod);
            ps.setString(4, "CONFIRMED");

            ps.executeUpdate();

            data.audit(
                    buyerId,
                    "ORDER_CREATED",
                    "Total: ₹" + total +
                            ", Payment: " +
                            paymentMethod,
                    getIP(exchange));

            sendJson(
                    exchange,
                    Map.of(
                            "success", true,
                            "message",
                            "Order placed successfully"
                    ));

        } catch (SQLException e) {

            sendJson(
                    exchange,
                    Map.of(
                            "success", false,
                            "message",
                            "Order failed"
                    ));
        }
    }

    // ==============================
    // LOGOUT
    // ==============================

    private static void logout(
            HttpExchange exchange)
            throws IOException {

        String body =
                readBody(exchange);

        JsonObject json =
                gson.fromJson(body, JsonObject.class);

        int userId =
                json.get("userId").getAsInt();

        data.audit(
                userId,
                "LOGOUT",
                "User logged out",
                getIP(exchange));

        sendJson(
                exchange,
                Map.of(
                        "success", true,
                        "message",
                        "Logged out"
                ));
    }

    // ==============================
    // CUSTOMIZATION REQUEST
    // ==============================

    private static void customization(
            HttpExchange exchange)
            throws IOException {

        String body =
                readBody(exchange);

        JsonObject json =
                gson.fromJson(body, JsonObject.class);

        int buyerId =
                json.get("buyerId").getAsInt();

        int productId =
                json.get("productId").getAsInt();

        String request =
                json.get("request")
                        .getAsString();

        String sql = """
                INSERT INTO customization_requests
                (buyer_id, product_id, request_text)
                VALUES (?, ?, ?)
                """;

        try (Connection con =
                     data.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.setInt(2, productId);
            ps.setString(3, request);

            ps.executeUpdate();

            data.audit(
                    buyerId,
                    "CUSTOMIZATION_REQUEST",
                    request,
                    getIP(exchange));

            sendJson(
                    exchange,
                    Map.of(
                            "success", true,
                            "message",
                            "Customization request sent to seller"
                    ));

        } catch (SQLException e) {

            sendJson(
                    exchange,
                    Map.of(
                            "success", false,
                            "message",
                            "Request failed"
                    ));
        }
    }

    // ==============================
    // ADMIN
    // ==============================

    private static void admin(
            HttpExchange exchange)
            throws IOException {

        List<Map<String, Object>> data =
                new ArrayList<>();

        String sql = """
                SELECT
                    a.id,
                    a.user_id,
                    u.name,
                    u.email,
                    a.action,
                    a.details,
                    a.ip_address,
                    a.created_at
                FROM audit_logs a
                LEFT JOIN users u
                ON a.user_id = u.id
                ORDER BY a.id DESC
                """;

        try (Connection con =
                     data.connect();
             Statement st =
                     con.createStatement();
             ResultSet rs =
                     st.executeQuery(sql)) {

            while (rs.next()) {

                Map<String, Object> row =
                        new LinkedHashMap<>();

                row.put(
                        "id",
                        rs.getInt("id"));

                row.put(
                        "userId",
                        rs.getObject("user_id"));

                row.put(
                        "name",
                        rs.getString("name"));

                row.put(
                        "email",
                        rs.getString("email"));

                row.put(
                        "action",
                        rs.getString("action"));

                row.put(
                        "details",
                        rs.getString("details"));

                row.put(
                        "ip",
                        rs.getString("ip_address"));

                row.put(
                        "time",
                        rs.getString("created_at"));

                data.add(row);
            }

            sendJson(
                    exchange,
                    data);

        } catch (SQLException e) {

            send(
                    exchange,
                    500,
                    "Admin database error");
        }
    }

    // ==============================
    // HELPERS
    // ==============================

    private static String readBody(
            HttpExchange exchange)
            throws IOException {

        InputStream input =
                exchange.getRequestBody();

        return new String(
                input.readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private static void send(
            HttpExchange exchange,
            int status,
            String text)
            throws IOException {

        byte[] data =
                text.getBytes(
                        StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/plain; charset=UTF-8");

        exchange.sendResponseHeaders(
                status,
                data.length);

        OutputStream out =
                exchange.getResponseBody();

        out.write(data);
        out.close();
    }

    private static void sendJson(
            HttpExchange exchange,
            Object object)
            throws IOException {

        String json =
                gson.toJson(object);

        byte[] data =
                json.getBytes(
                        StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(
                200,
                data.length);

        OutputStream out =
                exchange.getResponseBody();

        out.write(data);
        out.close();
    }

    private static String getIP(
            HttpExchange exchange) {

        return exchange
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();
    }

    private static Map<String, String>
    queryParams(String query) {

        Map<String, String> result =
                new HashMap<>();

        if (query == null)
            return result;

        for (String part :
                query.split("&")) {

            String[] pair =
                    part.split("=");

            if (pair.length == 2) {

                result.put(
                        URLDecoder.decode(
                                pair[0],
                                StandardCharsets.UTF_8),

                        URLDecoder.decode(
                                pair[1],
                                StandardCharsets.UTF_8));
            }
        }

        return result;
    }
}