import javax.swing.text.html.Option;
import java.nio.file.Path;
import java.util.Optional;

public enum ContentType {
    HTML("text/html"),
    GIF("image/gif");

    private final String description;

    ContentType(String description) {
        this.description = description;
    }

    static Optional<ContentType> forPath(Path path) {
        if (path.endsWith("html"))
            return Optional.of(ContentType.HTML);
        else if (path.endsWith("gif"))
            return Optional.of(ContentType.GIF);
        else
            return Optional.empty();
    }

    @Override
    public String toString() {
        return description;
    }
}
