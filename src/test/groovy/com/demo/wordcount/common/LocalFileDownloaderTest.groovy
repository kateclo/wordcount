package com.demo.wordcount.common

import com.demo.wordcount.exception.FileDownloadException
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

class LocalFileDownloaderTest extends Specification {

    private static final String OUTPUTS3_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("outputs-3").toString()

    private static final String SAMPLE1_TXT_FILE = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("2347").resolve("sample-1.txt").toString()

    def setupSpec() {
        Files.createDirectories(Paths.get(OUTPUTS3_DIR))
    }

    def cleanupSpec() {
        Path directoryPath = Paths.get(OUTPUTS3_DIR)

        // clean up contents only
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            paths.filter { !it.equals(directoryPath) }
                    .sorted(Comparator.reverseOrder())
                    .forEach(Files::deleteIfExists)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }


    def "should be able to download local file when there are no issues calling FileUtil.copyFileToDir"() {
        given:
        FileDownloader downloader = new LocalFileDownloader()

        when:
        Path downloadedFile = downloader.download(SAMPLE1_TXT_FILE, OUTPUTS3_DIR)

        then:
        downloadedFile != null
        noExceptionThrown()
    }

    def "should throw FileDownloadException in downloading local file when there is an issue calling FileUtil.copyFileToDir"() {
        given:
        FileDownloader downloader = new LocalFileDownloader()
        String nonExistentDestination = Paths.get(OUTPUTS3_DIR).resolve(RandomStringUtils.randomAlphanumeric(10)).toString()

        when:
        downloader.download(SAMPLE1_TXT_FILE, nonExistentDestination)

        then:
        thrown FileDownloadException
    }
}
