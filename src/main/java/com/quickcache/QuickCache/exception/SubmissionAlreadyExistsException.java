package com.quickcache.QuickCache.exception;


public class SubmissionAlreadyExistsException extends RuntimeException {
    
    public SubmissionAlreadyExistsException(String message) {
        super(message);
    }
    
    public SubmissionAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
