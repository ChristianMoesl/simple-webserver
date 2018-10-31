import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("usage: <domain> <port> <web-folder>");
            System.exit(-1);
        }

        Server server = new Server(args[0], Integer.valueOf(args[1]), args[2]);
        server.register(new RootSite());
        server.run();
    }
}
