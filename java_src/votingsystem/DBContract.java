package votingsystem;

public interface DBContract {

    static final String TABLE_OPT = "option_table";
    static final String TABLE_QUES = "question_table";
    static final String TABLE_USER = "user_table";
    static final String TABLE_USER_QUES_OPT = "user_question_option_table";
    
    static interface TableOpt {
        
        static final String COL_ID = "ID";
        static final String COL_BODY = "body";
        static final String COL_QUES_ID = "question_id";
        static final String COL_VOTE_COUNT = "vote_count";
        static final String COL_CREATED_ON = "created_on";
        
    }
    
    static interface TableQues {
        
        static final String COL_ID = "ID";
        static final String COL_BODY = "body";
        static final String COL_SETTER_ID = "setter_id";
        static final String COL_CREATED_ON = "created_on";
        static final String COL_TOP_RATED_OPT = "top_rated_option";
        static final String COL_OPEN_STATUS = "open_status";
        
    }
    
    static interface TableUser {
        
        static final String COL_ID = "ID";
        static final String COL_NAME = "display_name";
        static final String COL_USERNAME = "username";
        static final String COL_PASSWD = "u_passwd_c";
        static final String COL_USER_TYPE = "user_type";
        
    }
    
    static interface TableUserQuesOption {
        
        static final String COL_ID = "ID";
        static final String COL_QUES_ID = "question_id";
        static final String COL_OPT_ID = "option_id";
        static final String COL_USER_ID = "user_id";
        
    }
    
}
