package uz.app.Anno.Util;

import uz.app.Anno.Annotations.Column;
import uz.app.iTask.Repositories.*;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Anno {

    private static HashMap<Pair<Class, String>, Field> columnField;
    public static void Init() throws Exception
    {
        columnField = new HashMap<Pair<Class, String>, Field>();

        try {

        } catch (Exception ex){
            throw new Exception("Error on initializing global variables: " + ex.getMessage());
        }
    }

    public static String getColumnName(Field field)
    {
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

    /*public static Class getTypeVariable(Class cl, int varIndex)
    {
        Class actualClass = cl;
        return (Class) ((ParameterizedType) actualClass.getGenericSuperclass()).getActualTypeArguments()[varIndex];
    }*/

}
