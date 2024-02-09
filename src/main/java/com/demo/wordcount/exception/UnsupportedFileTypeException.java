package com.demo.wordcount.exception;

public class UnsupportedFileTypeException extends IllegalArgumentException {
    public UnsupportedFileTypeException(String field, String value) {
        super(String.format("File type not supported : {%s=%s}", field, value));
    }
}