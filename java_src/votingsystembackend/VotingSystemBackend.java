package votingsystembackend;

import java.util.ArrayList;
import java.util.List;
import votingsystem.Option;
import votingsystem.Question;
import votingsystem.User;

public class VotingSystemBackend {

    //response keys
    final public static String RESULT_CODE_KEY = "result_code";
    final public static String RESULT_KEY = "result";
    final public static String ERROR_KEY = "error";
    final public static String USER_KEY = "user";
    final public static String QUES_KEY = "question";
    
    public static UserBean userToUserBean(User user) {
        UserBean userBean = new UserBean();
        userBean.displayName = user.getDisplayName();
        userBean.username = user.USERNAME;
        return userBean;
    }
    
    public static QuestionBean questionToQuestionBean(Question ques) {
        QuestionBean qbean = new QuestionBean();
        UserBean ubean = userToUserBean(ques.SETTER);
        Option optArr[] = ques.getAllOptions();
        List<OptionBean> obeanList = new ArrayList<>();
        for(Option o : optArr)
            obeanList.add(optionToOptionBean(o));
        qbean.setBody(ques.getBody());
        qbean.setSetter(ubean);
        qbean.setOptionList(obeanList);
        qbean.setIsOpen(ques.isOpened());
        qbean.setVoted(false);
        return qbean;
    }
    
    public static OptionBean optionToOptionBean(Option opt) {
        OptionBean obean = new OptionBean();
        obean.setBody(opt.getBody());
        obean.setVoteCount(opt.getVoteCount());
        obean.setId(opt.getID());
        return obean;
    }
    
}
