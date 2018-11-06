
public enum StatusCode {
    OK(200, "OK"),
    ERROR_BAD_REQUEST(400, "Bad Request"),
    ERROR_NOT_FOUND(404, "Not Found"),
    ERROR_METHOD_NOT_ALLOWED(405, "Method Not Allowed");

    final int code;
    final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return Integer.toString(code) + " " + message;
    }
}
