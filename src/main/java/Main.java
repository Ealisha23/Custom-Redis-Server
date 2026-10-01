import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) {
        // Log output for debugging
        System.out.println("Logs from your program will appear here!");

        int port = 6379;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Allows immediate reuse of port 6379 upon server restart
            serverSocket.setReuseAddress(true);

            while (true) {
                // Continuously listen and accept incoming client connections
                Socket clientSocket = serverSocket.accept();

                // Spawn a new async task/thread for every connected client
                CompletableFuture.runAsync(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            Scanner scanner = new Scanner(inputStream)
        ) {
            // Loop while the client connection remains active
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.toUpperCase().contains("PING")) {
                    // RESP Simple String format for PONG
                    outputStream.write("+PONG\r\n".getBytes());
                    outputStream.flush();
                } else if (line.toUpperCase().contains("ECHO")) {
                    // Extract bulk string length line and value line
                    if (scanner.hasNextLine()) {
                        String restHeader = scanner.nextLine(); // e.g. $10
                        if (scanner.hasNextLine()) {
                            String restBody = scanner.nextLine(); // e.g. strawberry
                            
                            // Send back bulk string in RESP format
                            String response = restHeader + "\r\n" + restBody + "\r\n";
                            outputStream.write(response.getBytes());
                            outputStream.flush();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client handler exception: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Failed to close client socket: " + e.getMessage());
            }
        }
    }
}