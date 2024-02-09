package com.demo.wordcount.exception;

public class FileReadingException extends IllegalOperationException {
    public FileReadingException(String filepath) {
        super(String.format("Error reading contents of %s", filepath));
    }
}
