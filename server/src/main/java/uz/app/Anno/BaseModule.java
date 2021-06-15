package uz.app.Anno;


import uz.app.Anno.Annotations.Module;
import uz.app.Anno.Annotations.Route;
import uz.app.Anno.Util.HttpMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class BaseModule {
    protected String moduleName;

    public void Init() throws Exception
    {

    }

    @Override
    public int hashCode() {
        return moduleName.hashCode();
    }
}
