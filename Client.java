import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static boolean isLogado = true;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
	System.out.println();
	System.out.println("Bem vindo ao simulador P2P, envie EXIT a qualquer momento para desconectar: ");
	System.out.println();

        System.out.print("Digite seu nickname: ");
        String username = scanner.nextLine();

	System.out.println();
	System.out.println("Bem vindo, converse com usuarios logados no chat:");
        String postUrl = "https://friendly-pancake-pj9g79549xpw367w6-8080.app.github.dev/message";
        String fetchUrl = "https://friendly-pancake-pj9g79549xpw367w6-8080.app.github.dev/fetch";

        new Thread(() -> {
            Set<String> seen = new HashSet<>();
            while (true) {
		if(isLogado != true){break;}
                try {
                    URL url = URI.create(fetchUrl).toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (!seen.contains(line) && !line.trim().isEmpty()) {
                            System.out.println(line);
                            seen.add(line);
                        }
                    }
                    in.close();
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
            String msg = scanner.nextLine();
            if ("exit".equalsIgnoreCase(msg)){
		isLogado = false;
		break;
		};
            try {
                URL url = URI.create(postUrl).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String formatted = username + ": " + msg;
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(formatted.getBytes("UTF-8"));
                }

                conn.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}