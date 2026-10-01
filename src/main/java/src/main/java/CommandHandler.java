package src.main.java;

public class CommandHandler {
    public String handle(String[] tokens) {
        String command = tokens[0].toUpperCase();
        return switch (command) {
            case "PING" -> "+PONG\r\n";
            case "ECHO" -> "$" + tokens[1].length() + "\r\n" + tokens[1] + "\r\n";
            default -> "-ERR unknown command\r\n";
        };
    }
}

