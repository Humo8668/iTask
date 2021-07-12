package uz.app.iTask.Services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import uz.app.Anno.BaseModule;
import uz.app.Anno.Annotations.Module;
import uz.app.Anno.Annotations.Route;
import uz.app.Anno.Util.HttpMethod;
import uz.app.iTask.Models.User;
import uz.app.iTask.Util.Setup;


@Module("auth")
public class AuthService extends BaseModule {

    String getHash(String word, String hashingAlgorithm)
    {
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance(hashingAlgorithm);
        } catch(NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        byte[] hashBytes = md.digest(word.getBytes());
        BigInteger bigNum = new BigInteger(1, hashBytes);
        // Convert message digest into hex value
        String hashStr = bigNum.toString(16);
        // Add preceding 0s to make it 32 bit
        for(int i = hashStr.length(); i < 32; i++)
            hashStr.concat("0");
        
        return hashStr;
    }

    @Route(value = "/login", method = HttpMethod.POST)
    void login(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // ========================================================================
        // ===== Parsing json and getting login-password ===== 
        // ========================================================================
        Gson gson = new Gson();
        String reqBody = this.getReqBody(request);
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> requestBodyMap = gson.fromJson(reqBody, type);
        
        String login = requestBodyMap.get("login");
        String password = requestBodyMap.get("password");
        
        PrintWriter out = response.getWriter();
        out.print(login);
        out.print(" <br> ");
        out.print(password);

        // ========================================================================
        // ===== Getting hash-algoritm from context and make hash of password =====
        // ========================================================================
        ServletContext ctx = getContext();
        String hashingAlgorithm = ctx.getInitParameter("HASHING_ALGORITHM");
        String hashStr = getHash(password, hashingAlgorithm);
        out.print(" <br> ");
        out.print("hashed_password = " + hashStr);

        // ========================================================================
        // ===== Retrieving user by login and compare password hashes =====
        // ========================================================================
        User user = Setup.userRepo.getByLogin(login);
        if(user == null)
        {
            //response.sendError(200);
            return;
        }
        if(hashStr.equals(user.getPasswordHash()))
        {
            out.println("Authorized!!!");
            response.setStatus(200);
        } else
        {
            response.sendError(401);
            return;
        }
    }

}
