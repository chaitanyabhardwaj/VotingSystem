package votingsystem;

import cblog.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {
    
    final public UserType TYPE;
    final public int UID;
    final public String USERNAME;
    final private static User.DB USER_DB = new User.DB();
    
    private static Logger LOGGER;
    final public static int BLANK_ID = -999;
    
    private boolean framed;
    private String displayName;
    private String passwd;
    
    public User(int uid, String displayName, String username, String passwd, UserType type) {
        UID = uid;
        this.displayName = displayName;
        USERNAME = username;
        this.passwd = passwd;
        TYPE = type;
        if(LogManager.isEnabled())
            LOGGER = LogManager.getLogger();
    }
    
    public static User createUser(String displayName, String username, String passwd, UserType type) {
        User user = new User(BLANK_ID, displayName, username, passwd, type);
        user.framed = true;
        return user;
    }
    
    public static User getUser(String username) {
        try {
            return USER_DB.get(username);
        } catch (SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Got null user");
            }
            System.out.println(ex.toString());
            return null;
        }
    }
    
    public static User getUser(int uid) {
        try {
            return USER_DB.get(uid);
        } catch (SQLException ex) {
            if(LogManager.isEnabled()) {
                LOGGER.error(ex.toString());
                LOGGER.info("Got null user");
            }
            System.out.println(ex.toString());
            return null;
        }
    }
    
    public static User getAuthUser(String username, String passwd) {
        User user = getUser(username);
        if(user.passwd.equals(passwd)) return user;
        if(LogManager.isEnabled())
            LOGGER.debug("Wrong Passwd! Returning null");
        return null;
    }
    
    public static User getAuthUser(int uid, String passwd) {
        User user = getUser(uid);
        if(user.passwd.equals(passwd)) return user;
        if(LogManager.isEnabled())
            LOGGER.debug("Wrong Passwd! Returning null");
        return null;
    }
    
    //getter
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isFramed() {
        return framed;
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
        
        public User get(int id) throws SQLException {
            manager.openConnection();
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_USER + " WHERE ID = " + id + ";");
            if(!set.next()) {
                if(LogManager.isEnabled()) {
                    LOGGER.debug("User ID(" + id + ") not found");
                    LOGGER.info("Returning null");
                }
                return null;
            }
            final String name = set.getString(DBContract.TableUser.COL_NAME);
            final String username = set.getString(DBContract.TableUser.COL_USERNAME);
            final String passwd = set.getString(DBContract.TableUser.COL_PASSWD);
            final int typeInt = set.getInt(DBContract.TableUser.COL_USER_TYPE);
            final UserType type = typeInt == 0 ? UserType.ADMIN : UserType.USER;
            User newUser = new User(id,name,username,passwd,type);
            //every stored user is framed!
            newUser.framed = true;
            manager.closeConnection();
            return newUser;
        }
        
        public User get(String username) throws SQLException {
            manager.openConnection();
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_USER + " WHERE " + DBContract.TableUser.COL_USERNAME + " = '" + username + "';");
            if(!set.next()) {
                if(LogManager.isEnabled()) {
                    LOGGER.debug("User username(" + username + ") not found");
                    LOGGER.info("Returning null");
                }
                return null;
            }
            final int id = set.getInt(DBContract.TableUser.COL_ID);
            final String name = set.getString(DBContract.TableUser.COL_NAME);
            final String passwd = set.getString(DBContract.TableUser.COL_PASSWD);
            final int typeInt = set.getInt(DBContract.TableUser.COL_USER_TYPE);
            final UserType type = typeInt == 0 ? UserType.ADMIN : UserType.USER;
            manager.closeConnection();
            User newUser = new User(id,name,username,passwd,type);
            //every stored user is framed!
            newUser.framed = true;
            return newUser;
        }
        
        public int store(User user) throws SQLException {
            final int type = user.TYPE == UserType.ADMIN ? 0 : 1;
            manager.openConnection();
            manager.executeUpdate("INSERT INTO " + DBContract.TABLE_USER + "("
            + DBContract.TableUser.COL_NAME + ", "
            + DBContract.TableUser.COL_USERNAME + ", "
            + DBContract.TableUser.COL_PASSWD + ", "
            + DBContract.TableUser.COL_USER_TYPE + ") VALUES ("
            + "'" + user.getDisplayName() + "', "
            + "'" + user.USERNAME + "', "
            + "'" + user.passwd + "', "
            + type + ");");
            final int userNewID = manager.getAutoIncrementValue();
            manager.closeConnection();
            return userNewID;
        }
        
        //USE ONLY FOR DEBUGGING
        public User[] getAllUsers() throws SQLException {
            final ArrayList<User> userList = new ArrayList<>();
            manager.openConnection();
            ResultSet set = manager.executeQuery("SELECT * FROM " + DBContract.TABLE_USER);
            int id,typeInt;
            String name, username, passwd;
            UserType type;
            while(set.next()) {
                id = set.getInt(DBContract.TableUser.COL_ID);
                name = set.getString(DBContract.TableUser.COL_NAME);
                username = set.getString(DBContract.TableUser.COL_USERNAME);
                passwd = set.getString(DBContract.TableUser.COL_PASSWD);
                typeInt = set.getInt(DBContract.TableUser.COL_USER_TYPE);
                type = typeInt == 0 ? UserType.ADMIN : UserType.USER;
                userList.add(new User(id,name,username,passwd,type));
            }
            manager.closeConnection();
            final User userArr[] = new User[userList.size()];
            return userList.toArray(userArr);
        }
        
    }
    
}
