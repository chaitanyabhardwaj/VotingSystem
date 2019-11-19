package votingsystem;

import cblog.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Option {
    
    final public long CREATED_ON;
    private static Logger LOGGER;
    final public static int BLANK_ID = -999;
    
    private int ID;
    private int quesID;
    private boolean framed;
    private String body;
    private int voteCount;
    
    public Option(int id, String body, int quesID, int voteCount, long createdOn) {
        ID = id;
        this.body = body;
        this.quesID = quesID;
        this.voteCount = voteCount;
        CREATED_ON = createdOn;
        framed = false;
        if(LogManager.isEnabled())
            LOGGER = LogManager.getLogger();
    }
    
    public static Option frame(String body) {
        Option option = new Option(BLANK_ID, body, BLANK_ID, 0, System.currentTimeMillis());
        option.framed = true;
        return option;
    }
    
    //getters
    public int getID() {
        return ID;
    }
    
    public String getBody() {
        return body;
    }
    
    public int getParentQuestionId() {
        return quesID;
    }
    
    public int getVoteCount() {
        return voteCount;
    }
    
    public boolean isFramed() {
        return framed;
    }
    
    //setters
    public void setBody(String str) {
        body = str;
    }
    
    public void setParentQuestionId(int id) {
        quesID = id;
    }
    
    public void vote() {
        voteCount++;
    }
    
    public void setVoteCount(int count) {
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
        
        public Option get(int id) throws SQLException {
            manager.openConnection();
            //retrive option
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_OPT + " WHERE ID = " + id + ";");
            if(!set.next()) {
                if(LogManager.isEnabled()) {
                    LOGGER.debug("Invalid Option ID(" + id + ") provided");
                    LOGGER.info("Returning null");
                }
                return null;
            }
            final String body = set.getString(DBContract.TableOpt.COL_BODY);
            final int qid = set.getInt(DBContract.TableOpt.COL_QUES_ID);
            final int voteCount = set.getInt(DBContract.TableOpt.COL_VOTE_COUNT);
            final long createdOn = set.getLong(DBContract.TableOpt.COL_CREATED_ON);
            final Option option = new Option(id, body, qid, voteCount, createdOn);
            manager.closeConnection();
            return option;
        }
        
        public Option[] getByQuestion(int quesID) throws SQLException {
            String body;
            int qid,voteCount,oid;
            long createdOn;
            final ArrayList<Option> optList = new ArrayList<>();
            manager.openConnection();
            //retrive option
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_OPT + " WHERE " + DBContract.TableOpt.COL_QUES_ID + " = " + quesID + ";");
            while(set.next()) {
                oid = set.getInt(DBContract.TableOpt.COL_ID);
                body = set.getString(DBContract.TableOpt.COL_BODY);
                qid = set.getInt(DBContract.TableOpt.COL_QUES_ID);
                voteCount = set.getInt(DBContract.TableOpt.COL_VOTE_COUNT);
                createdOn = set.getLong(DBContract.TableOpt.COL_CREATED_ON);
                optList.add(new Option(oid, body, qid, voteCount, createdOn));
            }
            manager.closeConnection();
            final Option optArr[] = new Option[optList.size()];
            return optList.toArray(optArr);
        }
        
        public int store(Option opt) throws SQLException {
            if(LogManager.isEnabled()) {
                LOGGER.debug("Storing Option");
            }
            manager.openConnection();
            manager.executeUpdate("INSERT INTO " + DBContract.TABLE_OPT + "("
            + DBContract.TableOpt.COL_BODY + ", "
            + DBContract.TableOpt.COL_QUES_ID + ", "
            + DBContract.TableOpt.COL_VOTE_COUNT + ", "
            + DBContract.TableOpt.COL_CREATED_ON + ") VALUES ("
            + "'" + opt.getBody() + "', "
            + opt.getParentQuestionId() + ", "
            + opt.getVoteCount() + ", "
            + opt.CREATED_ON + ");");
            final int optNewID = manager.getAutoIncrementValue();
            //update the new id to the object
            opt.ID = optNewID;
            manager.closeConnection();
            return optNewID;
        }
        
        public void update(Option opt) throws SQLException {
            manager.openConnection();
            manager.executeUpdate("UPDATE " + DBContract.TABLE_OPT + " SET "
                    + DBContract.TableOpt.COL_BODY + " = '" + opt.getBody() + "', "
                    + DBContract.TableOpt.COL_VOTE_COUNT + " = " + opt.getVoteCount()
                    + " WHERE " + DBContract.TableOpt.COL_ID + " = " + opt.ID + ";");
            manager.closeConnection();
        }
        
    }
    
}
