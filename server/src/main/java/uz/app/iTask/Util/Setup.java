package uz.app.iTask.Util;


import org.slf4j.*;
import uz.app.iTask.Repositories.*;
import uz.app.iTask.Services.UsersService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Setup implements ServletContextListener {

    public static UserRepo userRepo;
    public static TaskRepo taskRepo;
    public static RoleRepo roleRepo;
    public static DeskRepo deskRepo;
    public static ActionLogRepo actionLogRepo;

    static final Logger log = LoggerFactory.getLogger(Setup.class);

    public void contextInitialized(ServletContextEvent event) throws RuntimeException {
        // Do your thing during webapp's startup.
        //log.warn("contextInitialized");

        //System.out.println(((Class<User>)User.class).getAnnotation(Schema.class).value());
        try {
            userRepo = new UserRepo();
            taskRepo = new TaskRepo();
            roleRepo = new RoleRepo();
            deskRepo = new DeskRepo();
            actionLogRepo = new ActionLogRepo();

            ModuleManager.Add("/user", new UsersService());
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Service stopped due to error.");
            throw new RuntimeException("Couldn't initialize app.");
        }
    }
    public void contextDestroyed(ServletContextEvent event) {
        // Do your thing during webapp's shutdown.

    }
}
