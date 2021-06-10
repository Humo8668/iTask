package uz.app.iTask.Services;

import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import uz.app.Anno.Annotations.Module;
import uz.app.Anno.Annotations.Route;
import uz.app.Anno.BaseModule;
import uz.app.Anno.Util.HttpMethod;
import uz.app.iTask.Models.User;
import uz.app.iTask.Util.Setup;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//  for all paths beginning "/user"
@Module("User")
public class UsersService extends BaseModule {
    static final Logger log = LoggerFactory.getLogger(Setup.class);

    // GET /getall
    @Route(value = "/getall", method = HttpMethod.GET)
    void getAll(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        PrintWriter out = res.getWriter();

        Gson gson = new Gson();
        User[] users;
        try {
            users = Setup.userRepo.getAll();
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
