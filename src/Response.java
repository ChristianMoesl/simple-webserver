import java.nio.ByteBuffer;

public class Response {
    private StatusCode status = StatusCode.ERROR_NOT_FOUND;
    private ContentType type = ContentType.TEXT;
    private byte[] content = new byte[0];

    public static final Response ERROR_BAD_REQUEST;
    public static final Response ERROR_NOT_FOUND;
    public static final Response ERROR_METHOD_NOT_ALLOWED;

    public static byte[] generateErrorPage(int code, String msg) {
        return ("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
                "<html>\n" +
                "<head>\n" +
                "   <title>" + Integer.toString(code) + " " + msg + "</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "   <h1>" + msg + "</h1>\n" +
                "</body>\n" +
                "</html>").getBytes();
    }

    static {
        ERROR_BAD_REQUEST = new Response();
        ERROR_BAD_REQUEST.setStatus(StatusCode.ERROR_BAD_REQUEST);
        ERROR_BAD_REQUEST.setContentType(ContentType.HTML);
        ERROR_BAD_REQUEST.setContent(generateErrorPage(StatusCode.ERROR_BAD_REQUEST.code, StatusCode.ERROR_BAD_REQUEST.message));

        ERROR_NOT_FOUND = new Response();
        ERROR_NOT_FOUND.setStatus(StatusCode.ERROR_NOT_FOUND);
        ERROR_NOT_FOUND.setContentType(ContentType.HTML);
        ERROR_NOT_FOUND.setContent(generateErrorPage(StatusCode.ERROR_NOT_FOUND.code, StatusCode.ERROR_NOT_FOUND.message));

        ERROR_METHOD_NOT_ALLOWED = new Response();
        ERROR_METHOD_NOT_ALLOWED.setStatus(StatusCode.ERROR_METHOD_NOT_ALLOWED);
        ERROR_METHOD_NOT_ALLOWED.setContentType(ContentType.HTML);
        ERROR_METHOD_NOT_ALLOWED.setContent(generateErrorPage(StatusCode.ERROR_METHOD_NOT_ALLOWED.code, StatusCode.ERROR_METHOD_NOT_ALLOWED.message));
    }

    public Response() { }

    public void setStatus(StatusCode code) {
        status = code;
    }

    public void setContentType(ContentType type) {
        this.type = type;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getBytes() {
        StringBuilder builder = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(status.toString())
                .append("\nContent-Length: ")
                .append(content.length)
                .append('\n');

        if (content.length > 0) {
            builder.append("Content-Type: ")
                   .append(type.toString())
                   .append('\n');
        }

        String header = builder.append("Connection: close")
                               .append("\n\n")
                               .toString();

        ByteBuffer buffer = ByteBuffer.allocate(header.getBytes().length + content.length);

        buffer.put(header.getBytes());
        buffer.put(content);

        return buffer.array();
    }
}
