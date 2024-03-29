package com.demo.wordcount.common;

import com.demo.wordcount.exception.FileCreationException;
import com.demo.wordcount.exception.FileDownloadException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;


@Slf4j
public class FileUtil {
    private static final String FILE_EXTENSION_TXT = ".txt";

    public static boolean hasTxtFileExtension(String filepath) {
        if (filepath != null) {
            return filepath.toLowerCase(Locale.getDefault()).endsWith(FILE_EXTENSION_TXT);
        } else {
            return false;
        }
    }

    public static Path createDirectory(String mainDirectory, String subdirectory) throws FileCreationException {
        try {
            Path currentWorkingDirPath = Paths.get(mainDirectory);
            Path absoluteDirectoryPath = currentWorkingDirPath.resolve(subdirectory);
            return Files.createDirectories(absoluteDirectoryPath);

        } catch (UnsupportedOperationException | IOException | SecurityException  | NullPointerException | InvalidPathException e) {
            log.error("Error creating directory: " + e.getMessage());
            throw new FileCreationException(mainDirectory, subdirectory);
        }
    }

    public static Path copyFileToDir(String sourceFile, String destinationDir) throws FileDownloadException {
        try {
            Path sourceFilepath = Paths.get(sourceFile);
            Path sourceFilename = sourceFilepath.getFileName();

            Path destinationDirPath = Paths.get(destinationDir).resolve(sourceFilename.toString());
            return Files.copy(sourceFilepath, destinationDirPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (InvalidPathException | IOException | NullPointerException e) {
            throw new FileDownloadException(sourceFile, destinationDir);
        }
    }
}