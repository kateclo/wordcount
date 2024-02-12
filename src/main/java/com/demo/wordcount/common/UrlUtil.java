package com.demo.wordcount.common;

import com.demo.wordcount.exception.FileDownloadException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;


@Slf4j
public class UrlUtil {

    public static final String FILE = "file:///";

    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    public static boolean isLocalFilepathAndExists(String filepathToCheck) {
        try {
            String cleanedFilepath = filepathToCheck;

            if (cleanedFilepath.toLowerCase(Locale.getDefault()).startsWith(FILE)
                    && cleanedFilepath.length() > FILE.length()) {
                cleanedFilepath = filepathToCheck.substring(FILE.length());
            }

            Path filepath = Paths.get(cleanedFilepath);
            return filepath.isAbsolute() && filepath.toFile().isFile();

        } catch (InvalidPathException | NullPointerException | IndexOutOfBoundsException e) {
            log.debug("Not a local filepath : " + filepathToCheck);
            return false;
        }
    }

    public static boolean isHttpOrHttpsUrl(String url) {
        try {
            URI uri = new URI(url);
            String uriScheme = uri.getScheme();

            if (uriScheme != null) {
                return uri.isAbsolute() && (uriScheme.equalsIgnoreCase(HTTP) || uriScheme.equalsIgnoreCase(HTTPS));
            } else {
                log.debug("Not a url: " + url);
                return false;
            }
        } catch (URISyntaxException | NullPointerException e) {
            log.debug("Not a url: " + url);
            return false;
        }
    }

    public static Path copyFileToDir(String webUrl, String destinationDir) throws FileDownloadException {
        try {
            URL url = new URL(webUrl);
            String urlPathName = url.getPath();
            Path urlFilepath = Paths.get(urlPathName);
            String urlFilename = urlFilepath.getFileName().toString();


            Path destinationPath = Paths.get(destinationDir);
            Path completeDestinationPath = destinationPath.resolve(urlFilename);

            try (InputStream in = url.openStream()) {
                Files.copy(in, completeDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                return completeDestinationPath;
            }
        } catch (InvalidPathException | NullPointerException | IOException e) {
            throw new FileDownloadException(webUrl, destinationDir);
        }
    }
}
