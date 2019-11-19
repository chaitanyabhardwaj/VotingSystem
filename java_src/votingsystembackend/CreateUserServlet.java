package votingsystembackend;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import votingsystem.LogManager;
import votingsystem.UnframedException;
import votingsystem.User;
import votingsystem.UserType;
import votingsystem.VotingSystem;

import cblog.Logger;
import com.google.gson.Gson;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

@WebServlet("/CreateUser")
public class CreateUserServlet extends HttpServlet {
    
    final static private String USER_BEAN_SESSION = "user_obj";
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        final VotingSystem vs = VotingSystem.initialize();
        final Logger LOGGER = LogManager.getLogger();
        //get parameters
        String name,username,passwd,type;
        UserType userType;
        
        int resultCode = 1;
        String result = "pass", error = "";
        UserBean ubean = null;
        //declare response list
        final Map<String, Object> resMap = new HashMap<>();
        
        name = req.getParameter("display_name").trim();
        username = req.getParameter("username").trim();
        passwd = req.getParameter("passwd").trim();
        type = req.getParameter("user_type").trim();
        switch(type) {
            case "0" : userType = UserType.ADMIN; break;
            case "1" : userType = UserType.USER; break;
            default :
                userType = UserType.USER;
                if(LogManager.isEnabled()) {
                    LOGGER.debug("Invalid User Type provided = " + type);
                    LOGGER.info("User not created!");
                }
                resultCode = 0;
                result = "fail";
                error = "Invalid User Type provide";
        }
        if(resultCode != 0) {
            //create user
            User user = User.createUser(name, username, passwd, userType);
            try {
                vs.pushUser(user);
            } catch (UnframedException ex) {
                if(LogManager.isEnabled())
                    LOGGER.error(ex.toString());
                System.out.println(ex.toString());
            }
        }
        //session
        HttpSession session = req.getSession();
        User vsUser = User.getAuthUser(username, passwd);
        if(vsUser == null) {
            resultCode = 0;
            result = "fail";
            error = "User not stored";
        }
        else {
            //convert vsUser to UserBean
            ubean = VotingSystemBackend.userToUserBean(vsUser);
            //store the newly created UserBean to session
            session.setAttribute(USER_BEAN_SESSION, ubean);
            //increase session timeout
            final Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setMaxAge(Integer.MAX_VALUE);
            res.addCookie(cookie);
        }
        resMap.put(VotingSystemBackend.RESULT_CODE_KEY, resultCode);
        resMap.put(VotingSystemBackend.RESULT_KEY, result);
        resMap.put(VotingSystemBackend.ERROR_KEY, error);
        resMap.put(VotingSystemBackend.USER_KEY, ubean);
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
