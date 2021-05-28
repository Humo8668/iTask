package uz.app.iTask.Servlets;

import com.google.gson.Gson;
import uz.app.iTask.Global;
import uz.app.iTask.Models.BaseEntity;
import uz.app.iTask.Models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="Users", value = "/user")
public class UsersServlet extends BaseServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        PrintWriter out = res.getWriter();

        Gson gson = new Gson();
        User[] users = new User[0];
        try {
            users = Global.userRepo.getAll();
        } catch (Exception ex) {
            log.error("Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
            res.sendError(500, "Error occurred: " + ex.getMessage());
            return;
        }

        String json = gson.toJson(users);
        res.setContentType("application/json");
        res.setStatus(200);
        out.print(json);
        return;
    }

}