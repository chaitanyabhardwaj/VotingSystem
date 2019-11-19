/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package votingsystembackend;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chaitanyabhardwaj
 */
public class QuestionBean {
    
    public String body;
    public UserBean setter;
    public List<OptionBean> optionList = new ArrayList<>();
    public boolean isOpen, voted;
    
    //getter
    public String getBody() {
        return body;
    }
    
    public UserBean getSetter() {
        return setter;
    }
    
    public List getOptionList() {
        return optionList;
    }
    
    public boolean getIsOpen() {
        return isOpen;
    }
    
    public boolean getVoted() {
        return voted;
    }
    
    //setter
    public void setBody(String body) {
        this.body = body;
    }
    
    public void setSetter(UserBean setter) {
        this.setter = setter;
    }
    
    public void setOptionList(List optionList) {
        this.optionList = optionList;
    }
    
    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
    
    public void setVoted(boolean voted) {
        this.voted = voted;
    }
    
}
