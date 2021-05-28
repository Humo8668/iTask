package uz.app.iTask.Util;

import org.slf4j.*;
import java.sql.*;
import java.util.LinkedList;

public class Database{
    private static final int POOL_SIZE = 10;
    private static LinkedList<Connection> availConnections = null;
    private static LinkedList<Connection> usingConnections = null;
    static final Logger log = LoggerFactory.getLogger(Config.class);

    public static void Init() throws Exception
    {
        availConnections = new LinkedList<Connection>();
        usingConnections = new LinkedList<Connection>();

        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        for(int i = 0; i < POOL_SIZE; i++)
        {
            try {
                connection = Database.newConnection();
            } catch (SQLException ex){
                log.warn("Couldn't connect to database");
                continue;
            }
            availConnections.add(connection);
        }

        return;
    }

    protected static Connection newConnection() throws SQLException
    {
        Connection connection = DriverManager.getConnection(Config.DB_STRING, Config.DB_USERNAME, Config.DB_PASSWORD);
        return connection;
    }

    public static Connection getConnection()
    {
        Connection connection = null;
        if(availConnections.size() > 0)
        {
            connection = availConnections.getFirst();
            availConnections.removeFirst();
            usingConnections.add(connection);
            return connection;
        }
        else
        {
            try{ connection = newConnection(); }
            catch(SQLException ex) { log.error("Error on instantiating new connection. " + ex.getMessage()); }
        }
        return connection;
    }

    public static void close(Connection connection)
    {
        if(usingConnections.contains(connection))
        {
            try {connection.commit();}
            catch (SQLException ex) { log.error("Error on committing before closing connection"); }
            usingConnections.remove(connection);
            availConnections.add(connection);
        }
        else if (availConnections.contains(connection))
        {
            try {
                connection.commit();
                connection.close();
            }catch (SQLException ex) {
                log.error("Error on connection closing");
            }
        }
        
        return;
    }

    public static void toTrash(Connection connection)
    {
        if(!usingConnections.contains(connection) && !availConnections.contains(connection))
            return;

        String query = "SELECT 1";
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException ex) {
            usingConnections.remove(connection);
            availConnections.remove(connection);
            try {
                connection.close();
            } catch (SQLException closeExc){
                log.error("Error on closing connection: " + closeExc.getMessage());
            }
        }
    }
}
