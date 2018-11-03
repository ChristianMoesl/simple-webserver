import java.nio.file.Path;
import java.util.Optional;

public enum RequestType {
    GET,
    POST;

    static Optional<RequestType> forString(String string) {
        if (string.equals("GET"))
            return Optional.of(RequestType.GET);
        else if (string.equals("POST"))
            return Optional.of(RequestType.POST);
        else
            return Optional.empty();
    }
}
