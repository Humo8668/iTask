package uz.app.Anno;


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


    public static void Init() { }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getPathInfo());
        RouteProcessingService.process(req.getPathInfo(), HttpMethod.GET, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getPathInfo());
        RouteProcessingService.process(req.getPathInfo(), HttpMethod.POST, req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getPathInfo());
        RouteProcessingService.process(req.getPathInfo(), HttpMethod.PUT, req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getPathInfo());
        RouteProcessingService.process(req.getPathInfo(), HttpMethod.DELETE, req, resp);
    }
}
