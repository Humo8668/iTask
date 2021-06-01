package uz.app.iTask.Servlets;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.app.Anno.BaseServlet;
import uz.app.iTask.Models.User;
import uz.app.iTask.Util.Setup;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="Users", value = "/user")
public class UsersServlet extends BaseServlet {

    static final Logger log = LoggerFactory.getLogger(UsersServlet.class);
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        PrintWriter out = res.getWriter();


        Gson gson = new Gson();
        User[] users = new User[0];
        try {
            users = Setup.userRepo.getAll();
            //Thread.sleep(10000);
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
