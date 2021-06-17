package uz.app.Anno;

import uz.app.Anno.Annotations.Schema;
import uz.app.Anno.Annotations.Table;
import uz.app.Anno.Util.Anno;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Repository<T extends BaseEntity> {
    protected String TABLE_NAME = "";
    protected String SCHEMA_NAME = "public";

    protected Class<T> ClassRef;
    protected Field idField;
    protected LinkedList<Field> generatedFields;
    protected LinkedList<Field> autoincrementFields;
    protected HashMap<Field, Integer> fieldSqlTypes;
    protected HashMap<String, Field> columnField;
    protected Field[] entityFields;
    private Constructor constructor = null;

    protected String getTableFullName()
    {
        return this.SCHEMA_NAME + ".\"" + this.TABLE_NAME + "\"";
    }

    protected String getIdColumnName()
    {
        return Anno.getColumnName(idField);
    }


    /**
     * Refreshes table information for every mapping class fields
     */
    protected void gatherTableData()
    {
        Connection conn;
        DatabaseMetaData metadata;
        ResultSet rs;

        fieldSqlTypes = new HashMap<Field, Integer>();
        columnField = new HashMap<String, Field>();
        autoincrementFields = new LinkedList<Field>();
        generatedFields = new LinkedList<Field>();
        entityFields = null;

        HashMap<Integer, String> colName = new HashMap<Integer, String>();
        HashMap<Integer, Field> colField = new HashMap<Integer, Field>();

        // ===== Firstly, get table's column names
        try {
            conn = Database.getConnection();
            metadata = conn.getMetaData();
            rs = metadata.getColumns(null, SCHEMA_NAME, TABLE_NAME, "%");
            while(rs.next()) {
                colName.put(rs.getInt("ORDINAL_POSITION"), rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException ex) {
            return; // Couldn't connect to database. Refreshing stopped.
        }

        // ===== Secondly, match fields with ordinal numbers
        try {
            rs.beforeFirst();
            while(rs.next()) {
                int ordinalPosition = rs.getInt("ORDINAL_POSITION");
                Field currField = Anno.getFieldByColumnName(colName.get(ordinalPosition), ClassRef);
                if(currField == null) // if there's no field in class that corresponds to current column then continue.
                    continue;

                colField.put(ordinalPosition, currField);
            }
        } catch (SQLException ex) {
            return; // Couldn't connect to database. Refreshing stopped.
        }

        // Collect entity's fields that correspond to column in table
        entityFields = colField.values().toArray(entityFields);

        // ===== And then, match column names to class fields
        for (Map.Entry<Integer, String> pair: colName.entrySet()) {
            columnField.put(pair.getValue(), colField.get(pair.getKey()));
        }

        // ===== Finally, collect generated and autoincrement columns
        try {
            rs.beforeFirst();
            while(rs.next()) {
                int ordinalPosition = rs.getInt("ORDINAL_POSITION");
                Field currField = colField.get(ordinalPosition);

                if(rs.getString("IS_GENERATEDCOLUMN").equals("YES"))
                    generatedFields.add(colField.get(ordinalPosition));

                if(rs.getString("IS_AUTOINCREMENT").equals("YES"))
                    autoincrementFields.add(colField.get(ordinalPosition));

                fieldSqlTypes.put(currField, rs.getInt("DATA_TYPE"));
            }
        } catch (SQLException ex) { //if a database access error occurs; method <beforeFirst()> is called on a closed result set or the result set type is TYPE_FORWARD_ONLY
            return; // Couldn't connect to database. Refreshing stopped.
        }

    }

    protected void Init(Class<T> cl) throws Exception
    {
        ClassRef = cl;
        Constructor ctor = null;
        Constructor ctors[] = cl.getDeclaredConstructors();
        for (Constructor c: ctors)
            if (c.getGenericParameterTypes().length == 0)
            {
                ctor = c;
                break;
            }

        if(ctor == null)
            throw new Exception("Error: constructor with no arguments not found. Class name: " + getClass().getName());

        this.constructor = ctor;
        this.constructor.setAccessible(true);

        this.SCHEMA_NAME = cl.getAnnotation(Schema.class).value();
        this.TABLE_NAME = cl.getAnnotation(Table.class).value();
        this.idField = Anno.getIdField(cl);

        entityFields = this.ClassRef.getDeclaredFields();

        gatherTableData();
    }

    public <E extends T> Repository(Class<T> forClass)
    {
        try {
            this.Init(forClass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <E extends T> Repository()
    {
    }

    public T[] getAll() throws Exception
    {
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new Exception("Couldn't connect to database.");

        LinkedList<T> entities = new LinkedList<T>();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + getTableFullName());
        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        T entity;
        while(rs.next())
        {
            entity = this.makeObject(rs, rsmd);
            entities.add(entity);
        }

        int i = 0;
        T[] res = (T[])Array.newInstance(ClassRef, entities.size());
        for (T e: entities) {
            if(i > entities.size())
                break;
            res[i] = e;
            i++;
        }
        try {
            Database.close(connection);
        } catch (Exception ex) {
            Database.toTrash(connection);
        }
        return entities.toArray(res);
    };

    public T getById(long id) throws Exception
    {
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new Exception("Couldn't connect to database.");

        T entity;
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + getTableFullName() + " WHERE " + getIdColumnName() + " = ?");
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        if(rs.next())   // If row exists
            entity = this.makeObject(rs, rsmd);
        else
            entity = null;

        try {
            Database.close(connection);
        } catch (Exception ex) {
            Database.toTrash(connection);
        }
        return entity;
    }

    public boolean save(T entity) throws Exception
    {
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new Exception("Couldn't connect to database.");

        if(entity == null)
            return false;

        for (Field field : entityFields)
        {
            field.setAccessible(true);
            if(field.get(entity) != null)
                System.out.println(field.getName() + " : " + field.get(entity).toString());
            else
                System.out.println(field.getName() + " : null");
        }

        //  INSERT INTO public."Users"("login", "fullName", "email", "passwordHash", "state")
        //	VALUES ('Vasya_gq', 'Vasya Ivanov', 'v.ivanov@meyl.ru', '$2y$12$ALkeFSdcN7o.JAY/e9z7VePMLD7WWJYDAbVyknB/tG40BWP.tgnh6', 'A');

        String fields = getEntityFields();
        StringBuilder SqlQuery = new StringBuilder("INSERT INTO ");
        String valuesPlaceholder = getEntityValuePlaceholders();
        SqlQuery.append(getTableFullName()).append(" (").append(fields).append(") VALUES (").append(valuesPlaceholder).append(");");

        PreparedStatement stmt = connection.prepareStatement(SqlQuery.toString());

        Integer SqlType;
        int index = 1;
        for (Field field : entityFields)
        {
            if(generatedFields.contains(field) || autoincrementFields.contains(field)) // exclude generated and autoincrement fields
                continue;

            field.setAccessible(true);
            Object value = field.get(entity);
            if(fieldSqlTypes.containsKey(field)) // if column type info exists
            {
                SqlType = fieldSqlTypes.get(field);
                stmt.setObject(index, value, SqlType);
            } else
            {
                stmt.setObject(index,value);
            }

            /*if(type.equals(Integer.TYPE))
                stmt.setInt(index, field.getInt(entity));
            else if(type.equals(Long.TYPE))
                stmt.setLong(index, field.getLong(entity));
            else if(type.equals(Float.TYPE))
                stmt.setFloat(index, field.getFloat(entity));
            else if(type.equals(Double.TYPE))
                stmt.setDouble(index, field.getDouble(entity));
            else if(type.equals(String.class))
                stmt.setString(index, field.get(entity).toString());
            else if(type.equals(Short.TYPE))
                stmt.setShort(index, field.getShort(entity));
            else if(type.equals(Date.class))
                stmt.setDate(index, java.sql.Date.valueOf(field.get(entity).toString()) );
            else
                stmt.setObject(index, field.get(entity));*/

            index++;
        }
        int affectedRowsCtn = stmt.executeUpdate();

        if(affectedRowsCtn == 0)
            return false;
        Database.close(connection);
        return true;
    }

    public boolean delete(long id) throws Exception
    {
        return false;
    }

    public long count() throws Exception
    {
        return 0;
    }

    protected final T makeObject(ResultSet rs, ResultSetMetaData rsmd) throws Exception
    {
        Object obj = null;
        obj = constructor.newInstance();    // can throw exception

        Class cl = obj.getClass();
        for(int colIndex = 1; colIndex <= rsmd.getColumnCount(); colIndex++)
        {
            String colName = rsmd.getColumnName(colIndex);
            Field field = Anno.getFieldByColumnName(colName, cl);
            int colType = rsmd.getColumnType(colIndex);

            switch (colType){
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                case Types.CHAR:
                case Types.NCHAR:
                case Types.NVARCHAR:
                    field.set(obj, rs.getString(colIndex).trim());
                    break;
                case Types.INTEGER:
                case Types.NUMERIC:
                    field.setInt(obj, rs.getInt(colIndex));
                    break;
                case Types.BIGINT:
                    field.setLong(obj, rs.getLong(colIndex));
                    break;
                case Types.SMALLINT:
                case Types.TINYINT:
                    field.setShort(obj, rs.getShort(colIndex));
                    break;
                case Types.REAL:
                    field.setFloat(obj, rs.getFloat(colIndex));
                    break;
                case Types.DOUBLE:
                case Types.DECIMAL:
                    field.setDouble(obj, rs.getDouble(colIndex));
                    break;
                case Types.TIME:
                    field.set(obj, rs.getTime(colIndex));
                    break;
                case Types.TIMESTAMP:
                    field.set(obj, rs.getTimestamp(colIndex));
                    break;
                case Types.DATE:
                    field.set(obj, rs.getDate(colIndex));
                    break;
                default:
                    field.set(obj, rs.getObject(colIndex));
                    break;
            }
        }
        return (T)obj;
    }

    /**
     * Writes comma separated column names
     * @return Comma separated column names
     */
    protected String getEntityFields()
    {
        StringBuilder fieldsList = new StringBuilder();
        for (Field field: entityFields)
        {
            if(generatedFields.contains(field) || autoincrementFields.contains(field)) // exclude generated and autoincrement fields
                continue;

            String columnName = Anno.getColumnName(field);
            fieldsList.append('"');
            fieldsList.append(columnName);
            fieldsList.append('"');
            fieldsList.append(',');
        }
        return fieldsList.substring(0, fieldsList.length()-1);  // returning without last comma
    }

    protected String getEntityValuePlaceholders()
    {
        StringBuilder strBld = new StringBuilder();
        for (Field field : entityFields)
        {
            if(generatedFields.contains(field) || autoincrementFields.contains(field)) // exclude generated and autoincrement fields
                continue;
            strBld.append("?,");
        }

        return strBld.substring(0, strBld.length()-1);
    }
}
