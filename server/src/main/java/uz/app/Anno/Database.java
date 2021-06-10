package uz.app.Anno;

import org.slf4j.*;

import java.sql.*;
import java.util.LinkedList;

public class Database{
    private static final int POOL_SIZE = 10;
    private static LinkedList<Connection> availConnections = null;
    private static LinkedList<Connection> usingConnections = null;

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
                continue;
                //throw new Exception("Couldn't connect to database");
            }
            availConnections.add(connection);
        }

        return;
    }

    protected static Connection newConnection() throws SQLException
    {
        Connection connection = DriverManager.getConnection(Global.DB_STRING, Global.DB_USERNAME, Global.DB_PASSWORD);
        return connection;
    }

    public static Connection getConnection() throws SQLException
    {
        Connection connection = null;
        if(availConnections.size() > 0)
        {
            connection = availConnections.getFirst();
            availConnections.removeFirst();
            usingConnections.add(connection);
        }
        else
        {
            connection = newConnection();
        }
        return connection;
    }

    public static void close(Connection connection) throws SQLException
    {
        if(usingConnections.contains(connection))
        {
            try {
                connection.commit();
            } catch (SQLException ex) {}
            usingConnections.remove(connection);
            availConnections.add(connection);
        }
        else if (availConnections.contains(connection))
        {
            try {
                connection.commit();
            } catch (SQLException ex) {}
            connection.close();
        }
        
        return;
    }

    // На случай, если связь с БД оборвана. Рекомендуется вызывать при получении исключения SQLException.
    public static void toTrash(Connection connection) throws SQLException
    {
        if(!usingConnections.contains(connection) && !availConnections.contains(connection))
            return;

        String query = "SELECT 1";
        try{
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            stmt.close();
        } catch (SQLException ex) { // Part of database connection logic
            usingConnections.remove(connection);
            availConnections.remove(connection);
            connection.close();
        }
    }
}
