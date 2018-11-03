import javax.swing.text.html.Option;
import java.nio.file.Path;
import java.util.Optional;

public enum ContentType {
    HTML("text/html"),
    TEXT("text/plain"),
    GIF("image/gif");

    private final String description;

    ContentType(String description) {
        this.description = description;
    }

    static Optional<ContentType> forPath(Path path) {
        Path fileNamePath = path.getFileName();

        if (fileNamePath == null)
            return Optional.empty();

        String fileName = fileNamePath.toString();

        if (fileName.endsWith("html"))
            return Optional.of(ContentType.HTML);
        else if (fileName.endsWith("txt"))
            return Optional.of(ContentType.TEXT);
        else if (fileName.endsWith("gif"))
            return Optional.of(ContentType.GIF);
        else
            return Optional.empty();
    }

    @Override
    public String toString() {
        return description;
    }
}
