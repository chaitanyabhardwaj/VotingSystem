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
public class OptionBean {
    
    public String body;
    public int voteCount;
    public int id;
    
    //getter
    public int getId() {
        return id;
    }
    
    public String getBody() {
        return body;
    }
    
    public int getVoteCount() {
        return voteCount;
    }
    
    //setter
    public void setId(int id) {
        this.id = id;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    
    
}
