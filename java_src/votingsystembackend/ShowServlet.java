package votingsystembackend;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

@WebServlet("/ShowS")
public class ShowServlet extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        if(s == null) {
            res.getWriter().write("Nothing to show man!");
        }
        else {
            String str = s.getAttribute("name").toString();
            res.getWriter().write(str + " = " + s.getId());
        }
    }
    
}
