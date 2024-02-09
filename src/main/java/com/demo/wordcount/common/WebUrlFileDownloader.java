package com.demo.wordcount.common;

import com.demo.wordcount.exception.FileDownloadException;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class WebUrlFileDownloader implements FileDownloader {
    @Override
    public Path download(String webUrl, String destinationDir) throws FileDownloadException {
        log.info(String.format("[Web File Download] Downloading file : %s", webUrl));
        return UrlUtil.copyFileToDir(webUrl, destinationDir);
    }
}

