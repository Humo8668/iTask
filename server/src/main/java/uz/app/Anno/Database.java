package uz.app.Anno;

import uz.app.Anno.Util.ConcurrentSet;
import uz.app.DataTable.DataTable;
import uz.app.Anno.PoolConnection;

import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

public class Database{

    public static DataTable getTable(String tableName, String schemaName, String columns, String where, String order) throws SQLException
    {
        DataTable dt = new DataTable();
        Connection conn = PoolConnection.getConnection();
        if(conn == null) throw new SQLException("Couldn't connect to database. Got null instead of connection.");

        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("SELECT ").append(columns)
                    .append(" FROM ").append(schemaName).append("\"").append(tableName).append("\" ")
                    .append("WHERE ").append(where)
                    .append(" ORDER BY ").append(order);

        PreparedStatement stmt = conn.prepareStatement(sqlStatement.toString());
        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        
        int columnsCount = rsmd.getColumnCount();
        String colName;
        for (int i = 1; i < columnsCount + 1; i++) {
            colName = rsmd.getColumnName(i);
            dt.addColumn(colName);
        }

        LinkedList<Object> row = new LinkedList<Object>();
        while(rs.next()) {
            for (int i = 1; i < columnsCount + 1; i++) {
                row.add( rs.getObject(rsmd.getColumnName(i)) );
            }
            dt.addRow(row);
        }

        return dt;
    }
    public static DataTable getTable(String tableName, String schemaName, String columns, String where) throws SQLException
    {
        return getTable(tableName, schemaName, columns, where, "0");
    }
    public static DataTable getTable(String tableName, String schemaName, String columns) throws SQLException
    {
        return getTable(tableName, schemaName, columns, "1=1", "0");
    }
    public static DataTable getTable(String tableName, String schemaName) throws SQLException
    {
        return getTable(tableName, schemaName, "*", "1=1", "0");
    }
    public static DataTable getTable(String tableName) throws SQLException
    {
        return getTable(tableName, "public", "*", "1=1", "0");
    }


    public static void execProcedure(String procedureName, Object[] args) throws SQLException, SQLTimeoutException
    {
        // THERE ARE IN-OUT PARAMS!!!!
        
        /*Connection conn = PoolConnection.getConnection();
        if(conn == null) throw new SQLException("Couldn't connect to database. Got null instead of connection.");

        String sqlStatement = procedureName + "(";
        for(int i = 0; i < args.length; i++)
            sqlStatement += "?,";
        sqlStatement = sqlStatement.substring(0, sqlStatement.length()-1);
        CallableStatement cs = conn.prepareCall("call " + sqlStatement);

        for(int parameterIndex = 0; parameterIndex < args.length; parameterIndex++)
            cs.setObject(parameterIndex, args[parameterIndex]);
        
        cs.execute();*/

        return;
    }
    public static void execProcedure(String procedureName, Object arg1, Object arg2, Object arg3, Object arg4)
        throws SQLException, SQLTimeoutException
    {
        Object[] args = new Object[4];
        args[0] = arg1; args[1] = arg2;
        args[2] = arg3; args[3] = arg4;
        execProcedure(procedureName, args);
    }
    public static void execProcedure(String procedureName, Object arg1, Object arg2, Object arg3)
        throws SQLException, SQLTimeoutException
    {
        Object[] args = new Object[4];
        args[0] = arg1; args[1] = arg2;
        args[2] = arg3;
        execProcedure(procedureName, args);
    }
    public static void execProcedure(String procedureName, Object arg1, Object arg2)
        throws SQLException, SQLTimeoutException
    {
        Object[] args = new Object[4];
        args[0] = arg1; args[1] = arg2;
        execProcedure(procedureName, args);
    }
    public static void execProcedure(String procedureName, Object arg1)
        throws SQLException, SQLTimeoutException
    {
        Object[] args = new Object[4];
        args[0] = arg1;
        execProcedure(procedureName, args);
    }


    public static void execFunction(String functionName, Object[] args) throws SQLException, SQLTimeoutException
    {
        /*Connection conn = PoolConnection.getConnection();
        if(conn == null) throw new SQLException("Couldn't connect to database. Got null instead of connection.");

        String sqlStatement = functionName + "(";
        for(int i = 0; i < args.length; i++)
            sqlStatement += "?,";
        sqlStatement = sqlStatement.substring(0, sqlStatement.length()-1);
        CallableStatement cs = conn.prepareCall("SELECT " + sqlStatement);
        cs.
        for(int parameterIndex = 0; parameterIndex < args.length; parameterIndex++)
            cs.setObject(parameterIndex, args[parameterIndex]);
        
        cs.execute();*/

        return;
    }
    public static void execFunction(String functionName, Object arg1, Object arg2, Object arg3, Object arg4)
        throws SQLException, SQLTimeoutException
    {
        Object[] args = new Object[4];
        args[0] = arg1; args[1] = arg2;
        args[2] = arg3; args[3] = arg4;
        execProcedure(functionName, args);
    }
    public static void execFunction(String functionName, Object arg1, Object arg2, Object arg3)
        throws SQLException, SQLTimeoutException
    {
        Object[] args = new Object[4];
        args[0] = arg1; args[1] = arg2;
        args[2] = arg3;
        execProcedure(functionName, args);
    }
    public static void execFunction(String functionName, Object arg1, Object arg2)
        throws SQLException, SQLTimeoutException
    {
        Object[] args = new Object[4];
        args[0] = arg1; args[1] = arg2;
        execProcedure(functionName, args);
    }
    public static void execFunction(String functionName, Object arg1)
        throws SQLException, SQLTimeoutException
    {
        Object[] args = new Object[4];
        args[0] = arg1;
        execProcedure(functionName, args);
    }
}
