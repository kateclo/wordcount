package com.demo.wordcount.common;

import com.demo.wordcount.exception.FileCreationException;
import com.demo.wordcount.exception.FileDownloadException;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class LocalFileDownloader implements FileDownloader {
    @Override
    public Path download(String sourceFilename, String destinationDir) throws FileDownloadException {
        log.info(String.format("[Local File Download] Downloading file : %s", sourceFilename));
        return FileUtil.copyFileToDir(sourceFilename, destinationDir);
    }
}
