import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run(8080);

            System.out.println("♕ 240 Chess Server");
        } catch (Exception ex) {
            System.out.println("Unable to start server: " + ex);
        }
    }
}