package uz.app.Anno;


import uz.app.Anno.Util.Anno;
import uz.app.Anno.Util.HttpMethod;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name="AnnoMainServlet", value = "/*")
public class MainServlet extends HttpServlet {

    static ServletContext context;

    @Override
    public void init() throws ServletException {
        if(!Global.isInit)
        {
            try {
                Global.Init(getServletContext());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new ServletException(ex.getCause());
            }

            MainServlet.context = getServletContext();
        }

        System.out.println("Main_servlet initialized");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod().trim().toUpperCase();
        /*System.out.println("getServletPath = "+ req.getServletPath());
        System.out.println("getPathInfo = "+ req.getPathInfo());
        System.out.println("getPathTranslated = "+ req.getPathTranslated());
        System.out.println("getContextPath = "+ req.getContextPath());
        System.out.println("getRequestURI = "+ req.getRequestURI());
        System.out.println("getRequestURL = "+ req.getRequestURL().toString());*/
        System.out.println(HttpMethod.valueOf(HttpMethod.class, method) + " " + req.getRequestURI());
        String contextPath = req.getContextPath();
        String requestUri = req.getRequestURI();
        if(contextPath != null && contextPath.length() > 0)
            requestUri = requestUri.substring(contextPath.length(), requestUri.length());   // parse request path relative to application
        RouteProcessingService.process(requestUri, HttpMethod.valueOf(HttpMethod.class, method), req, resp);
    }

    public static ServletContext getContext()
    {
        return context;
    }
}
