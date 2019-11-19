package votingsystem;

import cblog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LogManager {
    
    private static Logger logger;
    private static boolean enabled;
    private static File logFile;
    
    final public static String DEFAULT_LOG_PATH = "voting_system.log";
    
    private LogManager() {
        //non-initialisable
    }
    
    public static void enableLogging(File logFile) throws FileNotFoundException {
        if(!logFile.exists()) throw new FileNotFoundException();
        LogManager.logFile = logFile;
        LogManager.enabled = true;
    }
    
    public static void disableLogging() {
        LogManager.enabled = false;
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static Logger getLogger() {
        //SINGLETON PATTERN
        if(LogManager.logger == null) {
            if(!enabled) throw new IllegalStateException("Cannot return Logger. Logging is not enabled.");
            Logger l = null;
            try {
                l = Logger.getLogger(logFile,true);
                l.log("LOGGER TURNED ON!");
            }
            catch(IOException ex) {
                System.out.println(ex.toString());
            }
            LogManager.logger = l;
        }
        return LogManager.logger;
    }
    
}
