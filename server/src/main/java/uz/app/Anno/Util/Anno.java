package uz.app.Anno.Util;

import uz.app.Anno.Annotations.Column;
import uz.app.Anno.Annotations.Id;
import uz.app.Anno.Annotations.Table;
import uz.app.Anno.Database;
import uz.app.Anno.Global;
import uz.app.Anno.Repository;
import uz.app.Anno.RouteProcessingService;
import uz.app.iTask.Repositories.*;

import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.util.HashMap;
import java.util.LinkedList;

public class Anno {
    public static class TableMetaData{}

    public static class EntityMetaData{}

    public static TableMetaData forTable() { return new TableMetaData(); }

    public static EntityMetaData forEntity() { return new EntityMetaData(); }


    private static HashMap<Pair<Class, String>, Field> columnField;     // <EntityClass, ColumnName, FieldInClass>

    public static void Init() throws Exception
    {
        columnField = new HashMap<Pair<Class, String>, Field>();
    }

    public static String getColumnName(Field field)
    {
        if(field == null)
            return "";
        return field.getAnnotation(Column.class).value();
    }

    public static Field getFieldByColumnName(String columnName, Class cl)
    {
        Pair<Class, String> column = new Pair<Class, String>(cl, columnName);
        if(columnField.containsKey(column))
        {
            return columnField.get(column);
        }
        // else
        Field[] fields = cl.getDeclaredFields();
        for(Field field : fields)
        {
            String currColumn = field.getAnnotation(Column.class).value();
            if(columnName.equals(currColumn))
            {
                field.setAccessible(true);
                columnField.put(column, field);
                return field;
            }
        }

        return null;
    }

    public static Field getIdField(Class objectClass)
    {
        //System.out.println("Id field searching started for " + objectClass.getName());
        Field res = null;
        Field[] fields = objectClass.getDeclaredFields();
        //System.out.println("Count = " + fields.length);
        for (Field field: fields) {
            //System.out.println("Another field : " + field.getName());
            if(field.getAnnotation(Id.class) != null)
            {
                res = field;
                //System.out.println(field.getName() + " has <id> annotation");
                break;
            }
        }
        return res;
    }


    /*public static Class getTypeVariable(Class cl, int varIndex)
    {
        Class actualClass = cl;
        return (Class) ((ParameterizedType) actualClass.getGenericSuperclass()).getActualTypeArguments()[varIndex];
    }*/

}
