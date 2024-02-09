package com.demo.wordcount.exception;

public abstract class IllegalOperationException extends Exception {
    IllegalOperationException(String message) {
        super(message);
    }
}
