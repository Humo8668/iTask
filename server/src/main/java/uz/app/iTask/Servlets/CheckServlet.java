package uz.app.iTask.Servlets;

import uz.app.Anno.BaseServlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(value = "/check")
public class CheckServlet extends BaseServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        PrintWriter out = res.getWriter();
        out.print("successful");
        /*try{
            throw new IOException("");
        } catch (java.io.IOException ex){
            res.sendError(500, "ERROR");
        }*/
    }

}
