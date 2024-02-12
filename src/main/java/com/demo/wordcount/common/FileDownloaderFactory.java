package com.demo.wordcount.common;

import com.demo.wordcount.exception.UnresolvedSourceException;

public class FileDownloaderFactory {
    public static FileDownloader getFileDownloader(String path) throws UnresolvedSourceException {
        if (UrlUtil.isLocalFilepathAndExists(path)) {
            return new LocalFileDownloader();
        } else if (UrlUtil.isHttpOrHttpsUrl(path)) {
            return new WebUrlFileDownloader();
        } else {
            throw new UnresolvedSourceException(path);
        }
    }
}
