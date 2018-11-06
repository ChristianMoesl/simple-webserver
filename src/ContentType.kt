import java.nio.file.Path

enum class ContentType constructor(private val description: String) {
    HTML("text/html"),
    TEXT("text/plain"),
    GIF("image/gif");

    override fun toString(): String {
        return description
    }

    companion object {

        internal fun forPath(path: Path): ContentType? {
            val fileNamePath = path.fileName ?: return null

            val fileName = fileNamePath.toString()

            return when {
                fileName.endsWith("html") -> ContentType.HTML
                fileName.endsWith("txt") -> ContentType.TEXT
                fileName.endsWith("gif") -> ContentType.GIF
                else -> null
            }
        }
    }
}
