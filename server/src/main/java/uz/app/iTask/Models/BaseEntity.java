package uz.app.iTask.Models;

import uz.app.iTask.Util.Anno;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import uz.app.iTask.Util.Pair;

public abstract class BaseEntity {

    public void setValueAccordingType(BaseEntity obj, String columnName, int JDBCType, Object value)
    {
        Field field = Anno.getFieldByColumnName(columnName, this.getClass());
        field.setAccessible(true);
        try {
            field.set(this, value);
        } catch (Exception ex){
            //field.getType();
        }
        return;
    }
}
