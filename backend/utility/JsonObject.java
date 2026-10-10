package backend.utility;

public class JsonObject {

    public static String message(
            String text) {

        return "{\"message\":\""
                + text
                + "\"}";
    }

}