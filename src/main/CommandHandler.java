import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandHandler {
    // Thread-safe in-memory store for key-value pairs
    private static final Map<String, String> store = new ConcurrentHashMap<>();

    public static String handleCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "-ERR empty command\r\n";
        }

        // Split incoming command by space or newline delimiters
        String[] tokens = input.trim().split("\\s+");
        String command = tokens[0].toUpperCase();

        switch (command) {
            case "PING":
                return "+PONG\r\n";

            case "ECHO":
                if (tokens.length > 1) {
                    String message = tokens[1];
                    return "$" + message.length() + "\r\n" + message + "\r\n";
                }
                return "-ERR wrong number of arguments for 'echo'\r\n";

            case "SET":
                if (tokens.length >= 3) {
                    String key = tokens[1];
                    String value = tokens[2];
                    store.put(key, value);
                    return "+OK\r\n"; // Simple string response
                }
                return "-ERR wrong number of arguments for 'set'\r\n";

            case "GET":
                if (tokens.length >= 2) {
                    String key = tokens[1];
                    String value = store.get(key);
                    if (value == null) {
                        return "$-1\r\n"; // Null bulk string in RESP
                    }
                    return "$" + value.length() + "\r\n" + value + "\r\n"; // Bulk string response
                }
                return "-ERR wrong number of arguments for 'get'\r\n";

            default:
                return "-ERR unknown command '" + command + "'\r\n";
        }
    }
}