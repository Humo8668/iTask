package uz.app.Anno;

import uz.app.Anno.Util.ConcurrentSet;
import uz.app.DataTable.DataTable;
import uz.app.Anno.PoolConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

public class Database{

    public enum ArgumentMode { IN, OUT, INOUT }

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


    public static class Function
    {
        protected CallableStatement statement;
        protected Integer argCounter;
        protected StringBuilder SqlStatement;
        protected HashMap<Integer, Object> argVals;
        protected HashMap<Integer, ArgumentMode> argModes;
        protected HashMap<Integer, Integer> argTypes;

        private Function() {}

        public Function(String NameOfFunc)
        {
            this.SqlStatement = new StringBuilder("{? = call " + NameOfFunc + "(");
            this.argCounter = 1;        //  We have one argument for query yet - result of function.
            this.statement = null;

            this.argVals = new HashMap<Integer, Object>();
            this.argModes = new HashMap<Integer, ArgumentMode>();
        }

        public Function argInt(Integer argument, ArgumentMode argMode) throws SQLException
        {
            this.argVals.put(argCounter, argument);
            this.argModes.put(argCounter, argMode);
            this.argTypes.put(argCounter, java.sql.Types.INTEGER);
            this.argCounter++;
            this.SqlStatement.append("?,");
            return this;
        }

        public Function argString(String argument, ArgumentMode argMode) throws SQLException
        {
            this.argVals.put(argCounter, argument);
            this.argModes.put(argCounter, argMode);
            this.argTypes.put(argCounter, java.sql.Types.VARCHAR);
            this.argCounter++;
            this.SqlStatement.append("?,");
            return this;
        }

        public Function argFloat(Float argument, ArgumentMode argMode) throws SQLException
        {
            this.argVals.put(argCounter, argument);
            this.argModes.put(argCounter, argMode);
            this.argTypes.put(argCounter, java.sql.Types.FLOAT);
            this.argCounter++;
            this.SqlStatement.append("?,");
            return this;
        }

        public Function argBoolean(Boolean argument, ArgumentMode argMode) throws SQLException
        {
            this.argVals.put(argCounter, argument);
            this.argModes.put(argCounter, argMode);
            this.argTypes.put(argCounter, java.sql.Types.BOOLEAN);
            this.argCounter++;
            this.SqlStatement.append("?,");
            return this;
        }

        public Function argDate(Date argument, ArgumentMode argMode) throws SQLException
        {
            this.argVals.put(argCounter, argument);
            this.argModes.put(argCounter, argMode);
            this.argTypes.put(argCounter, java.sql.Types.DATE);
            this.argCounter++;
            this.SqlStatement.append("?,");
            return this;
        }

        public Object execute(Connection conn) throws SQLException
        {
            this.SqlStatement.delete(this.SqlStatement.length()-1, this.SqlStatement.length()); // remove last comma
            this.SqlStatement.append(") }");
            this.statement = conn.prepareCall(this.SqlStatement.toString());

            Object value;
            Integer sqlType;
            ArgumentMode argMode;
            for(Integer argPos : this.argVals.keySet())
            {
                value = this.argVals.get(argPos);
                sqlType = this.argTypes.get(argPos);
                argMode = this.argModes.get(argPos);

                switch(argMode)
                {
                    case OUT:
                        statement.registerOutParameter(argPos, sqlType);
                        break;
                    case INOUT:
                        statement.registerOutParameter(argPos, sqlType);
                        statement.setObject(argPos, value, sqlType);
                        break;
                    case IN:
                        statement.setObject(argPos, value, sqlType);
                        break;
                }
            }
            this.statement.execute();
            return this.statement.getObject(1);
        }

        public Integer getInt(int position) throws SQLException {
            return this.statement.getInt(position);
        } 
        public String getString(int position) throws SQLException {
            return this.statement.getString(position);
        }
        public Float getFloat(int position) throws SQLException {
            return this.statement.getFloat(position);
        }
        public Boolean getBoolean(int position) throws SQLException {
            return this.statement.getBoolean(position);
        }
        public Date getDate(int position) throws SQLException {
            return this.statement.getDate(position);
        }
    }

    public static class Procedure extends Function
    {
        public Procedure(String NameOfProcedure) {
            this.SqlStatement = new StringBuilder("{ call " + NameOfProcedure + "(");
            this.argCounter = 0;
            this.statement = null;

            this.argVals = new HashMap<Integer, Object>();
            this.argModes = new HashMap<Integer, ArgumentMode>();
        }

        @Override
        public Object execute(Connection conn) throws SQLException {
            super.execute(conn);
            return null;
        }
    }

    public static Function setFunction(String funcName)
    {
        return new Function(funcName);
    }

    public static Procedure setProcedure(String procName)
    {
        return new Procedure(procName);
    }
}
