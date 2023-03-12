import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Client {
    public static String search() throws IOException {
        System.out.print("Inserisci il libro da cercare: ");
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        return read.readLine();
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket server = new Socket(InetAddress.getLocalHost(), 8888);
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        PrintWriter out = new PrintWriter(server.getOutputStream(), true);
        out.println(search());
    }
}
