package uz.app.iTask.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//@WebServlet(value = "/check")
public class CheckServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        //System.out.println("Check requested");
        //PrintWriter out = res.getWriter();
        //out.print("successful");
        /*try{
            throw new IOException("");
        } catch (java.io.IOException ex){
            res.sendError(500, "ERROR");
        }*/
    }

}
