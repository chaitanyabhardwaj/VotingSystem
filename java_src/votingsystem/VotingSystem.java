package votingsystem;

import cblog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotingSystem {
    
    //ENV VAR
    //final public static String ENV_DB_PATH = "CBVSDB_PATH";
    //final public static String ENV_DB_USERNAME = "CBVSDB_USER";
    //final public static String ENV_DB_PASSWD = "CBVSDB_PASSWD";
    final public static String DB_PATH = "//localhost:3306/cb_voting_system_db";
    final public static String DB_USERNAME = "<your_db_username>";
    final public static String DB_PASSWD = "<your_db_password>";
    
    private Logger LOGGER;
    private static DatabaseManager dbmanager;
    private Question.DB questionDB;
    private User.DB userDB;
    private Option.DB optionDB;
    private VotingSystem.DB votingSystemDB;
    
    private VotingSystem() {
        //non-instansiable
    }
    
    public static VotingSystem initialize() {
        VotingSystem vs = new VotingSystem();
        //initialize logger
        try {
            LogManager.enableLogging(new File(LogManager.DEFAULT_LOG_PATH));
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }
        if(LogManager.isEnabled())
            vs.LOGGER = LogManager.getLogger();
        else
            vs.LOGGER = null;
        //initialize database
        try {
            dbmanager = DatabaseManager.getInstance(DB_PATH,DB_USERNAME,DB_PASSWD);
                
        }
        catch(InvalidURLException ex) {
            if(LogManager.isEnabled())
                vs.LOGGER.error(ex.toString());
            else
                System.out.println(ex.toString());
        }
        vs.questionDB = new Question.DB(dbmanager);
        vs.userDB = new User.DB(dbmanager);
        vs.optionDB = new Option.DB(dbmanager);
        vs.votingSystemDB = new VotingSystem.DB(dbmanager);
        return vs;
    }
    
    public void disableLogging() {
        LogManager.disableLogging();
    }
    
    public void pushUser(User user) throws UnframedException {
        if(!user.isFramed()) throw new UnframedException("Pushed User must be framed");
        try {
            final int newId = userDB.store(user);
            if(LogManager.isEnabled())
                LOGGER.info("User created : " + user.USERNAME + " ID(" + newId + ")");
        } catch (SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("User can't be pushed");
            }
        }
    }
    
    public User pullUser(int id) {
        return User.getUser(id);
    }
    
    public User pullUser(String username) {
        return User.getUser(username);
    }
    
    public void pushQuestion(Question ques) throws PermissionDeniedException, UnframedException {
        if(!ques.isFramed()) throw new UnframedException("Pushed Question must be framed");
        if(ques.SETTER.TYPE != UserType.ADMIN) throw new PermissionDeniedException();
        Option opt[] = ques.getAllOptions();
        for(Option o : opt) {
            if(!o.isFramed()) {
                LOGGER.error("Unframed Option ID( " + o.getID() + ") passed.");
                LOGGER.info("Question ID(" + ques.getID() + ") NOT pushed.");
                throw new UnframedException("Pushed Option must be framed!");
            }
        }
        //store questions in db
        try {
            questionDB.store(ques);
            LOGGER.debug("Question ID(" + ques.getID() + ") pushed!");
        }
        catch(SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Question can't be pushed");
            }
            else {
                System.out.println(ex.toString());
            }
        }
    }
    
    public Question pullQuestion(int id) {
        try {
            return questionDB.get(id);
        }
        catch(SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Returning null");
            }
            return null;
        }
    }
    
    public Question[] pullQuestions() {
        try {
            return questionDB.getAll();
        }
        catch(SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Returning null");
            }
            return null;
        }
    }
    
    public Option pullOption(int optId) {
        try {
            return optionDB.get(optId);
        } catch (SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Returning null");
            }
            return null;
        }
    }
    
    public void voteOption(Option opt, User votedBy) {
        opt.vote();
        try {
            optionDB.update(opt);
            //fill the user_ques_opt for vote
            final int oid = opt.getID();
            final int qid = opt.getParentQuestionId();
            final int uid = votedBy.UID;
            votingSystemDB.registerUserQuesVote(uid, qid, oid);
        } catch (SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Option ID(" + opt.getID() + ") Update Failed!");
            }
        }
    }
    
    public List<Map> getRegisterByUser(int uid) {
        try {
            return votingSystemDB.getByUser(uid);
        } catch (SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Returning null");
            }
            return null;
        }
    }
    
    public List<Map> getRegisterByQuestion(int qid) {
        try {
            return votingSystemDB.getByQues(qid);
        } catch (SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Returning null");
            }
            return null;
        }
    }
    
    public List<Map> getRegisterByOption(int oid) {
        try {
            return votingSystemDB.getByOpt(oid);
        } catch (SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Returning null");
            }
            return null;
        }
    }
    
    public static class DB {
        
        private DatabaseManager manager;
        
        public DB(VotingSystem vs) {
            try {
                manager = DatabaseManager.getInstance(VotingSystem.DB_PATH,VotingSystem.DB_USERNAME,VotingSystem.DB_PASSWD);
            }
            catch(InvalidURLException ex) {
                if(LogManager.isEnabled())
                    vs.LOGGER.error(ex.toString());
                System.out.println(ex.toString());
            }
        }
        
        public DB(DatabaseManager manager) {
            this.manager = manager;
        }
        
        public List<Map> getByUser(int uid) throws SQLException {
            final List<Map> list = new ArrayList<>();
            manager.openConnection();
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_USER_QUES_OPT + " WHERE " + DBContract.TableUserQuesOption.COL_USER_ID + " = " + uid + ";");
            int qid, oid;
            Map<String, Integer> map;
            while(set.next()) {
                map = new HashMap<>();
                qid = set.getInt(DBContract.TableUserQuesOption.COL_QUES_ID);
                oid = set.getInt(DBContract.TableUserQuesOption.COL_OPT_ID);
                map.put(DBContract.TableUserQuesOption.COL_USER_ID, uid);
                map.put(DBContract.TableUserQuesOption.COL_QUES_ID, qid);
                map.put(DBContract.TableUserQuesOption.COL_OPT_ID, oid);
                list.add(map);
            }
            manager.closeConnection();
            return list;
        }
        
        public List<Map> getByQues(int qid) throws SQLException {
            final List<Map> list = new ArrayList<>();
            manager.openConnection();
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_USER_QUES_OPT + " WHERE " + DBContract.TableUserQuesOption.COL_QUES_ID + " = " + qid + ";");
            int uid, oid;
            Map<String, Integer> map;
            while(set.next()) {
                map = new HashMap<>();
                uid = set.getInt(DBContract.TableUserQuesOption.COL_USER_ID);
                oid = set.getInt(DBContract.TableUserQuesOption.COL_OPT_ID);
                map.put(DBContract.TableUserQuesOption.COL_USER_ID, uid);
                map.put(DBContract.TableUserQuesOption.COL_QUES_ID, qid);
                map.put(DBContract.TableUserQuesOption.COL_OPT_ID, oid);
                list.add(map);
            }
            manager.closeConnection();
            return list;
        }
        
        public List<Map> getByOpt(int oid) throws SQLException {
            final List<Map> list = new ArrayList<>();
            manager.openConnection();
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_USER_QUES_OPT + " WHERE " + DBContract.TableUserQuesOption.COL_OPT_ID + " = " + oid + ";");
            int qid, uid;
            Map<String, Integer> map;
            while(set.next()) {
                map = new HashMap<>();
                qid = set.getInt(DBContract.TableUserQuesOption.COL_QUES_ID);
                uid = set.getInt(DBContract.TableUserQuesOption.COL_USER_ID);
                map.put(DBContract.TableUserQuesOption.COL_USER_ID, uid);
                map.put(DBContract.TableUserQuesOption.COL_QUES_ID, qid);
                map.put(DBContract.TableUserQuesOption.COL_OPT_ID, oid);
                list.add(map);
            }
            manager.closeConnection();
            return list;
        }
        
        public void registerUserQuesVote(int uid, int qid, int oid) throws SQLException {
            manager.openConnection();
            manager.executeUpdate("INSERT INTO " + DBContract.TABLE_USER_QUES_OPT + "("
            + DBContract.TableUserQuesOption.COL_USER_ID + ", "
            + DBContract.TableUserQuesOption.COL_QUES_ID + ", "
            + DBContract.TableUserQuesOption.COL_OPT_ID + ") VALUES ("
            + uid + ", "
            + qid + ", "
            + oid + ");");
            manager.closeConnection();
        }
        
    }
    
}
