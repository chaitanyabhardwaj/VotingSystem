package votingsystembackend;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import votingsystem.LogManager;
import votingsystem.VotingSystem;

import cblog.Logger;
import com.google.gson.Gson;

@WebServlet("/GetUser")
public class GetUserServlet extends HttpServlet {
    
    final static private String USER_BEAN_SESSION = "user_obj";
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        final VotingSystem vs = VotingSystem.initialize();
        final Logger LOGGER = LogManager.getLogger();
        //result vairables
        int resultCode = 1;
        String result = "pass", error = "";
        UserBean user = null;
        //response map
        final Map<String, Object> resMap = new HashMap<>();
        //session
        HttpSession session = req.getSession(false);
        if(session != null) {
            //Logged In
            //return the user
            user = (UserBean) session.getAttribute(USER_BEAN_SESSION);
            if(LogManager.isEnabled()) {
                //LOGGER.info("Returning user ID(" + user.username + ")");
            }
        }
        //put result to resMap
        resMap.put(VotingSystemBackend.RESULT_CODE_KEY, resultCode);
        resMap.put(VotingSystemBackend.RESULT_KEY, result);
        resMap.put(VotingSystemBackend.ERROR_KEY, error);
        resMap.put(VotingSystemBackend.USER_KEY, user);
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
