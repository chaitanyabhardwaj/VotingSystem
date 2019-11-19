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
import votingsystem.User;

import cblog.Logger;
import com.google.gson.Gson;
import javax.servlet.http.Cookie;

@WebServlet("/GetUserAuth")
public class GetUserAuthServlet extends HttpServlet {
    
    final static private String USER_BEAN_SESSION = "user_obj";
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        final VotingSystem vs = VotingSystem.initialize();
        final Logger LOGGER = LogManager.getLogger();
        //get parameters
        String username,passwd;
        username = req.getParameter("username").trim();
        passwd = req.getParameter("passwd").trim();
        //result vairables
        int resultCode = 1;
        String result = "pass", error = "";
        UserBean user = null;
        //response map
        final Map<String, Object> resMap = new HashMap<>();
        //session
        HttpSession session = req.getSession(false);
        if(session == null) {
            //Not logged In
            //Check the paramters
            if(passwd.length() < 4 || username.length() < 4) {
                resultCode = 0;
                result = "fail";
                error = "Invalid auth credentials";
            }
            else {
                //Parameters ok!
                //Start new session
                //TODO : GET AUTH USER : MORE EFF!
                session = req.getSession();
                User vsUser = User.getAuthUser(username, passwd);
                if(vsUser == null) {
                    resultCode = 0;
                    result = "fail";
                    error = "Invalid auth credentials";
                }
                else {
                    //convert vsUser to UserBean
                    user = VotingSystemBackend.userToUserBean(vsUser);
                    //store the newly created UserBean to session
                    session.setAttribute(USER_BEAN_SESSION, user);
                    //increase session timeout
                    final Cookie cookie = new Cookie("JSESSIONID", session.getId());
                    cookie.setMaxAge(Integer.MAX_VALUE);
                    res.addCookie(cookie);
                }
            }
        }
        else {
            //Already Logged In
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
