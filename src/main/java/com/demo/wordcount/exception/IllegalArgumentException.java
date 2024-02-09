package com.demo.wordcount.exception;

public abstract class IllegalArgumentException extends Exception {
    IllegalArgumentException(String message) {
        super(message);
    }
}