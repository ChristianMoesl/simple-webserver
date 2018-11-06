import javax.swing.text.AbstractDocument;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Server {

    private final InetSocketAddress endPoint;
    private final Pattern requestPattern = Pattern.compile("([A-Z]+) (/[a-zA-Z0-9.]*) HTTP/1.1");

    private final String webFolder;
    private ServerSocket socket;
    private Hashtable<String, Controller> controllers = new Hashtable<>();
    private Optional<Response> response = Optional.empty();

    public Server(String hostname, int port, String webFolder) {
        this.webFolder = webFolder;
        endPoint = new InetSocketAddress(hostname, port);
    }

    public void register(Controller controller) {
        controllers.put(controller.getPath(), controller);
    }

    private Optional<Controller> findForPath(String path) {
        return Optional.ofNullable(controllers.getOrDefault(path, null));
    }

    private boolean serveFilesFor(String path, RequestType requestType) {
        if (requestType != RequestType.GET) {
            response = Optional.of(new Response());
            return true;
        }

        String searchPath = webFolder + path;

        Path file;
        if (path.charAt(path.length() - 1) == '/')
            file = Paths.get(searchPath + "index.html");
        else
            file = Paths.get(searchPath);

        if (!Files.exists(file))
            return false;

        ContentType.forPath(file).ifPresentOrElse(contentType -> {
            System.out.println("Serve static file " + file);
            try {
                Response resp = new Response();
                resp.setStatus(StatusCode.OK);
                resp.setContentType(contentType);
                resp.setContent(Files.readAllBytes(file));
                response = Optional.of(resp);
            } catch (IOException e) {
                response = Optional.empty();
            }
        },() -> response = Optional.empty());

        return response.isPresent();
    }

    private boolean executeControllerFor(String path, RequestType requestType) {
        Optional<Controller> controller = findForPath(path);
        boolean served = controller.isPresent();

        controller.ifPresentOrElse(c -> {
            Response resp = new Response();
            System.out.println("Executing controller \"" + c.getClass().getSimpleName() + "\" for request");
            c.get(new Request(requestType, path), resp);
            response = Optional.of(resp);
        }, () -> response = Optional.empty());

        return served;
    }

    private void serveNotFoundFor(String path, RequestType requestType) {
        response = Optional.of(new Response());
    }

    private void parseRequest(List<String> request) {
        if (request.size() > 0) {
            String line = request.get(0);
            Matcher matcher = requestPattern.matcher(line);
            if (matcher.matches()) {
                Optional<RequestType> requestType = RequestType.forString(matcher.group(1));
                String path = matcher.group(2);

                requestType.ifPresent(rqType -> {
                    System.out.println("Incoming Request: " + rqType + " "+ path);

                    if (!serveFilesFor(path, rqType))
                        if (!executeControllerFor(path, rqType))
                            serveNotFoundFor(path, rqType);
                });
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
            }

            return lines;
        } catch (IOException exception) {
            return Collections.emptyList();
        }
    }

    private void sendResponse(OutputStream outputStream) {
        try {
            outputStream.write(response.get().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        socket = new ServerSocket();
        socket.bind(endPoint, 100);

        while (true) {
            try (Socket activeSocket = socket.accept()) {
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
