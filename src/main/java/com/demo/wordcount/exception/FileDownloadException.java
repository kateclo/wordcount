package com.demo.wordcount.exception;

public class FileDownloadException extends IllegalOperationException {
    public FileDownloadException(String filenameToDownload, String destinationDir) {
        super(String.format("Error downloading %s to %s", filenameToDownload, destinationDir));
    }
}
