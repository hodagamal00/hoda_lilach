package il.OCSFMediatorExample.server;

public class App {
    public static void main(String[] args) {
        SimpleServer server = new SimpleServer(3000);
        try {
            server.listen();
            System.out.println("Server is running on port 3000...");
        } catch (Exception e) {
            System.err.println("Failed to start server!");
            e.printStackTrace();
        }
    }
}
