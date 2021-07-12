package uz.app.Anno;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

public abstract class BaseModule {
    protected String moduleName;

    public BaseModule()
    {
        try{
            RouteProcessingService.AddModule(this);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        init();
    }

    public void init()
    {
        System.out.println("Module <" + this.moduleName +  "> initialized");
        return;
    }

    public String getReqBody(HttpServletRequest req) throws IOException
    {
        return req.getReader().lines().collect(Collectors.joining());
    }

    public ServletContext getContext()
    {
        return MainServlet.getContext();
    }

    @Override
    public int hashCode() {
        return moduleName.hashCode();
    }
}
