

public class Request {
    private String path;
    private RequestType type;

    public Request(RequestType type, String path) {
        this.type = type;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public RequestType getType() {
        return type;
    }
}
