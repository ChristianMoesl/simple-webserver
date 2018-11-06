import java.nio.file.Path;

public class Request {
    private Path path;
    private RequestType type;

    public Request(RequestType type, Path path) {
        this.type = type;
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public RequestType getType() {
        return type;
    }
}
