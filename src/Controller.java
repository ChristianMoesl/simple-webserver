
public abstract class Controller {

    public void get(Request request, Response response) {
        response.setStatus(StatusCode.ERROR_NOT_FOUND);
    }

    public void post(Request request, Response response) {
        response.setStatus(StatusCode.ERROR_NOT_FOUND);
    }

    public abstract String getPath();
}