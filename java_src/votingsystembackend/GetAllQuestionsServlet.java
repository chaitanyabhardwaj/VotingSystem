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
import java.util.ArrayList;
import java.util.List;
import votingsystem.DBContract;
import votingsystem.Question;
import votingsystem.User;

@WebServlet("/GetAllQuestions")
public class GetAllQuestionsServlet extends HttpServlet {
    
    final static private String USER_BEAN_SESSION = "user_obj";
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        final VotingSystem vs = VotingSystem.initialize();
        final Logger LOGGER = LogManager.getLogger();
        //result vairables
        int resultCode = 1;
        String result = "pass", error = "";
        List<QuestionBean> quesBean = new ArrayList<>();
        //response map
        final Map<String, Object> resMap = new HashMap<>();
        //session
        HttpSession session = req.getSession(false);
        if(session != null) {
            final UserBean ubean = (UserBean) session.getAttribute(USER_BEAN_SESSION);
            final User user = vs.pullUser(ubean.username);
            List<Map> registerList = vs.getRegisterByUser(user.UID);
            int qid;
            boolean v;
            QuestionBean qbean;
            Question quesArr[] = vs.pullQuestions();
            if(LogManager.isEnabled()) {
                LOGGER.debug("Got quesArr = " + quesArr.length);
            }
            for(Question q : quesArr) {
                qid = q.getID();
                v = false;
                for(Map m : registerList) {
                    if((int) m.get(DBContract.TableUserQuesOption.COL_QUES_ID) == qid) {
                        v = true;
                        break;
                    }
                }
                qbean = VotingSystemBackend.questionToQuestionBean(q);
                qbean.setVoted(v);
                quesBean.add(qbean);
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
