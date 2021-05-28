package uz.app.iTask.Repositories;

import uz.app.iTask.Models.BaseEntity;
import uz.app.iTask.Util.Anno;
import uz.app.iTask.Util.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

public abstract class Repository<T extends BaseEntity> {
    String TABLE_NAME = "";
    String SCHEMA_NAME = "public";
    Annotation[] Annotations = null;
    protected Constructor constructor = null;

    protected String getTableFullName()
    {
        return this.SCHEMA_NAME + ".\"" + this.TABLE_NAME + "\"";
    }

    protected void Init(Class<T> cl) throws Exception
    {
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
    }

    public abstract T[] getAll() throws Exception;

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
