package com.demo.wordcount.exception;

public class FileCreationException extends IllegalOperationException {
    public FileCreationException(String mainDir, String subDir) {
        super(String.format("Error creating file or directory %s in %s", subDir, mainDir));
    }
}
