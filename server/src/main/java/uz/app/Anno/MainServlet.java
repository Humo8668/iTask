package uz.app.Anno;


import uz.app.Anno.Util.Anno;
import uz.app.Anno.Util.HttpMethod;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name="AnnoMainServlet", value = "/*")
public class MainServlet extends HttpServlet {

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
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod().trim().toUpperCase();
        System.out.println(HttpMethod.valueOf(HttpMethod.class, method) + " " + req.getRequestURI());
        RouteProcessingService.process(req.getRequestURI(), HttpMethod.valueOf(HttpMethod.class, method), req, resp);
    }
}
