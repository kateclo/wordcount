package com.demo.wordcount.exception;

public class UnresolvedSourceException extends IllegalArgumentException {
    public UnresolvedSourceException(String value) {
        super(String.format("Source %s cannot be resolved. Either it doesn't exist, or it is not a supported link.", value));
    }
}
