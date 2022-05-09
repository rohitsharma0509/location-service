package com.scb.location.exception;


/**
 * Custom runtime exception for empty id list
 *
 * @Exception Empty ID List
 *
 */
public class NoResultFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoResultFoundException(String message) {
        super(message);
    }

}