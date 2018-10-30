

public class RootSite implements RestController {
    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public String get() {
        return "<!DOCTYPE html><html><body><h1>Hello World</h1></body></html>";
    }
}
