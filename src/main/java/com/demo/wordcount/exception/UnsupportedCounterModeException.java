package com.demo.wordcount.exception;

public class UnsupportedCounterModeException extends IllegalOperationException {
    public UnsupportedCounterModeException(String mode) {
        super(String.format("Mode not supported: %s", mode));
    }
}
