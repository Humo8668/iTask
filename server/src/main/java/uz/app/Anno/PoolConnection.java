package uz.app.Anno;

import uz.app.Anno.Util.ConcurrentSet;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;

public class PoolConnection {
    private static int POOL_SIZE = 10;
    private static int CONN_RETRY_MILLISECONDS = 50;   // Time in milliseconds for delay per connection retry.
    private static boolean IS_AUTO_COMMIT = true;
    private static ArrayBlockingQueue<Connection> availConnections = null;
    private static ConcurrentSet<Connection> usingConnections = null;

    public static void Init() throws Exception
    {
        try {
            POOL_SIZE = Integer.parseInt(Global.DB_CONN_POOL_SIZE);
        } catch (NumberFormatException ex) {
            POOL_SIZE = 10;
        }
        availConnections = new ArrayBlockingQueue<Connection>(POOL_SIZE);
        usingConnections = new ConcurrentSet<Connection>();

        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        for(int i = 0; i < POOL_SIZE; i++)
        {
            try {
                connection = PoolConnection.newConnection();
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
        Connection connection = null;
        Boolean newConnectionCreated = false;

        while(!newConnectionCreated)
        {
            try {
                connection = DriverManager.getConnection(Global.DB_STRING, Global.DB_USERNAME, Global.DB_PASSWORD);
                newConnectionCreated = true;
            } catch (SQLException ex) {
                try {
                    synchronized (newConnectionCreated) {
                        newConnectionCreated.wait(CONN_RETRY_MILLISECONDS);
                    }
                } catch (InterruptedException interruptEx) {
                    interruptEx.printStackTrace();
                    throw ex;
                }
            }
        }
        connection.setAutoCommit(IS_AUTO_COMMIT);
        //connection = DriverManager.getConnection(Global.DB_STRING, Global.DB_USERNAME, Global.DB_PASSWORD);
        return connection;
    }

    public static Connection getConnection() throws SQLException
    {
        Connection connection = null;

        connection = availConnections.poll();
        if(connection==null)
            connection = newConnection();
        else
            usingConnections.add(connection);
        return connection;
    }

    public static void close(Connection connection) throws SQLException
    {
        synchronized (usingConnections)
        {
            if(usingConnections.hasElement(connection)) {
                usingConnections.removeElement(connection);
                availConnections.add(connection);
            } else {
                connection.close();
            }
        }
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
            usingConnections.removeElement(connection);
            availConnections.remove(connection);
            connection.close();
        }
    }
}
