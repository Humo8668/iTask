package uz.app.Anno;

import uz.app.Anno.Util.Anno;
import javax.servlet.ServletContext;

public class Global {
    public static String DB_STRING = "";
    public static String DB_USERNAME = "";
    public static String DB_PASSWORD = "";
    public static String DB_CONN_POOL_SIZE = "";

    public static boolean isInit = false;

    public static void Init(ServletContext ctx) throws Exception {
        DB_STRING = ctx.getInitParameter("DB_STRING");
        DB_USERNAME = ctx.getInitParameter("DB_USERNAME");
        DB_PASSWORD = ctx.getInitParameter("DB_PASSWORD");
        DB_CONN_POOL_SIZE = ctx.getInitParameter("DB_CONN_POOL_SIZE");

        System.out.println("DB_STRING = " + DB_STRING);
        System.out.println("DB_USERNAME = " + DB_USERNAME);
        System.out.println("DB_PASSWORD = " + DB_PASSWORD);
        System.out.println("DB_CONN_POOL_SIZE = " + DB_CONN_POOL_SIZE);

        PoolConnection.Init();
        Anno.Init();

        //Database.setFunction("getGreeting").execute(PoolConnection.getConnection());

        for (Repository<? extends BaseEntity> repo: Repository.Instances) {
            repo.Init();
        }
        isInit = true;

    }
}
