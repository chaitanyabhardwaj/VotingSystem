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
public class InvalidURLException extends Exception {

    /**
     * Creates a new instance of <code>InvalidURLException</code> without detail message.
     */
    public InvalidURLException() {
    }

    /**
     * Constructs an instance of <code>InvalidURLException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidURLException(String msg) {
        super(msg);
    }
}
