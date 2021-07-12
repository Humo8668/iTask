package uz.app.iTask.Services;

import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import uz.app.Anno.Annotations.Module;
import uz.app.Anno.Annotations.Route;
import uz.app.Anno.BaseModule;
import uz.app.Anno.Exceptions.*;
import uz.app.Anno.Util.HttpMethod;
import uz.app.iTask.Models.User;
import uz.app.iTask.Util.StdResp;
import uz.app.iTask.Util.Setup;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//  for all paths beginning "/user"
@Module("User")
public class UsersService extends BaseModule {
    static final Logger log = LoggerFactory.getLogger(Setup.class);

    @Route(value = "/getall", method = HttpMethod.GET)
    void getAll(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        PrintWriter out = res.getWriter();

        Gson gson = new Gson();
        User[] users;
        try {
            users = Setup.userRepo.getAll();
        } catch (SQLException ex) {
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

    @Route(value = "/getbyid", method = HttpMethod.GET)
    void getById(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        PrintWriter out = res.getWriter();

        long id = Long.parseLong(req.getParameter("id"));
        Gson gson = new Gson();
        User user;
        try {
            user = Setup.userRepo.getById(id);
        } catch (Exception ex) {
            log.error("Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
            res.sendError(500, "Error occurred: " + ex.getMessage());
            return;
        }

        String json = gson.toJson(user);
        res.setContentType("application/json");
        res.setStatus(200);
        out.print(json);
    }

    @Route(value = "/create", method = HttpMethod.POST)
    void create(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        PrintWriter out = res.getWriter();
        Gson gson = new Gson();

        String reqBody = this.getReqBody(req);
        User u = gson.fromJson(reqBody, User.class);
        StdResp stdResp = new StdResp();

        try {
            Setup.userRepo.save(u);
            stdResp.errorCode = "0";
            stdResp.errorText = "Success";
        } catch (SQLException ex) {
            log.error("Error: " + ex.getMessage() + "\n");
            res.sendError(500, "Error occurred: " + ex.getMessage());
            return;
        } catch (AnnoValidationException ex) {
            stdResp.errorCode = ex.errorCode;
            stdResp.errorText = ex.getMessage();
        }
        out.print(gson.toJson(stdResp));
        res.setContentType("application/json");
        res.setStatus(200);
    }

    @Route(value = "/del", method = HttpMethod.DELETE)
    void delete(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException
    {
        PrintWriter out = res.getWriter();

        Gson gson = new Gson();
        String reqBody = this.getReqBody(req);
        Map jsonMap = gson.fromJson(reqBody, Map.class);
        StdResp stdResp = new StdResp();
        if(!jsonMap.containsKey("user_id")) {
            stdResp.errorCode = "1";
            stdResp.errorText = "Necessary parameter does not exist.";
            //System.out.println("Necessary parameter does not exist.");
        }
        else {
            try {
                Long userId = Double.valueOf(jsonMap.get("user_id").toString()).longValue();
                Setup.userRepo.delete(userId);
                stdResp.errorCode = "0";
                stdResp.errorText = "Success";
            } catch (NumberFormatException ex) {
                stdResp.errorCode = "01000";
                stdResp.errorText = "Number format exception: " + ex.getMessage();
            } catch (SQLException ex) {
                ex.printStackTrace();
                //System.out.println(ex.getMessage());
                res.sendError(500, "Error occurred: " + ex.getMessage());
                return;
            }
        }
        out.print(gson.toJson(stdResp));
        res.setContentType("application/json");
        res.setStatus(200);
    }

    @Route(value = "/count", method = HttpMethod.GET)
    void count(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        PrintWriter out = res.getWriter();
        Gson gson = new Gson();

        try {
            Long count = Setup.userRepo.count();
            HashMap<String, String> map = new HashMap<>();
            map.put("count", count.toString());
            String resJson = gson.toJson(map, Map.class);
            out.print(resJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            res.sendError(500, "Error occurred: " + ex.getMessage());
            return;
        }

        res.setContentType("application/json");
        res.setStatus(200);
    }
}
