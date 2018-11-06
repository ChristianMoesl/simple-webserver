
enum class RequestType {
    GET,
    POST;

    companion object {

        internal fun forString(string: String): RequestType? {
            return when (string) {
                "GET" -> RequestType.GET
                "POST" -> RequestType.POST
                else -> null
            }
        }
    }
}
