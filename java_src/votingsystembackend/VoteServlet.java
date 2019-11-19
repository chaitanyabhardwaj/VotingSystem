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
import votingsystem.Option;
import votingsystem.Question;
import votingsystem.User;

@WebServlet("/Vote")
public class VoteServlet extends HttpServlet {
    
    final static private String USER_BEAN_SESSION = "user_obj";
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        final VotingSystem vs = VotingSystem.initialize();
        final Logger LOGGER = LogManager.getLogger();
        //result vairables
        int resultCode = 1;
        String result = "pass", error = "";
        QuestionBean qbean = null;
        //response map
        final Map<String, Object> resMap = new HashMap<>();
        //session
        HttpSession session = req.getSession(false);
        if(session != null) {
            //get req parameters
            final String optionId = req.getParameter("option").trim();
            Option opt = vs.pullOption(Integer.parseInt(optionId));
            final UserBean ubean = (UserBean) session.getAttribute(USER_BEAN_SESSION);
            final User user = vs.pullUser(ubean.username);
            if(opt != null) {
                vs.voteOption(opt,user);
                final Question ques = vs.pullQuestion(opt.getParentQuestionId());
                qbean = VotingSystemBackend.questionToQuestionBean(ques);
            }
            else {
                resultCode = 0;
                result = "fail";
                error = "Invalid option ID. Option not found";
            }
        }
        else {
            resultCode = 0;
            result = "fail";
            error = "Unauthentic request";
        }
        //put result to resMap
        resMap.put(VotingSystemBackend.RESULT_CODE_KEY, resultCode);
        resMap.put(VotingSystemBackend.RESULT_KEY, result);
        resMap.put(VotingSystemBackend.ERROR_KEY, error);
        resMap.put(VotingSystemBackend.QUES_KEY, qbean);
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
