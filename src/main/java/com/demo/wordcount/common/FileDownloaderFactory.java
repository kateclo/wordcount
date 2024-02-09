package com.demo.wordcount.common;

import com.demo.wordcount.exception.UnsupportedLinkException;

public class FileDownloaderFactory {
    public static FileDownloader getFileDownloader(String path) throws UnsupportedLinkException {
        if (UrlUtil.isLinkLocalFilepathAndExists(path)) {
            return new LocalFileDownloader();
        } else if (UrlUtil.isHttpOrHttpsUrl(path)) {
            return new WebUrlFileDownloader();
        } else {
            throw new UnsupportedLinkException(path);
        }
    }
}
