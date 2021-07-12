package uz.app.Anno;

import uz.app.Anno.Annotations.Module;
import uz.app.Anno.Annotations.Route;
import uz.app.Anno.Util.HttpMethod;
import uz.app.Anno.Util.Pair;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class RouteProcessingService {

    protected static HashMap<Pair<String, HttpMethod>, Pair<BaseModule, Method>> RouteHashing =
            new HashMap<Pair<String, HttpMethod>, Pair<BaseModule, Method>>();

    public static void AddModule(BaseModule module) throws Exception
    {
        Class<? extends BaseModule> moduleClass = module.getClass();
        Annotation moduleAnno = moduleClass.getAnnotation(Module.class);
        String moduleName = ((Module) moduleAnno).value();

        moduleName = moduleName.trim();
        moduleName = moduleName.toLowerCase();
        if(!moduleName.startsWith("/"))
            moduleName = "/" + moduleName;
        if(moduleName.endsWith("/"))
            moduleName = moduleName.substring(0, moduleName.length()-1);

        Method[] methods = moduleClass.getDeclaredMethods();
        for (Method method: methods) {
            Annotation route = method.getAnnotation(Route.class);
            if(route == null)
                continue;
            Class<?>[] paramTypes = method.getParameterTypes();

            if(paramTypes.length != 2)
                throw new Exception("Method's arguments count is wrong.");
            if(!paramTypes[0].equals(HttpServletRequest.class) ||
                    !paramTypes[1].equals(HttpServletResponse.class))
                throw new Exception("Method's argument types are wrong.");

            
            HttpMethod httpMethod = ((Route) route).method();
            String path = ((Route) route).value();

            path = path.trim();
            path = path.toLowerCase();
            if(!path.startsWith("/"))
                path = "/" + path;
            if(path.endsWith("/"))
                path = path.substring(0, path.length()-1);

            String fullPath = moduleName + path;
            System.out.println(httpMethod.toString() + " : " + fullPath);
            RouteHashing.put(
                    new Pair<String, HttpMethod>(fullPath, httpMethod),
                    new Pair<BaseModule, Method>(module, method));
        }
        module.moduleName = moduleName;
    }

    public static void process(String reqPath, HttpMethod httpMethod, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        if(reqPath == null)
        {
            res.sendError(404);
            return;
        }
        reqPath = reqPath.trim();
        reqPath = reqPath.toLowerCase();
        Pair<String, HttpMethod> routeInfo = new Pair<String, HttpMethod>(reqPath, httpMethod);
        Pair<BaseModule, Method> logicMethod;
        if(!RouteHashing.containsKey(routeInfo))
        {
            System.out.println("Route " + reqPath + " didn't caught by main servlet.");
            res.sendError(404);
            return;
        }

        logicMethod = RouteHashing.get(routeInfo);
        Method method = logicMethod.getValue();
        BaseModule module = logicMethod.getKey();
        method.setAccessible(true);

        try {
            method.invoke(module, req, res);
        }catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        catch (InvocationTargetException ex) {
            if(ex.getCause().getClass().equals(ServletException.class))
                throw new ServletException(ex.getMessage());
            else if(ex.getCause().getClass().equals(IOException.class))
                throw new IOException(ex.getMessage());
            else
                throw new ServletException(ex.getCause());
        }
    }
}
