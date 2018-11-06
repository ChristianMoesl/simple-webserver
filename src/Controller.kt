abstract class Controller {
    abstract val path: String

    open operator fun get(request: Request, response: Response) {
        response.status = StatusCode.ERROR_NOT_FOUND
    }

    open fun post(request: Request, response: Response) {
        response.status = StatusCode.ERROR_NOT_FOUND
    }
}