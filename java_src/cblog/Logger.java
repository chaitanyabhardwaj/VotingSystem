package cblog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class Logger {
    
    final private BufferedReader R;
    final private PrintWriter W;
    
    private Logger(BufferedReader reader, PrintWriter writer) {
        R = reader;
        W = writer;
    }
    
    public static Logger getLogger(File file)throws FileNotFoundException, IOException {
        if(!file.exists()) throw new FileNotFoundException();
        BufferedReader br = new BufferedReader(new FileReader(file));
        PrintWriter pw = new PrintWriter(new FileWriter(file, true));
        return new Logger(br,pw);
    }
    
    public static Logger getLogger(String path)throws FileNotFoundException, IOException {
        File file = new File(path);
        return getLogger(file);
    }
    
    public static Logger getLogger(File file, boolean createFile) throws IOException {
        if(!createFile) return getLogger(file);
        file.createNewFile();
        return getLogger(file);
    }
    
    public static Logger getLogger(String path, boolean createFile) throws IOException {
        File file = new File(path);
        return getLogger(file, createFile);
    }
    
    public void debug(String str) {
        str = str.trim();
        str = "DEBUG : " + str;
        log(str);
    }
    
    public void error(String str) {
        str = str.trim();
        str = "***ERROR*** : " + str;
        log(str);
    }
    
    public void info(String str) {
        str = str.trim();
        str = "INFO : " + str;
        log(str);
    }
        
    public void log(String str) {
        Date date = new Date();
        str = str.trim();
        str = date.toString() + " : " + str;
        W.write(str);
        W.write("\r\n");
        W.flush();
    }
    
    public void close() throws IOException {
        R.close();
        W.close();
    }
    
}
