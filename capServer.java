import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;
import java.util.regex.*;


public class capServer {

    private static final int PORT = 8080;

    private static final String DATABASE =
            "jdbc:sqlite:yashzmart.db";


    public static void main(String[] args)
            throws Exception {

        Class.forName(
            "org.sqlite.JDBC"
        );


        createDatabase();

        insertInitialProducts();


        HttpServer server =
            HttpServer.create(
                new InetSocketAddress(PORT),
                0
            );


        server.createContext(
            "/api/register",
            capServer::register
        );


        server.createContext(
            "/api/login",
            capServer::login
        );


        server.createContext(
            "/api/products",
            capServer::products
        );


        server.createContext(
            "/",
            capServer::serveFile
        );


        server.setExecutor(null);


        server.start();


        System.out.println(
            "================================="
        );

        System.out.println(
            "YASHZ MART SERVER STARTED"
        );

        System.out.println(
            "================================="
        );

        System.out.println(
            "Open:"
        );

        System.out.println(
            "http://localhost:8080/"
        );

        System.out.println(
            "================================="
        );

    }


    /* DATABASE CONNECTION */

    private static Connection database()
            throws SQLException {

        return DriverManager.getConnection(
            DATABASE
        );

    }


    /* CREATE DATABASE */

    private static void createDatabase()
            throws Exception {

        try (
            Connection connection =
                database();

            Statement statement =
                connection.createStatement()
        ) {


            statement.execute(
                "CREATE TABLE IF NOT EXISTS users (" +

                "id INTEGER PRIMARY KEY AUTOINCREMENT," +

                "name TEXT NOT NULL," +

                "email TEXT UNIQUE NOT NULL," +

                "password_hash TEXT NOT NULL," +

                "role TEXT NOT NULL," +

                "created_at TEXT DEFAULT CURRENT_TIMESTAMP" +

                ")"
            );


            statement.execute(
                "CREATE TABLE IF NOT EXISTS products (" +

                "id INTEGER PRIMARY KEY AUTOINCREMENT," +

                "seller_id INTEGER," +

                "name TEXT NOT NULL," +

                "category TEXT NOT NULL," +

                "type TEXT NOT NULL," +

                "price REAL NOT NULL," +

                "color TEXT NOT NULL," +

                "description TEXT," +

                "customizable INTEGER DEFAULT 0," +

                "rating REAL DEFAULT 4.5," +

                "delivery TEXT DEFAULT 'free'," +

                "delivery_time TEXT DEFAULT '3-5'," +

                "trusted INTEGER DEFAULT 0," +

                "FOREIGN KEY(seller_id) REFERENCES users(id)" +

                ")"
            );

        }

    }


    /* INITIAL PRODUCTS */

    private static void insertInitialProducts()
            throws Exception {


        try (
            Connection connection =
                database();

            Statement statement =
                connection.createStatement();

            ResultSet result =
                statement.executeQuery(
                    "SELECT COUNT(*) FROM products"
                )
        ) {


            if (
                result.next() &&
                result.getInt(1) > 0
            ) {

                return;

            }

        }


        String[][] products = {

            {
                "Anarkali Kurti",
                "Women",
                "kurti",
                "899",
                "pink",
                "Beautiful Anarkali Kurti",
                "1"
            },

            {
                "Straight Cut Kurti",
                "Women",
                "kurti",
                "799",
                "purple",
                "Straight cut women's kurti",
                "1"
            },

            {
                "Women's Baggy Jeans",
                "Women",
                "jeans",
                "1199",
                "blue",
                "Comfortable baggy jeans",
                "1"
            },

            {
                "Women's Saree",
                "Women",
                "dress",
                "1499",
                "red",
                "Traditional saree",
                "1"
            },

            {
                "Men's Cotton T-Shirt",
                "Men",
                "tshirt",
                "599",
                "white",
                "Cotton men's T-shirt",
                "0"
            },

            {
                "Men's Formal Shirt",
                "Men",
                "shirt",
                "899",
                "blue",
                "Formal men's shirt",
                "0"
            },

            {
                "Men's Sports Pants",
                "Men",
                "pants",
                "799",
                "black",
                "Sports pants",
                "0"
            },

            {
                "Men's Running Shoes",
                "Men",
                "shoe",
                "1699",
                "black",
                "Running shoes",
                "0"
            },

            {
                "Girls' Cotton Frock",
                "Kids",
                "dress",
                "499",
                "pink",
                "Cotton frock for girls",
                "1"
            },

            {
                "Boys' Printed T-Shirt",
                "Kids",
                "tshirt",
                "449",
                "blue",
                "Printed boys T-shirt",
                "1"
            },

            {
                "Kids' Night Suit",
                "Kids",
                "pants",
                "549",
                "green",
                "Kids night suit",
                "1"
            },

            {
                "Professional Football",
                "Sports",
                "football",
                "899",
                "white",
                "Professional football",
                "0"
            },

            {
                "Indoor Basketball",
                "Sports",
                "basketball",
                "999",
                "orange",
                "Indoor basketball",
                "0"
            },

            {
                "Training Volleyball",
                "Sports",
                "volleyball",
                "799",
                "white",
                "Training volleyball",
                "0"
            },

            {
                "Cricket Bat",
                "Sports",
                "cricket",
                "1299",
                "brown",
                "Cricket bat",
                "0"
            },

            {
                "Badminton Racket",
                "Sports",
                "racket",
                "1099",
                "blue",
                "Badminton racket",
                "0"
            },

            {
                "Training Dumbbell",
                "Sports",
                "dumbbell",
                "799",
                "black",
                "Training dumbbell",
                "0"
            },

            {
                "Soft Teddy Bear",
                "Toys & Gifts",
                "teddy",
                "699",
                "brown",
                "Soft teddy bear",
                "1"
            },

            {
                "Birthday Gift Box",
                "Toys & Gifts",
                "gift",
                "499",
                "purple",
                "Birthday gift box",
                "1"
            },

            {
                "Kids Digital Watch",
                "Toys & Gifts",
                "watch",
                "599",
                "black",
                "Kids digital watch",
                "1"
            },

            {
                "Gold Style Chain",
                "Accessories",
                "chain",
                "799",
                "yellow",
                "Fashion chain",
                "0"
            },

            {
                "Classic Watch",
                "Accessories",
                "watch",
                "1299",
                "black",
                "Classic watch",
                "0"
            },

            {
                "Women's Hand Bag",
                "Accessories",
                "bag",
                "999",
                "black",
                "Women's handbag",
                "0"
            },

            {
                "Comfort Slippers",
                "Accessories",
                "slipper",
                "399",
                "pink",
                "Comfort slippers",
                "0"
            }

        };


        String sql =
            "INSERT INTO products " +
            "(name,category,type,price,color," +
            "description,customizable,rating," +
            "delivery,delivery_time,trusted)" +

            " VALUES (?,?,?,?,?,?,?,4.6,'free','3-5',1)";


        try (
            Connection connection =
                database();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {


            for (
                String[] product :
                products
            ) {

                statement.setString(
                    1,
                    product[0]
                );

                statement.setString(
                    2,
                    product[1]
                );

                statement.setString(
                    3,
                    product[2]
                );

                statement.setDouble(
                    4,
                    Double.parseDouble(
                        product[3]
                    )
                );

                statement.setString(
                    5,
                    product[4]
                );

                statement.setString(
                    6,
                    product[5]
                );

                statement.setInt(
                    7,
                    Integer.parseInt(
                        product[6]
                    )
                );

                statement.addBatch();

            }


            statement.executeBatch();

        }

    }


    /* REGISTER */

    private static void register(
        HttpExchange exchange
    ) throws IOException {


        try {

            Map<String,String> data =
                parseJson(
                    readBody(exchange)
                );


            String name =
                data.get("name");

            String email =
                data.get("email")
                    .toLowerCase();

            String password =
                data.get("password");

            String role =
                data.get("role");


            if (
                name == null ||
                email == null ||
                password == null ||
                role == null
            ) {

                sendJson(
                    exchange,
                    400,
                    "{\"message\":\"All fields are required\"}"
                );

                return;

            }


            String hashedPassword =
                hashPassword(password);


            try (
                Connection connection =
                    database();

                PreparedStatement statement =
                    connection.prepareStatement(
                        "INSERT INTO users " +
                        "(name,email,password_hash,role) " +
                        "VALUES (?,?,?,?)"
                    )
            ) {


                statement.setString(
                    1,
                    name
                );

                statement.setString(
                    2,
                    email
                );

                statement.setString(
                    3,
                    hashedPassword
                );

                statement.setString(
                    4,
                    role
                );


                statement.executeUpdate();

            }


            sendJson(
                exchange,
                200,
                "{\"message\":\"Account created successfully\"}"
            );

        }

        catch (SQLException error) {

            sendJson(
                exchange,
                400,
                "{\"message\":\"Email already exists\"}"
            );

        }

        catch (Exception error) {

            sendJson(
                exchange,
                500,
                "{\"message\":\"Server error\"}"
            );

        }

    }


    /* LOGIN */

    private static void login(
        HttpExchange exchange
    ) throws IOException {


        try {

            Map<String,String> data =
                parseJson(
                    readBody(exchange)
                );


            String email =
                data.get("email")
                    .toLowerCase();

            String password =
                data.get("password");

            String role =
                data.get("role");


            try (
                Connection connection =
                    database();

                PreparedStatement statement =
                    connection.prepareStatement(

                        "SELECT id,name,email,role " +

                        "FROM users " +

                        "WHERE email=? " +

                        "AND password_hash=? " +

                        "AND role=?"

                    )
            ) {


                statement.setString(
                    1,
                    email
                );

                statement.setString(
                    2,
                    hashPassword(password)
                );

                statement.setString(
                    3,
                    role
                );


                ResultSet result =
                    statement.executeQuery();


                if (
                    !result.next()
                ) {

                    sendJson(
                        exchange,
                        401,
                        "{\"message\":\"Invalid email, password or account type\"}"
                    );

                    return;

                }


                String json =

                    "{"

                    + "\"user\":{"

                    + "\"id\":"
                    + result.getInt("id")
                    + ","

                    + "\"name\":\""
                    + escape(
                        result.getString(
                            "name"
                        )
                    )
                    + "\","

                    + "\"email\":\""
                    + escape(
                        result.getString(
                            "email"
                        )
                    )
                    + "\","

                    + "\"role\":\""
                    + escape(
                        result.getString(
                            "role"
                        )
                    )
                    + "\""

                    + "}"

                    + "}";


                sendJson(
                    exchange,
                    200,
                    json
                );

            }

        }

        catch (Exception error) {

            sendJson(
                exchange,
                500,
                "{\"message\":\"Login server error\"}"
            );

        }

    }


    /* PRODUCTS */

    private static void products(
        HttpExchange exchange
    ) throws IOException {


        try {

            if (
                exchange
                    .getRequestMethod()
                    .equals("GET")
            ) {

                sendJson(
                    exchange,
                    200,
                    getProducts()
                );

                return;

            }


            if (
                exchange
                    .getRequestMethod()
                    .equals("POST")
            ) {

                Map<String,String> data =
                    parseJson(
                        readBody(exchange)
                    );


                try (
                    Connection connection =
                        database();

                    PreparedStatement statement =
                        connection.prepareStatement(

                            "INSERT INTO products " +

                            "(seller_id,name,category,type," +

                            "price,color,description," +

                            "customizable,rating,delivery," +

                            "delivery_time,trusted)" +

                            " VALUES (?,?,?,?,?,?,?,?," +

                            "4.6,'free','3-5',0)"

                        )
                ) {


                    statement.setInt(
                        1,
                        Integer.parseInt(
                            data.get("sellerId")
                        )
                    );

                    statement.setString(
                        2,
                        data.get("name")
                    );

                    statement.setString(
                        3,
                        data.get("category")
                    );

                    statement.setString(
                        4,
                        data.get("type")
                    );

                    statement.setDouble(
                        5,
                        Double.parseDouble(
                            data.get("price")
                        )
                    );

                    statement.setString(
                        6,
                        data.get("color")
                    );

                    statement.setString(
                        7,
                        data.get("description")
                    );

                    statement.setInt(
                        8,
                        "true".equals(
                            data.get(
                                "customizable"
                            )
                        )
                        ? 1
                        : 0
                    );


                    statement.executeUpdate();

                }


                sendJson(
                    exchange,
                    200,
                    "{\"message\":\"Product added successfully\"}"
                );

                return;

            }


            sendJson(
                exchange,
                405,
                "{\"message\":\"Method not allowed\"}"
            );

        }

        catch (Exception error) {

            sendJson(
                exchange,
                500,
                "{\"message\":\"Product database error\"}"
            );

        }

    }


    /* GET PRODUCTS */

    private static String getProducts()
            throws Exception {


        StringBuilder json =
            new StringBuilder("[");


        try (
            Connection connection =
                database();

            Statement statement =
                connection.createStatement();

            ResultSet result =
                statement.executeQuery(
                    "SELECT * FROM products " +
                    "ORDER BY id DESC"
                )
        ) {


            boolean first = true;


            while (
                result.next()
            ) {


                if (!first) {

                    json.append(",");

                }


                first = false;


                json.append("{");

                json.append(
                    "\"id\":" +
                    result.getInt("id")
                );

                json.append(",");

                json.append(
                    "\"name\":\"" +
                    escape(
                        result.getString(
                            "name"
                        )
                    ) +
                    "\""
                );

                json.append(",");

                json.append(
                    "\"category\":\"" +
                    escape(
                        result.getString(
                            "category"
                        )
                    ) +
                    "\""
                );

                json.append(",");

                json.append(
                    "\"type\":\"" +
                    escape(
                        result.getString(
                            "type"
                        )
                    ) +
                    "\""
                );

                json.append(",");

                json.append(
                    "\"price\":" +
                    result.getDouble(
                        "price"
                    )
                );

                json.append(",");

                json.append(
                    "\"color\":\"" +
                    result.getString(
                        "color"
                    ) +
                    "\""
                );

                json.append(",");

                json.append(
                    "\"description\":\"" +
                    escape(
                        result.getString(
                            "description"
                        )
                    ) +
                    "\""
                );

                json.append(",");

                json.append(
                    "\"customizable\":" +
                    (
                        result.getInt(
                            "customizable"
                        ) == 1
                    )
                );

                json.append(",");

                json.append(
                    "\"rating\":" +
                    result.getDouble(
                        "rating"
                    )
                );

                json.append(",");

                json.append(
                    "\"delivery\":\"" +
                    result.getString(
                        "delivery"
                    ) +
                    "\""
                );

                json.append(",");

                json.append(
                    "\"deliveryTime\":\"" +
                    result.getString(
                        "delivery_time"
                    ) +
                    "\""
                );

                json.append(",");

                json.append(
                    "\"trusted\":" +
                    (
                        result.getInt(
                            "trusted"
                        ) == 1
                    )
                );

                json.append("}");

            }

        }


        json.append("]");


        return json.toString();

    }


    /* STATIC FILE SERVER */

    private static void serveFile(
        HttpExchange exchange
    ) throws IOException {


        String path =
            exchange
                .getRequestURI()
                .getPath();


        if (
            path.equals("/")
        ) {

            path =
                "/caplogin.html";

        }


        if (
            path.contains("..")
        ) {

            sendText(
                exchange,
                403,
                "Forbidden"
            );

            return;

        }


        Path file =
            Paths.get(
                ".",
                path.substring(1)
            );


        if (
            !Files.exists(file) ||
            Files.isDirectory(file)
        ) {

            sendText(
                exchange,
                404,
                "404 - File Not Found"
            );

            return;

        }


        byte[] data =
            Files.readAllBytes(file);


        exchange
            .getResponseHeaders()
            .set(
                "Content-Type",
                getContentType(file)
            );


        exchange.sendResponseHeaders(
            200,
            data.length
        );


        try (
            OutputStream output =
                exchange.getResponseBody()
        ) {

            output.write(data);

        }

    }


    /* PASSWORD HASH */

    private static String hashPassword(
        String password
    ) throws Exception {


        MessageDigest digest =
            MessageDigest.getInstance(
                "SHA-256"
            );


        byte[] bytes =
            digest.digest(
                password.getBytes(
                    StandardCharsets.UTF_8
                )
            );


        StringBuilder result =
            new StringBuilder();


        for (
            byte b :
            bytes
        ) {

            result.append(
                String.format(
                    "%02x",
                    b
                )
            );

        }


        return result.toString();

    }


    /* SIMPLE JSON READER */

    private static Map<String,String> parseJson(
        String json
    ) {


        Map<String,String> map =
            new HashMap<>();


        Pattern pattern =
            Pattern.compile(
                "\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\""
            );


        Matcher matcher =
            pattern.matcher(json);


        while (
            matcher.find()
        ) {

            map.put(
                matcher.group(1),
                matcher.group(2)
            );

        }


        return map;

    }


    private static String readBody(
        HttpExchange exchange
    ) throws IOException {

        return new String(
            exchange
                .getRequestBody()
                .readAllBytes(),
            StandardCharsets.UTF_8
        );

    }


    private static void sendJson(
        HttpExchange exchange,
        int status,
        String json
    ) throws IOException {


        byte[] data =
            json.getBytes(
                StandardCharsets.UTF_8
            );


        exchange
            .getResponseHeaders()
            .set(
                "Content-Type",
                "application/json; charset=UTF-8"
            );


        exchange.sendResponseHeaders(
            status,
            data.length
        );


        try (
            OutputStream output =
                exchange.getResponseBody()
        ) {

            output.write(data);

        }

    }


    private static void sendText(
        HttpExchange exchange,
        int status,
        String text
    ) throws IOException {


        byte[] data =
            text.getBytes(
                StandardCharsets.UTF_8
            );


        exchange
            .getResponseHeaders()
            .set(
                "Content-Type",
                "text/plain; charset=UTF-8"
            );


        exchange.sendResponseHeaders(
            status,
            data.length
        );


        try (
            OutputStream output =
                exchange.getResponseBody()
        ) {

            output.write(data);

        }

    }


    private static String escape(
        String text
    ) {

        if (text == null)
            return "";


        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", " ")
            .replace("\r", " ");

    }


    private static String getContentType(
        Path file
    ) {

        String name =
            file
                .getFileName()
                .toString()
                .toLowerCase();


        if (
            name.endsWith(".html")
        )
            return "text/html; charset=UTF-8";


        if (
            name.endsWith(".css")
        )
            return "text/css; charset=UTF-8";


        if (
            name.endsWith(".js")
        )
            return "application/javascript; charset=UTF-8";


        if (
            name.endsWith(".json")
        )
            return "application/json; charset=UTF-8";


        return "application/octet-stream";
    }
}