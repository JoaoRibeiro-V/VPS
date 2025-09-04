import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class Server {
    private static List<String> messages = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/message", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
                reader.close();

                String msg = body.toString();
                messages.add(msg);
                System.out.println(msg + exchange.getLocalAddress().toString()); // print to server console

                String response = "Message received!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });

        server.createContext("/fetch", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                StringBuilder response = new StringBuilder();
                synchronized (messages) {
                    for (String msg : messages) {
                        response.append(msg).append("\n");
                    }
                }
                exchange.sendResponseHeaders(200, response.toString().getBytes("UTF-8").length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.toString().getBytes("UTF-8"));
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Broadcast server running on port " + port);
    }
}