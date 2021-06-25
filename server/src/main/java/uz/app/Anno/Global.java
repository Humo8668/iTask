package uz.app.Anno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.app.Anno.Util.Anno;
import uz.app.iTask.Util.Setup;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.Properties;

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
        System.out.println("DB_CONN_POOL_SIZE = " + DB_CONN_POOL_SIZE);

        Database.Init();
        Anno.Init();

        for (Repository repo: Repository.Instances) {
            repo.Init();
        }
        isInit = true;

    }
}
