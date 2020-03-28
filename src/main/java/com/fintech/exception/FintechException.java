package com.fintech.exception;

public class FintechException extends RuntimeException {

    private static final long serialVersionUID = -3252221694239463340L;

    public FintechException(String message) {
        super(message);
    }
    
    public FintechException(String message, final Exception ex) {
        super(message, ex);
    }
    
    public FintechException(final Exception ex) {
        super(FintechException.class.getName(), ex);
    } 
        
}
