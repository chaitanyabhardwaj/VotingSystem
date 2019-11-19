package votingsystem;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestClass {

    public static void main(String[] args) throws IOException {
        TestClass t = new TestClass();
        TestClass.DB tdb = new TestClass.DB();
        tdb.run();
        //t.testUser();
        //t.testQuestion();
        //t.testVoting();
        //t.testDisplay();
    }
    
    void testUser() {
        VotingSystem vs = VotingSystem.initialize();
        User user = User.createUser("a", "a", "a", UserType.ADMIN);
        try {
            vs.pushUser(user);
        } catch (UnframedException ex) {
            System.out.println(ex.toString());
        }
    }
    
    void testQuestion() {
        VotingSystem vs = VotingSystem.initialize();
        User user = User.getAuthUser("a", "a");
        Option option1 = Option.frame("No");
        Option option2 = Option.frame("NO");
        Option option3 = Option.frame("Are you kidding me");
        Question ques = Question.frame("Should Modi be held Prime Minister again?", user, new Option[]{option1,option2,option3});
        try {
            vs.pushQuestion(ques);
        } catch (PermissionDeniedException | UnframedException ex) {
            Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void testVoting() {
        VotingSystem vs = VotingSystem.initialize();
        Question ques = vs.pullQuestion(1);
        Option opt[] = ques.getAllOptions();
        User user = vs.pullUser("a");
        vs.voteOption(opt[0],user);
    }
    
    void testDisplay() {
        VotingSystem vs = VotingSystem.initialize();
        Question ques[] = vs.pullQuestions();
        Option optArr[];
        for(Question q : ques) {
            System.out.println(q.getBody());
            optArr = q.getAllOptions();
            for(Option o : optArr) {
                System.out.println(o.getBody());
            }
            System.out.println("Posted On : " + new Date(q.CREATED_ON).toString());
            System.out.println("Author : " + q.SETTER.getDisplayName());
            System.out.println();
        }
    }
    
    private static class DB {
        
        final private static String USER_TABLE_C = "CREATE TABLE " + DBContract.TABLE_USER + "("
                + DBContract.TableUser.COL_ID + " INT NOT NULL AUTO_INCREMENT,"
                + DBContract.TableUser.COL_NAME + " VARCHAR(150) NOT NULL,"
                + DBContract.TableUser.COL_USERNAME + " VARCHAR(150) NOT NULL UNIQUE,"
                + DBContract.TableUser.COL_PASSWD + " VARCHAR(50) NOT NULL,"
                + DBContract.TableUser.COL_USER_TYPE + " INT NOT NULL,"
                + " PRIMARY KEY (" + DBContract.TableUser.COL_ID + "));";
        
        final private static String QUES_TABLE_C = "CREATE TABLE " + DBContract.TABLE_QUES + "("
                + DBContract.TableQues.COL_ID + " INT NOT NULL AUTO_INCREMENT,"
                + DBContract.TableQues.COL_BODY + " VARCHAR(500) NOT NULL,"
                + DBContract.TableQues.COL_SETTER_ID + " INT NOT NULL,"
                + DBContract.TableQues.COL_CREATED_ON + " BIGINT NOT NULL,"
                + DBContract.TableQues.COL_TOP_RATED_OPT + " INT,"
                + DBContract.TableQues.COL_OPEN_STATUS + " INT,"
                + " PRIMARY KEY (" + DBContract.TableQues.COL_ID + "));";
        
        final private static String OPT_TABLE_C = "CREATE TABLE " + DBContract.TABLE_OPT + "("
                + DBContract.TableOpt.COL_ID + " INT NOT NULL AUTO_INCREMENT,"
                + DBContract.TableOpt.COL_BODY + " VARCHAR(100) NOT NULL,"
                + DBContract.TableOpt.COL_QUES_ID + " INT NOT NULL,"
                + DBContract.TableOpt.COL_VOTE_COUNT + " INT,"
                + DBContract.TableOpt.COL_CREATED_ON + " BIGINT NOT NULL,"
                + " PRIMARY KEY (" + DBContract.TableOpt.COL_ID + "));";
        
        final private static String USER_QUES_OPT_TABLE_C = "CREATE TABLE " + DBContract.TABLE_USER_QUES_OPT + "("
                + DBContract.TableUserQuesOption.COL_ID + " INT NOT NULL AUTO_INCREMENT,"
                + DBContract.TableUserQuesOption.COL_USER_ID + " INT NOT NULL,"
                + DBContract.TableUserQuesOption.COL_QUES_ID + " INT NOT NULL,"
                + DBContract.TableUserQuesOption.COL_OPT_ID + " INT NOT NULL,"
                + " PRIMARY KEY (" + DBContract.TableOpt.COL_ID + "));";
        
        public void run() {
            try {
                DatabaseManager db = DatabaseManager.getInstance(VotingSystem.DB_PATH, VotingSystem.DB_USERNAME, VotingSystem.DB_PASSWD);
                if(!db.databaseExists()) {
                    db.createDatabase();
                    db.openConnection();
                    //create tables
                    db.executeUpdate(USER_TABLE_C);
                    db.executeUpdate(QUES_TABLE_C);
                    db.executeUpdate(OPT_TABLE_C);
                    db.executeUpdate(USER_QUES_OPT_TABLE_C);
                    db.closeConnection();
                }
            } catch (InvalidURLException | SQLException ex) {
                Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
