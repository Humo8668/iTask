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
import java.util.stream.Collectors;

public abstract class BaseModule {
    protected String moduleName;

    public String getReqBody(HttpServletRequest req) throws IOException
    {
        return req.getReader().lines().collect(Collectors.joining());
    }

    @Override
    public int hashCode() {
        return moduleName.hashCode();
    }
}
