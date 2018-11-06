import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.*;

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
            return empty();

        String fileName = fileNamePath.toString();

        if (fileName.endsWith("html"))
            return of(ContentType.HTML);
        else if (fileName.endsWith("txt"))
            return of(ContentType.TEXT);
        else if (fileName.endsWith("gif"))
            return of(ContentType.GIF);
        else
            return empty();
    }

    @Override
    public String toString() {
        return description;
    }
}
