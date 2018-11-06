import java.nio.ByteBuffer

class Response {
    var status = StatusCode.ERROR_NOT_FOUND
    var contentType = ContentType.HTML
    var content = ("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
            "<html>\n" +
            "<head>\n" +
            "   <title>404 Not Found</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "   <h1>Not Found</h1>\n" +
            "   <p>The requested URL was not found on this server.</p>\n" +
            "</body>\n" +
            "</html>").toByteArray()

    val bytes: ByteArray
        get() {
            val header = StringBuilder()
                    .append("HTTP/1.1 ")
                    .append(status.toString())
                    .append("\nContent-Length: ").append(content.size).append('\n')
                    .append("Content-Type: ")
                    .append(contentType.toString())
                    .append("\nConnection: close")
                    .append("\n\n")
                    .toString()

            val buffer = ByteBuffer.allocate(header.toByteArray().size + content.size)

            buffer.put(header.toByteArray())
            buffer.put(content)

            return buffer.array()
        }
}
