class TimeRestController : Controller() {
    override val path: String
        get() = "/time"

    override fun get(request: Request, response: Response) {
        response.status = StatusCode.OK
        response.contentType = ContentType.TEXT
        response.content = System.currentTimeMillis().toString().toByteArray()
    }
}
