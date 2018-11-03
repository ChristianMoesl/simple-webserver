import javax.swing.text.AbstractDocument;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Response {
    private StatusCode status = StatusCode.ERROR_NOT_FOUND;
    private ContentType type = ContentType.HTML;
    private byte[] content = ("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
            "<html>\n" +
            "<head>\n" +
            "   <title>404 Not Found</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "   <h1>Not Found</h1>\n" +
            "   <p>The requested URL was not found on this server.</p>\n" +
            "</body>\n" +
            "</html>").getBytes();

    public Response() {}

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
        String header = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(status.toString())
                .append("\nContent-Length: ").append(content.length).append('\n')
                .append("Content-Type: ")
                .append(type.toString())
                .append("\nConnection: close")
                .append("\n\n")
                .toString();

        ByteBuffer buffer = ByteBuffer.allocate(header.getBytes().length + content.length);

        buffer.put(header.getBytes());
        buffer.put(content);

        return buffer.array();
    }
}
