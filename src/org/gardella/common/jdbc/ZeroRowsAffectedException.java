package org.gardella.common.jdbc;

public class ZeroRowsAffectedException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public ZeroRowsAffectedException(String message) {
        super(message);
    }
    
}
