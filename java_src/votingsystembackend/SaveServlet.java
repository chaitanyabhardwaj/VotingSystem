package votingsystembackend;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

@WebServlet("/SaveS")
public class SaveServlet extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String str = req.getParameter("test_name");
        HttpSession s = req.getSession();
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        PrintWriter pw = res.getWriter();
        if(s.isNew()) {
            final Cookie cookie = new Cookie("JSESSIONID", s.getId());
            cookie.setMaxAge(Integer.MAX_VALUE);
            res.addCookie(cookie);
            s.setAttribute("name", str);
            pw.write("New!");
            pw.write(s.getId());
        }
        pw.write("Saved = " + str);
    }
    
}
