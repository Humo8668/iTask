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

public class Global implements ServletContextListener {
    public static String DB_STRING = "";
    public static String DB_USERNAME = "";
    public static String DB_PASSWORD = "";

    public void contextInitialized(ServletContextEvent event) throws RuntimeException {
        // Do your thing during webapp's startup.

        ServletContext ctx = event.getServletContext(); // Context from web.xml file.
        DB_STRING = ctx.getInitParameter("DB_STRING");
        DB_USERNAME = ctx.getInitParameter("DB_USERNAME");
        DB_PASSWORD = ctx.getInitParameter("DB_PASSWORD");
        try {
            Anno.Init();
            Database.Init();
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't initialize app.");
        }
    }
    public void contextDestroyed(ServletContextEvent event) {
        // Do your thing during webapp's shutdown.

    }
}
