package uz.app.iTask.Services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uz.app.Anno.Annotations.Module;
import uz.app.Anno.Annotations.Route;
import uz.app.Anno.Util.HttpMethod;

@Module("auth")
public class AuthService {
    @Route(value = "/login", method = HttpMethod.POST)
    void login(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        
    }

}
