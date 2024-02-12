package com.demo.wordcount.common

import com.demo.wordcount.exception.FileDownloadException
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

class WebUrlFileDownloaderTest extends Specification {

    private static final String OUTPUTS4_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("outputs-4").toString()

    def cleanupSpec() {
        Path directoryPath = Paths.get(OUTPUTS4_DIR)

        // clean up contents only
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            paths.filter { !it.equals(directoryPath) }
                    .sorted(Comparator.reverseOrder())
                    .forEach(Files::deleteIfExists)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }


    def "should be able to download file from a URL when there are no issues calling UrlUtil.copyFileToDir"() {
        given:
        FileDownloader downloader = new WebUrlFileDownloader()
        String webUrl = "http://www.gutenberg.org/cache/epub/2347/pg2347.txt"

        when:
        Path downloadedFile = downloader.download(webUrl, OUTPUTS4_DIR)

        then:
        downloadedFile != null
        noExceptionThrown()

    }

    def "should throw FileDownloadException in downloading file from a URL when there is an issue calling UrlUtil.copyFileToDir"() {
        given:
        FileDownloader downloader = new WebUrlFileDownloader()
        String webUrl = "http://this/does/not/exist"

        when:
        downloader.download(webUrl, OUTPUTS4_DIR)

        then:
        thrown FileDownloadException

    }
}
