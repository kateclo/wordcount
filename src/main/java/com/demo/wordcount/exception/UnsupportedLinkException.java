package com.demo.wordcount.exception;

public class UnsupportedLinkException extends IllegalArgumentException {
    public UnsupportedLinkException(String value) {
        super(String.format("Link is not supported : %s", value));
    }
}
