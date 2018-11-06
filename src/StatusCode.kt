import java.util.Optional

enum class StatusCode private constructor(internal val code: Int, internal val message: String) {
    OK(200, "OK"),
    ERROR_BAD_REQUEST(400, "Bad Request"),
    ERROR_NOT_FOUND(404, "Not Found");

    val isGood: Boolean
        get() = code == StatusCode.OK.code

    override fun toString(): String {
        return Integer.toString(code) + " " + message
    }

    companion object {

        internal fun parse(text: String): Optional<StatusCode> {
            return if (text == "200")
                Optional.of(StatusCode.OK)
            else if (text == "400")
                Optional.of(StatusCode.ERROR_BAD_REQUEST)
            else if (text == "404")
                Optional.of(StatusCode.ERROR_NOT_FOUND)
            else
                Optional.empty()
        }
    }
}
