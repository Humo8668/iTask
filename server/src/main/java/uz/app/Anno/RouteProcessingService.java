package uz.app.Anno;

import uz.app.Anno.Annotations.Module;
import uz.app.Anno.Util.Pair;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteProcessingService {

    static HashMap<String, Pair<Class<? extends BaseModule>, Method>> RouteHashing;

    public static boolean isValidPath(String path)
    {
        Pattern pattern = Pattern.compile("^([\\/]([\\w])*)*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

    public void AddModule(BaseModule module)
    {

    }
}
