
public abstract class Controller {

    private void setNotFound(Response response) {
        response.setStatus(StatusCode.ERROR_NOT_FOUND);
        response.setContentType(ContentType.HTML);
        response.setContent(Response.generateErrorPage(StatusCode.ERROR_NOT_FOUND.code, StatusCode.ERROR_NOT_FOUND.message));
    }

    public void get(Request request, Response response) {
        setNotFound(response);
    }

    public void post(Request request, Response response) {
        setNotFound(response);
    }

    public abstract String getPath();
}