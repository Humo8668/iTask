package uz.app.iTask.Filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;

@WebFilter("/check")
public class UsersServiceFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletOutputStream out = response.getOutputStream();
        /*out.print(request.getLocalAddr() + "<br>");
        out.print(request.getLocalName() + "<br>");
        out.print(request.getLocalPort() + "<br>");
        out.print(request.getRemoteAddr() + "<br>");
        out.print(request.getRemoteHost() + "<br>");
        out.print(request.getRemotePort() + "<br>");
        out.print(request.isSecure() + "<br>");
        out.print(request.getServerName() + "<br>");*/
        
        String ipAddr = request.getRemoteAddr();
        byte[] secret = new byte[32];
        String secretStr = "Some secret key Some secret key";
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String dateStr = dateFormat.format(new Date());

        secret = "Some secret key Some secret key".getBytes();

        HashMap<String, Object> jwtHeader = new HashMap<String, Object>();
        jwtHeader.put("alg", "HS256");
        jwtHeader.put("typ", "JWT");

        HashMap<String, String> jwtPayload = new HashMap<String, String>();
        jwtPayload.put("ip_address", ipAddr);
        jwtPayload.put("issue_datetime", dateStr);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create().withHeader(jwtHeader).withPayload(jwtPayload).sign(algorithm);
        out.print("ip = " + ipAddr+"<br>");
        out.print("secret = "+secretStr+"<br>");
        out.print("dateStr = "+dateStr+"<br>");
        out.print("got token = "+token+"<br>");

        response.setContentType("text/html");
        chain.doFilter(request, response);
    }


    public void init(FilterConfig filterConfig) throws ServletException {
        
    }


    public void destroy() {
    }
}
