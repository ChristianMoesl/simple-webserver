import javax.swing.text.AbstractDocument;
import java.nio.charset.StandardCharsets;

public class Response {
    private StatusCode status = StatusCode.ERROR_NOT_FOUND;
    private ContentType type = ContentType.HTML;
    private String htmlText = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
            "<html>\n" +
            "<head>\n" +
            "   <title>404 Not Found</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "   <h1>Not Found</h1>\n" +
            "   <p>The requested URL was not found on this server.</p>\n" +
            "</body>\n" +
            "</html>";
    private byte[] content;

    public Response() {}

    public Response(StatusCode statu, String htmlText) {
        this.status = status;
        this.htmlText = htmlText;
    }

    public void setStatus(StatusCode code) {
        status = code;
    }

    public void setContentType(ContentType type) {
        this.type = type;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        if (type != ContentType.HTML)
            htmlText = new String(content, StandardCharsets.UTF_8);

        return new StringBuilder()
                .append("HTTP/1.1 ")
                .append(status.toString())
                .append("\nContent-Length: ").append(htmlText.length()).append('\n')
                .append("Content-Type: ")
                .append(type.toString())
                .append(" \n\n")
                .append(htmlText)
                .toString();
    }
}
