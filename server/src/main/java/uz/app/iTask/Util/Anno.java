package uz.app.iTask.Util;

import org.slf4j.*;
import uz.app.iTask.Annotations.Column;
import uz.app.iTask.Models.BaseEntity;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Anno {
    static final Logger log = LoggerFactory.getLogger(Anno.class);

    private static HashMap<Pair<Class, String>, Field> columnField;
    public static void Init()
    {
        columnField = new HashMap<Pair<Class, String>, Field>();
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
