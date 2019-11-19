package votingsystem;

import cblog.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Question {
    
    final public static int QUES_CLOSED = 0, QUES_OPENED = 1;
    final public User SETTER;
    final public long CREATED_ON;
    private static Logger LOGGER;
    final public static int BLANK_ID = -999;
    
    final private Question.DB QUES_DB = new Question.DB();
    final private Option.DB OPT_DB = new Option.DB();
    
    private int ID, openStatus;
    private ArrayList<Option> OPT_LIST;
    private String body;
    private int topRatedOptionId;
    private boolean framed;
    
    public Question(int id, String body, User setter, long createdOn, int topRatedOptionId, int openStatus) {
        ID = id;
        this.body = body;
        SETTER = setter;
        CREATED_ON = createdOn;
        this.topRatedOptionId = topRatedOptionId;
        framed = false;
        OPT_LIST = new ArrayList<>();
        this.openStatus = openStatus;
        if(LogManager.isEnabled())
            LOGGER = LogManager.getLogger();
    }
    
    public static Question frame(String body, User setter, Option options[]) {
        Question q = new Question(BLANK_ID, body, setter,
                System.currentTimeMillis(), BLANK_ID, QUES_OPENED);
        q.framed = true;
        q.addOption(options);
        return q;
    }
    
    //getters
    public int getID() {
        return ID;
    }
    
    public String getBody() {
        return body;
    }
    
    public int getVoteCount() {
        int c = 0;
        for(Option opt : OPT_LIST)
            c += opt.getVoteCount();
        return c;
    }
    
    public Option[] getAllOptions() {
        Option opt[] = new Option[OPT_LIST.size()];
        return OPT_LIST.toArray(opt);
    }
    
    public Option getTopRatedOption(boolean refresh) {
        if(refresh) {
            try {
                QUES_DB.refresh(this);
            } catch (SQLException ex) {
                LOGGER.error(ex.toString());
                LOGGER.info("Can't refresh the Question ID(" + ID + ")");
            }
        }
        if(OPT_LIST.isEmpty()) {
            if(LogManager.isEnabled())
                LOGGER.debug("OPT_LIST is empty for question ID(" + ID + ")");
            return null;
        }
        Option topOpt = OPT_LIST.get(0);
        for(Option o : OPT_LIST) {
            if(o.getVoteCount() > topOpt.getVoteCount())
                topOpt = o;
        }
        return topOpt;
    }
    
    public boolean isFramed() {
        return framed;
    }
    
    public boolean isOpened() {
        return openStatus == QUES_OPENED;
    }
    
    //setters
    public void setBody(String str) {
        body = str;
    }
    
    public void resetVote() {
        ///
        //IMPLEMENT
        ///
    }
    
    public void addOption(Option opt) {
        OPT_LIST.add(opt);
    }
    
    public void addOption(Option opt[]) {
        for(Option o : opt)
            addOption(o);
    }
    
    public void open() {
        openStatus = QUES_OPENED;
    }
    
    public void close() {
        openStatus = QUES_CLOSED;
    }
    
    public static class DB {
        
        private DatabaseManager manager;
        
        public DB() {
            try {
                manager = DatabaseManager.getInstance(VotingSystem.DB_PATH,VotingSystem.DB_USERNAME,VotingSystem.DB_PASSWD);
                
            }
            catch(InvalidURLException ex) {
                if(LogManager.isEnabled())
                    LOGGER.error(ex.toString());
                System.out.println(ex.toString());
            }
        }
        
        public DB(DatabaseManager manager) {
            this.manager = manager;
        }
        
        public int getLastID() throws SQLException {
            manager.openConnection();
            ResultSet set = manager.executeQuery("SELECT LAST_INSERTED_ID();");
            if(set.next()) return set.getInt(1);
            manager.closeConnection();
            return -1;
        }
        
        public Question get(int id) throws SQLException {
            manager.openConnection();
            //retrive question
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_QUES + " WHERE ID = " + id + ";");
            if(!set.next()) {
                if(LogManager.isEnabled()) {
                    LOGGER.debug("Invalid Question ID(" + id + ") provided");
                    LOGGER.info("Returning null");
                }
                return null;
            }
            final String body = set.getString(DBContract.TableQues.COL_BODY);
            final int uid = set.getInt(DBContract.TableQues.COL_SETTER_ID);
            final long createdOn = set.getLong(DBContract.TableQues.COL_CREATED_ON);
            final int troID = set.getInt(DBContract.TableQues.COL_TOP_RATED_OPT);
            final int openStatus = set.getInt(DBContract.TableQues.COL_OPEN_STATUS);
            //retrive user
            final User.DB userDB = new User.DB(manager);
            final User user = userDB.get(uid);
            //retrive options
            final Option.DB optDB = new Option.DB(manager);
            final Option optarr[] = optDB.getByQuestion(id);
            Question q = new Question(id,body,user,createdOn,troID,openStatus);
            q.addOption(optarr);
            manager.closeConnection();
            return q;
        }
        
        public Question[] getAll() throws SQLException {
            final ArrayList<Question> quesList = new ArrayList<>();
            manager.openConnection();
            //retrive question
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_QUES + ";");
            String body;
            int id, uid, troID, openStatus;
            long createdOn;
            final User.DB userDB = new User.DB();
            final Option.DB optDB = new Option.DB();
            User user;
            Option optarr[];
            while(set.next()) {
                id = set.getInt(DBContract.TableQues.COL_ID);
                body = set.getString(DBContract.TableQues.COL_BODY);
                uid = set.getInt(DBContract.TableQues.COL_SETTER_ID);
                createdOn = set.getLong(DBContract.TableQues.COL_CREATED_ON);
                troID = set.getInt(DBContract.TableQues.COL_TOP_RATED_OPT);
                openStatus = set.getInt(DBContract.TableQues.COL_OPEN_STATUS);
                //retrive user
                user = userDB.get(uid);
                //retrive options
                optarr = optDB.getByQuestion(id);
                Question q = new Question(id, body,user,createdOn,troID, openStatus);
                q.addOption(optarr);
                quesList.add(q);
                LOGGER.debug("Question ID(" + q.ID + ") added!");
            }
            manager.closeConnection();
            final Question quesArr[] = new Question[quesList.size()];
            return quesList.toArray(quesArr);
        }
        
        public Question[] getByUser(int userId) throws SQLException {
            final ArrayList<Question> quesList = new ArrayList<>();
            manager.openConnection();
            //retrive option
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_QUES + " WHERE " + DBContract.TableQues.COL_SETTER_ID + " = " + userId + ";");
            String body;
            int id, uid, troID, openStatus;
            long createdOn;
            final User.DB userDB = new User.DB();
            final Option.DB optDB = new Option.DB();
            User user;
            Option optarr[];
            while(set.next()) {
                id = set.getInt(DBContract.TableQues.COL_ID);
                body = set.getString(DBContract.TableQues.COL_BODY);
                uid = set.getInt(DBContract.TableQues.COL_SETTER_ID);
                createdOn = set.getLong(DBContract.TableQues.COL_CREATED_ON);
                troID = set.getInt(DBContract.TableQues.COL_TOP_RATED_OPT);
                openStatus = set.getInt(DBContract.TableQues.COL_OPEN_STATUS);
                //retrive user
                user = userDB.get(uid);
                //retrive options
                optarr = optDB.getByQuestion(id);
                Question q = new Question(id, body,user,createdOn,troID, openStatus);
                q.addOption(optarr);
                quesList.add(q);
                LOGGER.debug("Question ID(" + q.ID + ") added!");
            }
            manager.closeConnection();
            final Question quesArr[] = new Question[quesList.size()];
            return quesList.toArray(quesArr);
        }
        
        public int store(Question ques) throws SQLException {
            manager.openConnection();
            //store question
            int openStatus = (ques.isOpened()) ? 1 : 0;
            manager.executeUpdate("INSERT INTO " + DBContract.TABLE_QUES + "("
            + DBContract.TableQues.COL_BODY + ", "
            + DBContract.TableQues.COL_SETTER_ID + ", "
            + DBContract.TableQues.COL_CREATED_ON + ", "
            + DBContract.TableQues.COL_TOP_RATED_OPT + ", "
            + DBContract.TableQues.COL_OPEN_STATUS + ") VALUES ("
            + "'"+ ques.getBody() + "', "
            + ques.SETTER.UID+ ", "
            + ques.CREATED_ON + ", "
            + ques.getTopRatedOption(false).getID() + ", "
            + openStatus + ");");
            //get inserted question's id
            final int quesNewID = manager.getAutoIncrementValue();
            //update questionId in the obj
            ques.ID = quesNewID;
            if(quesNewID < 0) {
                if(LogManager.isEnabled()) {
                    LOGGER.error("AUTO_INCREAMENT ID NOT FOUND FOR Question ID(" + ques.ID + ")");
                    LOGGER.info("Throwing NullPointerException");
                }
                throw new NullPointerException("AUTO_INCREMENT value of Question can't be null");
            }
            //store options
            final Option.DB optDB = new Option.DB(manager);
            for (Option o : ques.OPT_LIST) {
                o.setParentQuestionId(quesNewID);
                optDB.store(o);
            }
            manager.closeConnection();
            return quesNewID;
        }
        
        public void update(Question q) throws SQLException {
            manager.openConnection();
            refresh(q);
            manager.executeUpdate("UPDATE " + DBContract.TABLE_QUES + " SET "
                    + DBContract.TableQues.COL_BODY + " = '" + q.getBody() + "', "
                    + DBContract.TableQues.COL_TOP_RATED_OPT + " = " + q.getTopRatedOption(false)+ ", "
                    + DBContract.TableQues.COL_OPEN_STATUS + " = " + q.openStatus
                    + " WHERE " + DBContract.TableOpt.COL_ID + " = " + q.ID + ";");
            manager.closeConnection();
        }
        
        public void refresh(Question q) throws SQLException {
            Option.DB optDB = new Option.DB();
            Option optArr[] = optDB.getByQuestion(q.ID);
            if(optArr != null && optArr.length == q.OPT_LIST.size()) {
                q.OPT_LIST = new ArrayList<>(Arrays.asList(optArr));
                LOGGER.debug("OPT_LIST updated");
            }
            else {
                LOGGER.error("OPT_LIST not updated");
                LOGGER.debug("OPT_LIST len -> " + q.OPT_LIST.size());
                if(optArr != null)
                    LOGGER.debug("OptArr len -> " + optArr.length);
                else
                    LOGGER.debug("OptArr is null");
            }
        }
        
    }
    
}
