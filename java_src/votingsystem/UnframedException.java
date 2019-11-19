/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package votingsystem;

/**
 *
 * @author chaitanyabhardwaj
 */
public class UnframedException extends Exception {

    /**
     * Creates a new instance of <code>UnframedQuestionException</code> without detail message.
     */
    public UnframedException() {
    }

    /**
     * Constructs an instance of <code>UnframedQuestionException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public UnframedException(String msg) {
        super(msg);
    }
}
