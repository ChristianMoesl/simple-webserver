import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server {

    private final InetSocketAddress endPoint;
    private final Pattern requestPattern = Pattern.compile("([A-Z]+) (/[a-zA-Z0-9]*) HTTP/1.1");

    private final String webFolder;
    private ServerSocket socket;
    private Hashtable<String, Controller> controllers = new Hashtable<>();
    private Response response = new Response();

    public Server(String hostname, int port, String webFolder) {
        this.webFolder = webFolder;
        endPoint = new InetSocketAddress(hostname, port);
    }

    public void register(Controller controller) {
        controllers.put(controller.getPath(), controller);
    }

    private Optional<Controller> findForPath(String path) {
        return Optional.of(controllers.getOrDefault(path, null));
    }

    private boolean serveFilesFor(String path) {
        String searchPath = webFolder + path;

        // look into directory
        if (path.charAt(path.length() - 1) == '/') {
            Path index = Paths.get(searchPath + "index.html");
            if (Files.exists(index)) {
                try {
                    response = new Response(StatusCode.OK, Files.lines(index).toString());
                } catch (IOException e) {
                    response = new Response();
                }
                return true;
            }
        } else {
            Path filePath = Paths.get(searchPath);

            if (Files.exists(filePath)) {
                ContentType.forPath(filePath).ifPresentOrElse(contentType -> {
                    try {
                        response = new Response();
                        response.setStatus(StatusCode.OK);
                        response.setContentType(contentType);
                        response.setContent(Files.readAllBytes(filePath));
                    } catch (IOException e) {
                        response = new Response();
                    }
                },() -> {
                    });


                return true;
            }
        }

        return false;
    }

    private boolean executeControllerFor(String path) {
        Optional<Controller> controller = findForPath(path);
        boolean served = controller.isPresent();

        controller.ifPresentOrElse(c -> {
            Response resp = new Response();
            c.get(new Request(RequestType.GET, path), resp);
            response = Optional.of(resp.toString());

            System.out.println(response.get());

            System.out.println("Sending html response");
        }, () -> {
            response = Optional.of("Error");
        });

        return served;
    }

    private void serveNotFoundFor(String path) {
        response = Optional.of(new Response().toString());
    }

    private void parseRequest(List<String> request) {
        if (request.size() > 0) {
            String line = request.get(0);
            Matcher matcher = requestPattern.matcher(line);
            if (matcher.matches()) {
                String requestType = matcher.group(1);
                String path = matcher.group(2);

                System.out.println("Incoming Request: " +requestType + " "+ path);

                if (!serveFilesFor(path))
                    if (!executeControllerFor(path))
                        serveNotFoundFor(path);
            } else {
                System.out.println("Request  \"" + line + "\" not supported");
            }
        }
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
        writer.print(response.get());
        writer.flush();
    }

    public void run() throws IOException {
        socket = new ServerSocket();
        socket.bind(endPoint, 100);

        while (true) {
            try (Socket activeSocket = socket.accept()) {
                while (!activeSocket.isClosed()) {
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
