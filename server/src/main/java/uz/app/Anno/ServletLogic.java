package uz.app.Anno;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

interface ServletLogic {
    public void process(BaseModule module, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException;
}
