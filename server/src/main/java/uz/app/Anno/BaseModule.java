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

    /**
     * Hash-map that defines path ~ method relation.
     */
    HashMap<String, ServletLogic> Mapping_Get;
    HashMap<String, ServletLogic> Mapping_Post;
    HashMap<String, ServletLogic> Mapping_Put;
    HashMap<String, ServletLogic> Mapping_Delete;

    public void Init() throws Exception
    {
        Mapping_Get = new HashMap<String, ServletLogic>();
        Mapping_Post = new HashMap<String, ServletLogic>();
        Mapping_Put = new HashMap<String, ServletLogic>();
        Mapping_Delete = new HashMap<String, ServletLogic>();

        Method[] methods = getClass().getDeclaredMethods();
        for (final Method method : methods)
        {
            method.setAccessible(true);
            //if(method.getExceptionTypes().length > 0)
            //    throw new Exception("Route handling functions must not to throw exceptions");

            Route routeAnno = method.getAnnotation(Route.class);
            if(routeAnno == null)
                continue;
            HttpMethod httpMethod = routeAnno.method();
            String path = routeAnno.value();
            if(!ModuleManager.isValidPath(path))
                throw new Exception("Invalid path for route.");
            ServletLogic sl = new ServletLogic() {
                public void process(BaseModule module, HttpServletRequest req, HttpServletResponse res)
                        throws ServletException, IOException {
                    try {
                        method.invoke(module, req, res);
                    }catch (IllegalAccessException ex) {}
                    catch (InvocationTargetException ex) {
                        if(ex.getCause().getClass().equals(ServletException.class))
                            throw new ServletException(ex.getMessage());
                        if(ex.getCause().getClass().equals(IOException.class))
                            throw new IOException(ex.getMessage());
                    }
                }
            };

            switch (httpMethod)
            {
                case GET:
                    Mapping_Get.put(path, sl);
                    break;
                case POST:
                    Mapping_Post.put(path, sl);
                    break;
                case PUT:
                    Mapping_Put.put(path, sl);
                    break;
                case DELETE:
                    Mapping_Delete.put(path, sl);
                    break;
            }
        }
        return;
    }
}
