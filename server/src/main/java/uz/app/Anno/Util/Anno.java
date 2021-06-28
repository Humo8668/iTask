package uz.app.Anno.Util;

import uz.app.Anno.*;
import uz.app.Anno.Annotations.Column;
import uz.app.Anno.Annotations.Id;
import uz.app.Anno.Annotations.Schema;
import uz.app.Anno.Annotations.Table;
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
    public static class TableMetaData
    {
        String TABLE_NAME;

        public TableMetaData(String tableName)
        {
            this.TABLE_NAME = tableName;
        }


    }

    public static class EntityMetaData
    {
        Class<? extends BaseEntity> ENTITY_CLASS;
        public EntityMetaData(Class<? extends BaseEntity> entityClass)
        {
            this.ENTITY_CLASS = entityClass;
        }

        public String getColumnName(Field field)
        {
            if(field == null)
                return "";

            return field.getAnnotation(Column.class).value();
        }

        public Field getFieldByColumnName(String columnName)
        {
            Field[] fields = ENTITY_CLASS.getDeclaredFields();
            for(Field field : fields)
            {
                String currColumn = field.getAnnotation(Column.class).value();
                if(columnName.equals(currColumn))
                {
                    field.setAccessible(true);
                    return field;
                }
            }
            return null;
        }

        public Field getIdField()
        {
            Field res = null;
            Field[] fields = ENTITY_CLASS.getDeclaredFields();
            for (Field field: fields) {
                if(field.getAnnotation(Id.class) != null)
                {
                    res = field;
                    break;
                }
            }
            return res;
        }

        public String getIdColumnName()
        {
            return getColumnName(getIdField());
        }

        public String getTableFullName()
        {
            return getSchemaName() + ".\"" + getTableName() + "\"";
        }

        public String getTableName()
        {
            return this.ENTITY_CLASS.getAnnotation(Table.class).value();
        }

        public String getSchemaName()
        {
            return this.ENTITY_CLASS.getAnnotation(Schema.class).value();
        }
    }

    public static TableMetaData forTable(String tableName)
    {
        return new TableMetaData(tableName);
    }

    public static EntityMetaData forEntity(Class<? extends BaseEntity> entityClass)
    {
        return new EntityMetaData(entityClass);
    }

    private static HashMap<Pair<Class, String>, Field> columnField;     // <EntityClass, ColumnName, FieldInClass>

    public static void Init() throws Exception
    {
        columnField = new HashMap<Pair<Class, String>, Field>();
    }

}
