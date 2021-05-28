package uz.app.iTask.Util;

import uz.app.iTask.Global;

import org.slf4j.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.util.Properties;

@WebListener
public class Config implements ServletContextListener {
    public static String DB_STRING = "";
    public static String DB_USERNAME = "";
    public static String DB_PASSWORD = "";


    static final Logger log = LoggerFactory.getLogger(Config.class);
    static Properties properties = null;
    private final static int DECRYPT_KEY_INT = 1;


    public void contextInitialized(ServletContextEvent event) throws RuntimeException {
        // Do your thing during webapp's startup.

        File configFile = new File("D:\\Projects\\PetProjects\\iTask\\server\\config.properties");
        try {
            InputStream fileStream = new FileInputStream(configFile);
            properties = new Properties();
            properties.load(fileStream);

            DB_STRING = properties.getProperty("DB_STRING");
            DB_USERNAME = properties.getProperty("DB_USERNAME");
            DB_PASSWORD = properties.getProperty("DB_PASSWORD");

            log.warn("Database settings:\n DB_STRING=" + DB_STRING + "; DB_USERNAME=" + DB_USERNAME + "; DB_PASSWORD=" + DB_PASSWORD + ";");
            fileStream.close();
        } catch (FileNotFoundException ex) {
            log.error("Config file not found: " + ex.getMessage());
            log.warn("Setting default configurations...");
        } catch (IOException ex) {
            log.error("Error on reading config file: " + ex.getMessage());
            log.warn("Setting default configurations...");
        } catch (Exception ex){
            log.error("Error on reading config file: " + ex.getMessage());
        }

        try {
            Global.Init();
            Database.Init();
            Anno.Init();
        } catch (Exception ex) {
            log.error("Service stopped due to error.");
            throw new RuntimeException("Couldn't initialize app.");
        }
    }
    public void contextDestroyed(ServletContextEvent event) {
        // Do your thing during webapp's shutdown.

    }
}
