package uz.app.Anno;

import uz.app.Anno.Annotations.Schema;
import uz.app.Anno.Annotations.Table;
import uz.app.Anno.Util.Anno;
import uz.app.Anno.Util.AnnoValidationException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class Repository<T extends BaseEntity> {
    public static LinkedList<Repository> Instances;

    static {
        Instances = new LinkedList<>();
    }

    protected String TABLE_NAME = "";
    protected String SCHEMA_NAME = "public";

    protected Class<T> ClassRef = null;
    protected Field idField;
    protected LinkedList<Field> GeneratedFields;        // List of entity class's fields that correspond generated column
    protected LinkedList<Field> AutoincrementFields;    // List of entity class's fields that correspond autoincrement column
    protected HashMap<Field, Integer> FieldSqlTypes;    // <Field of entity's class, Corresponding SQL type>
    protected HashMap<String, Field> ColumnField;       // <Column name, Entity's field>
    protected LinkedList<Field> NullableFields;
    protected Field[] entityFields;
    private Constructor constructor = null;

    public Repository()
    {
        Instances.add(this);

        FieldSqlTypes = new HashMap<Field, Integer>();
        ColumnField = new HashMap<String, Field>();
        AutoincrementFields = new LinkedList<Field>();
        GeneratedFields = new LinkedList<Field>();
        NullableFields = new LinkedList<Field>();
        entityFields = new Field[0];
    }

    /**
     * Refreshes table information for every mapping class fields
     */
    protected void gatherTableData()
    {
        Connection conn;
        DatabaseMetaData metadata;
        ResultSet rs;

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
            ex.printStackTrace();
            return; // Couldn't connect to database. Refreshing stopped.
        }

        // ===== Secondly, match fields with ordinal numbers
        try {
            rs.beforeFirst();
            while(rs.next()) {
                int ordinalPosition = rs.getInt("ORDINAL_POSITION");
                Field currField = Anno.forEntity(ClassRef).getFieldByColumnName(colName.get(ordinalPosition));
                if(currField == null) // if there's no field in class that corresponds to current column then continue.
                    continue;

                colField.put(ordinalPosition, currField);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return; // Couldn't connect to database. Refreshing stopped.
        }

        // Collect entity's fields that correspond to column in table
        entityFields = colField.values().toArray(entityFields);

        // ===== And then, match column names to class fields
        for (Map.Entry<Integer, String> pair: colName.entrySet()) {
            ColumnField.put(pair.getValue(), colField.get(pair.getKey()));
        }

        // ===== Finally, collect generated and autoincrement columns
        try {
            rs.beforeFirst();
            while(rs.next()) {
                int ordinalPosition = rs.getInt("ORDINAL_POSITION");
                Field currField = colField.get(ordinalPosition);

                if(rs.getString("IS_GENERATEDCOLUMN").equals("YES"))
                    GeneratedFields.add(colField.get(ordinalPosition));

                if(rs.getString("IS_AUTOINCREMENT").equals("YES"))
                    AutoincrementFields.add(colField.get(ordinalPosition));

                if(rs.getString("IS_NULLABLE").equals("YES"))
                    NullableFields.add(colField.get(ordinalPosition));

                FieldSqlTypes.put(currField, rs.getInt("DATA_TYPE"));
            }
        } catch (SQLException ex) { //if a database access error occurs; method <beforeFirst()> is called on a closed result set or the result set type is TYPE_FORWARD_ONLY
            ex.printStackTrace();
            return; // Couldn't connect to database. Refreshing stopped.
        }
    }

    protected void SetTargetEntity(Class<T> cl)
    {
        ClassRef = cl;
    }

    public void Init() throws Exception
    {
        if(ClassRef == null)
            throw new Exception("Target entity not set for repository");
        Constructor ctor = null;
        try{
            Constructor ctors[] = ClassRef.getDeclaredConstructors();
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

            Annotation SchemaAnno = ClassRef.getAnnotation(Schema.class);
            Annotation TableAnno = ClassRef.getAnnotation(Table.class);
            this.idField = Anno.forEntity(ClassRef).getIdField();


            if(SchemaAnno == null) {
                throw new NullPointerException("Schema's name must be indicated for entity class <" + ClassRef.getName() + ">");
            }
            if(TableAnno == null) {
                throw new NullPointerException("Table's name must be indicated for entity class <" + ClassRef.getName() + ">");
            }
            if(this.idField == null)
                throw new NullPointerException("Id field must be indicated for entity class <" + ClassRef.getName() + ">");

            this.SCHEMA_NAME = ((Schema) SchemaAnno).value();
            this.TABLE_NAME = ((Table) TableAnno).value();
            entityFields = this.ClassRef.getDeclaredFields();

            gatherTableData();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public T[] getAll() throws SQLException
    {
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new SQLException("Couldn't connect to database.");

        LinkedList<T> entities = new LinkedList<T>();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " +
                Anno.forEntity(ClassRef).getTableFullName());
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
        Database.close(connection);
        return entities.toArray(res);
    };

    public T getById(long id) throws SQLException
    {
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new SQLException("Couldn't connect to database.");

        T entity;
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ")
                .append(Anno.forEntity(ClassRef).getTableFullName())
                .append(" WHERE ")
                .append(Anno.forEntity(ClassRef).getIdColumnName())
                .append(" = ?");


        PreparedStatement stmt = connection.prepareStatement(query.toString());
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        if(rs.next())   // If row exists
            entity = this.makeObject(rs, rsmd);
        else
            entity = null;

        /*try {
            Database.close(connection);
        } catch (Exception ex) {
            Database.toTrash(connection);
        }*/
        Database.close(connection);
        return entity;
    }

    public void save(T entity) throws SQLException, AnnoValidationException
    {
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new SQLException("Couldn't connect to database.");

        if(entity == null)
            return;

        entity.validate();

        /*for (Field field : entityFields)
        {
            field.setAccessible(true);
            if(field.get(entity) != null)
                System.out.println(field.getName() + " : " + field.get(entity).toString());
            else
                System.out.println(field.getName() + " : null");
        }*/

        //  INSERT INTO public."Users"("login", "fullName", "email", "passwordHash", "state")
        //	VALUES ('Vasya_gq', 'Vasya Ivanov', 'v.ivanov@meyl.ru', '$2y$12$ALkeFSdcN7o.JAY/e9z7VePMLD7WWJYDAbVyknB/tG40BWP.tgnh6', 'A');

        String fields = getEntityFields();
        String valuesPlaceholder = getEntityValuePlaceholders();
        StringBuilder SqlQuery = new StringBuilder("INSERT INTO ");
        SqlQuery.append(Anno.forEntity(ClassRef).getTableFullName())
                .append(" (")
                .append(fields)
                .append(") VALUES (")
                .append(valuesPlaceholder)
                .append(");");


        PreparedStatement stmt = connection.prepareStatement(SqlQuery.toString());

        Integer SqlType;
        int index = 1;
        for (Field field : entityFields)
        {
            if(GeneratedFields.contains(field) || AutoincrementFields.contains(field)) // exclude generated and autoincrement fields
                continue;

            field.setAccessible(true);
            Object value;
            try { value = field.get(entity); } catch (IllegalAccessException ex) { ex.printStackTrace(); value = ""; }
            if(FieldSqlTypes.containsKey(field)) // if column type info exists
            {
                SqlType = FieldSqlTypes.get(field);
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
        Database.close(connection);

        if(affectedRowsCtn == 0)
            throw new SQLException("SQL: No rows affected");
    }

    public boolean delete(long id) throws SQLException
    {
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new SQLException("Couldn't connect to database.");

        System.out.println("Delete requested for id = " + id);
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM " +
                Anno.forEntity(ClassRef).getTableFullName() +
                " WHERE " +
                Anno.forEntity(ClassRef).getIdColumnName() +
                " = ?");
        stmt.setLong(1, id);
        stmt.executeUpdate();

        Database.close(connection);
        return true;
    }

    public long count() throws Exception
    {
        long res = 0;
        Connection connection = Database.getConnection();
        if(connection == null)
            throw new Exception("Couldn't connect to database.");

        PreparedStatement stmt = connection.prepareStatement("SELECT count(*) FROM " +
                Anno.forEntity(ClassRef).getTableFullName());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        try {
            res = rs.getInt(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Couldn't retrieve result.");
        } finally {
            Database.close(connection);
        }

        return res;
    }

    protected final T makeObject(ResultSet rs, ResultSetMetaData rsmd) throws SQLException
    {
        Object obj;
        try {
            obj = constructor.newInstance();
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        }

        Class cl = obj.getClass();
        for(int colIndex = 1; colIndex <= rsmd.getColumnCount(); colIndex++)
        {
            String colName = rsmd.getColumnName(colIndex);
            Field field = Anno.forEntity(cl).getFieldByColumnName(colName);
            int colType = rsmd.getColumnType(colIndex);

            if(field == null)
                continue;
            field.setAccessible(true);

            try {
                switch (colType) {
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
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
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
            if(GeneratedFields.contains(field) || AutoincrementFields.contains(field)) // exclude generated and autoincrement fields
                continue;

            String columnName = Anno.forEntity(ClassRef).getColumnName(field);
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
            if(GeneratedFields.contains(field) || AutoincrementFields.contains(field)) // exclude generated and autoincrement fields
                continue;
            strBld.append("?,");
        }

        return strBld.substring(0, strBld.length()-1);
    }
}
