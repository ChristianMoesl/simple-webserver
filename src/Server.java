import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server {

    private final InetSocketAddress endPoint;
    private final Pattern requestPattern = Pattern.compile("([A-Z]+) (/[a-zA-Z0-9]*) HTTP/1.1");

    private ServerSocket socket;
    private Hashtable<String, RestController> controllers = new Hashtable<>();
    private Optional<String> response = Optional.empty();

    public Server(String hostname, int port) {
        endPoint = new InetSocketAddress(hostname, port);
    }

    public void register(RestController controller) {
        controllers.put(controller.getPath(), controller);
    }

    private Optional<RestController> findForPath(String path) {
        return Optional.of(controllers.getOrDefault(path, null));
    }

    private void parseRequest(List<String> request) {
        if (request.size() > 0) {
            String line = request.get(0);
            Matcher matcher = requestPattern.matcher(line);
            if (matcher.matches()) {
                String requestType = matcher.group(1);
                String path = matcher.group(2);

                System.out.println("Incoming Request: " +requestType + " "+ path);

                findForPath(path).ifPresentOrElse(controller -> {
                    response = Optional.of(controller.get());

                    System.out.println("Sending html response");
                }, () -> {
                    response = Optional.of("Error");
                });
            } else {
                System.out.println("Request  \"" + line + "\" not supported");
            }
        }
    }

    private String buildHeader(String forResponse) {
        return new StringBuilder()
                .append("HTTP/1.1 200 OK\n")
                .append("Content-Length: ").append(response.get().length()).append('\n')
                .append("Content-Type: text/html\n\n")
                .toString();
    }


    private List<String> receiveRequest(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            List<String> lines = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null && !line.isEmpty()) {
                lines.add(line);
                System.out.println(line);
            }

            return lines;
        } catch (IOException exception) {
            return Collections.emptyList();
        }
    }

    private void sendResponse(OutputStream outputStream) {
        PrintWriter writer = new PrintWriter(outputStream);
        writer.print(buildHeader(response.get()));
        writer.print(response.get());
        writer.flush();
    }

    public void run() throws IOException {
        socket = new ServerSocket();
        socket.bind(endPoint, 100);

        while (true) {
            try (Socket activeSocket = socket.accept()) {

                while (activeSocket.isClosed()) {
                    List<String> request = receiveRequest(activeSocket.getInputStream());

                    parseRequest(request);

                    if (response.isPresent()) {
                        sendResponse(activeSocket.getOutputStream());

                        response = Optional.empty();
                    }
                }
            }
        }
    }
}
