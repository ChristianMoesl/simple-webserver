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

import static java.util.Optional.*;


public class Server {

    private final InetSocketAddress endPoint;
    private final Pattern requestPattern = Pattern.compile("([A-Z]+) (/[a-zA-Z0-9//.]*) HTTP/1.1");

    private final String webFolder;
    private ServerSocket socket;
    private Hashtable<String, Controller> controllers = new Hashtable<>();
    private Optional<Response> response = empty();

    public Server(String hostname, int port, String webFolder) {
        this.webFolder = webFolder;
        endPoint = new InetSocketAddress(hostname, port);
    }

    public void register(Controller controller) {
        controllers.put(controller.getPath(), controller);
    }

    private Optional<Controller> findForPath(Path path) {
        return ofNullable(controllers.getOrDefault(path.toString(), null));
    }

    private void serveFilesFor(Path path, RequestType requestType) {
        // Allow only get requests for static files
        if (requestType != RequestType.GET) {
            response = of(Response.ERROR_METHOD_NOT_ALLOWED);
            return;
        }

        String searchPath = webFolder + path;

        // Calculate path for file to be served
        Path file;
        if (Files.isDirectory(path))
            file = Paths.get(searchPath + "index.html");
        else
            file = Paths.get(searchPath);

        if (!Files.exists(file))
            return;

        ContentType.forPath(file).ifPresentOrElse(contentType -> {
            System.out.println("Serve static file " + file);
            try {
                Response resp = new Response();
                resp.setContentType(contentType);
                resp.setContent(Files.readAllBytes(file));
                response = of(resp);
            } catch (IOException e) {
                response = of(Response.ERROR_NOT_FOUND);
            }
        },() -> response = of(Response.ERROR_NOT_FOUND));
    }

    private void executeControllerFor(Path path, RequestType requestType) {
        Optional<Controller> controller = findForPath(path);

        controller.ifPresent(c -> {
            Response resp = new Response();
            System.out.println("Executing controller \"" + c.getClass().getSimpleName() + "\" for request");

            if (requestType == RequestType.GET)
                c.get(new Request(requestType, path), resp);
            else if (requestType == RequestType.POST)
                c.post(new Request(requestType, path), resp);
            else
                resp = Response.ERROR_METHOD_NOT_ALLOWED;

            response = of(resp);
        });
    }

    private void serveNotFoundFor(Path path, RequestType requestType) {
        response = of(Response.ERROR_NOT_FOUND);
    }

    private Optional<Request> parseRequest(List<String> request) {
        Optional result = empty();

        if (request.size() > 0) {
            String line = request.get(0);

            Matcher matcher = requestPattern.matcher(line);

            if (matcher.matches()) {
                Optional<RequestType> requestType = RequestType.forString(matcher.group(1));
                Path path = Paths.get(matcher.group(2)).normalize();

                if (requestType.isPresent()) {
                    System.out.println("Incoming Request: " + requestType.get() + " "+ path);

                    result = of(new Request(requestType.get(), path));
                }
            }
        }

        return result;
    }

    private void processRequest(Request request) {
        serveFilesFor(request.getPath(), request.getType());

        if (!response.isPresent())
            executeControllerFor(request.getPath(), request.getType());

        if (!response.isPresent())
            serveNotFoundFor(request.getPath(), request.getType());
    }

    private List<String> receiveRequest(InputStream inputStream) {
        List<String> lines = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line = br.readLine();

            while (line != null && !line.isEmpty()) {
                lines.add(line);

                line = br.readLine();
            }

            return lines;
        } catch (IOException exception) {
            return lines;
        }
    }

    private void sendResponse(OutputStream outputStream, Response response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public void run() {
        try {
            socket = new ServerSocket();
            socket.bind(endPoint, 100);
        } catch (IOException e) {
            System.err.println("Cannot bind to endpoint: " + endPoint.getHostName() + ":" + endPoint.getPort());
            System.exit(-1);
        }

        while (true) {
            try (Socket activeSocket = socket.accept()) {
                List<String> request = receiveRequest(activeSocket.getInputStream());

                parseRequest(request).ifPresentOrElse(this::processRequest, () -> {
                    response = of(Response.ERROR_BAD_REQUEST);
                });

                if (response.isPresent()) {
                    sendResponse(activeSocket.getOutputStream(), response.get());

                    response = empty();
                }
            } catch (IOException e) {
                System.err.println("Failed to handle connection");
            }
        }
    }
}
