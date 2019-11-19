package votingsystembackend;

import cblog.Logger;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import votingsystem.LogManager;
import votingsystem.VotingSystem;

@WebServlet("/LogOut")
public class LogOutServlet extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final VotingSystem vs = VotingSystem.initialize();
        final Logger LOGGER = LogManager.getLogger();
        HttpSession session = req.getSession(false);
        //result vairables
        int resultCode = 1;
        String result = "pass", error = "";
        //response map
        final Map<String, Object> resMap = new HashMap<>();
        if(session != null) {
            session.invalidate();
        }
        else {
            result = "fail";
            resultCode = 0;
            error = "User not logged in";
        }
        //put result to resMap
        resMap.put(VotingSystemBackend.RESULT_CODE_KEY, resultCode);
        resMap.put(VotingSystemBackend.RESULT_KEY, result);
        resMap.put(VotingSystemBackend.ERROR_KEY, error);
        resMap.put(VotingSystemBackend.USER_KEY, null);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            res.getWriter().write(new Gson().toJson(resMap));
        } catch (IOException ex) {
            if(LogManager.isEnabled())
                LOGGER.error(ex.toString());
            System.out.println(ex.toString());
        }
    }
    
}
