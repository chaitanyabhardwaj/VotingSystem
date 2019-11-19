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
import votingsystem.VotingSystem;

import cblog.Logger;
import com.google.gson.Gson;
import javax.servlet.http.HttpSession;
import votingsystem.Option;
import votingsystem.PermissionDeniedException;
import votingsystem.Question;
import votingsystem.UnframedException;
import votingsystem.User;
import votingsystem.UserType;

@WebServlet("/CreateQuestion")
public class CreateQuestionServlet extends HttpServlet {
    
    final static private String USER_BEAN_SESSION = "user_obj";
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        final VotingSystem vs = VotingSystem.initialize();
        final Logger LOGGER = LogManager.getLogger();
        //response variables
        int resultCode = 1;
        String result = "pass", error = "";
        QuestionBean quesBean = null;
        //declare response list
        final Map<String, Object> resMap = new HashMap<>();
        //verify session
        HttpSession session =  req.getSession(false);
        if(session != null) {
            //get parameters
            final String quesBody = req.getParameter("question").trim();
            final String optListStr[] = req.getParameterValues("options[]");
            final Option optArr[] = new Option[optListStr.length];
            //create options
            for(int i = 0; i < optArr.length; i++)
                optArr[i] = Option.frame(optListStr[i]);
            final UserBean ubean = (UserBean) session.getAttribute(USER_BEAN_SESSION);
            final User user = vs.pullUser(ubean.username);
            if(user.TYPE == UserType.ADMIN) {
                final Question question = Question.frame(quesBody, user, optArr);
                try {
                    vs.pushQuestion(question);
                    quesBean = VotingSystemBackend.questionToQuestionBean(question);
                } catch (PermissionDeniedException | UnframedException ex) {
                    if(LogManager.isEnabled())
                        LOGGER.error(ex.toString());
                    System.out.println(ex.toString());
                }
            }
            else {
                resultCode = 0;
                result = "fail";
                error = "Unauthentic request";
            }
        }
        else {
            resultCode = 0;
            result = "fail";
            error = "Unauthentic request";
        }
        resMap.put(VotingSystemBackend.RESULT_CODE_KEY, resultCode);
        resMap.put(VotingSystemBackend.RESULT_KEY, result);
        resMap.put(VotingSystemBackend.ERROR_KEY, error);
        resMap.put(VotingSystemBackend.QUES_KEY, quesBean);
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
