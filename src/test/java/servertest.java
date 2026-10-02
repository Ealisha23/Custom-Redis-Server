import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class servertest {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 6379);
             OutputStream out = socket.getOutputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. Test PING
            out.write("PING\r\n".getBytes());
            out.flush();
            System.out.println("PING Response: " + in.readLine());

            // 2. Test SET mykey myvalue
            out.write("SET mykey myvalue\r\n".getBytes());
            out.flush();
            System.out.println("SET Response: " + in.readLine());

            // 3. Test GET mykey
            out.write("GET mykey\r\n".getBytes());
            out.flush();
            String lengthHeader = in.readLine(); // Expecting $7
            String value = in.readLine();        // Expecting myvalue
            System.out.println("GET Response (" + lengthHeader + "): " + value);

            // 4. Test GET non-existent key
            out.write("GET nonexistent\r\n".getBytes());
            out.flush();
            System.out.println("GET Non-existent Response: " + in.readLine()); // Expecting $-1

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}