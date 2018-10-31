import java.util.Optional;

public enum StatusCode {
    OK(200, "OK"),
    ERROR_BAD_REQUEST(400, "Bad Request"),
    ERROR_NOT_FOUND(404, "Not Found");

    final int code;
    final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    static Optional<StatusCode> parse(String text) {
        if (text.equals("200"))
            return Optional.of(StatusCode.OK);
        else if (text.equals("400"))
            return Optional.of(StatusCode.ERROR_BAD_REQUEST);
        else if (text.equals("404"))
            return Optional.of(StatusCode.ERROR_NOT_FOUND);
        else
            return Optional.empty();
    }

    public boolean isGood() {
        return code == StatusCode.OK.code;
    }

    @Override
    public String toString() {
        return Integer.toString(code) + " " + message;
    }
}
