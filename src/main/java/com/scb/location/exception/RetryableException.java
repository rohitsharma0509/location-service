package com.scb.location.exception;


/**
 * Custom runtime exception for empty id list
 *
 * @Exception Empty ID List
 *
 */
public class RetryableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RetryableException(String message) {
        super(message);
    }

}