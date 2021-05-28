package uz.app.iTask;

import org.slf4j.*;

import uz.app.iTask.Repositories.*;

// Class for global variables.
public class Global {

    static final Logger log = LoggerFactory.getLogger(Global.class);

    public static UserRepo userRepo;
    public static TaskRepo taskRepo;
    public static RoleRepo roleRepo;
    public static DeskRepo deskRepo;
    public static ActionLogRepo actionLogRepo;

    public static void Init() throws Exception
    {
        try {
            userRepo = new UserRepo();
            taskRepo = new TaskRepo();
            roleRepo = new RoleRepo();
            deskRepo = new DeskRepo();
            actionLogRepo = new ActionLogRepo();
        } catch (Exception ex){
            log.error("Error on initializing global variables: " + ex.getMessage());
            throw ex;
        }
    }



}
