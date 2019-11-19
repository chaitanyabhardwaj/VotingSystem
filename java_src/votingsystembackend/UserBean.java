/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package votingsystembackend;

/**
 *
 * @author chaitanyabhardwaj
 */
public class UserBean {
    
    public String displayName, username;
    
    //getters
    public String getDisplayName() {
        return displayName;
    }
    
    public String getUsername() {
        return username;
    }
    
    //setters
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
}
