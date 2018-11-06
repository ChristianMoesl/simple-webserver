import java.util.Optional;
import static java.util.Optional.*;

public enum RequestType {
    GET,
    POST;

    static Optional<RequestType> forString(String string) {
        if (string.equals("GET"))
            return of(RequestType.GET);
        else if (string.equals("POST"))
            return of(RequestType.POST);
        else
            return empty();
    }
}
