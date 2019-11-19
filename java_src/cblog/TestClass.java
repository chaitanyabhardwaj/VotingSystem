package cblog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

public class TestClass {

    public static void main(String[] args) throws IOException {
        try {
            Logger log = Logger.getLogger("sample.log", true);
            log.log("Hey now");
            log.debug("Heyyy noww");
            log.error("OOOOOOOOoOOooLALALALALAL hey now it is aaawwwesome!");
            //log.close();
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
