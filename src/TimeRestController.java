

public class TimeRestController extends Controller {
    @Override
    public String getPath() {
        return "/time";
    }

    @Override
    public void get(Request request, Response response) {
        response.setStatus(StatusCode.OK);
        response.setContentType(ContentType.TEXT);
        response.setContent(Long.toString(System.currentTimeMillis()).getBytes());
    }
}
