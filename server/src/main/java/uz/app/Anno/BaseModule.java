package uz.app.Anno;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

public abstract class BaseModule {
    protected String moduleName;

    public String getReqBody(HttpServletRequest req) throws IOException
    {
        return req.getReader().lines().collect(Collectors.joining());
    }

    @Override
    public int hashCode() {
        return moduleName.hashCode();
    }
}
