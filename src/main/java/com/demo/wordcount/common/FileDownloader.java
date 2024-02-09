package com.demo.wordcount.common;

import com.demo.wordcount.exception.FileDownloadException;

import java.nio.file.Path;

public interface FileDownloader {
    Path download(String sourceFilepath, String destinationDir) throws FileDownloadException;
}