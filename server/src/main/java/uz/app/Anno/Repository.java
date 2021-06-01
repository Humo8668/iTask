package uz.app.Anno;

import uz.app.Anno.Annotations.Schema;
import uz.app.Anno.Annotations.Table;
import uz.app.Anno.Util.Anno;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.LinkedList;

public abstract class Repository<T extends BaseEntity> {
    protected String TABLE_NAME = "";
    protected String SCHEMA_NAME = "public";
    private Annotation[] Annotations = null;
    private Constructor constructor = null;
    protected Class<T> ClassRef;
    protected String getTableFullName()
    {
        return this.SCHEMA_NAME + ".\"" + this.TABLE_NAME + "\"";
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
        this.Annotations = cl.getAnnotations();
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

        return entities.toArray(res);
    };

    public abstract T getById(long id) throws Exception;

    public abstract boolean save(T entity) throws Exception;

    public abstract boolean delete(long id) throws Exception;

    public abstract long count() throws Exception;

    protected T makeObject(ResultSet rs, ResultSetMetaData rsmd) throws Exception
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
}
