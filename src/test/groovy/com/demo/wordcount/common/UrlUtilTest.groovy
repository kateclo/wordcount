package com.demo.wordcount.common

import com.demo.wordcount.exception.FileDownloadException
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

class UrlUtilTest extends Specification {

    private static final String OUTPUTS_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("outputs").toString()

    private static final String SAMPLE1_TXT_FILE =
            Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
                    .resolve("resources").resolve("test-data").resolve("2347").resolve("sample-1.txt").toString()

    private static final String TEST_DATA_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").toString()

    private static final String TEMP_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("temp").toString()

    def setupSpec() {
        Files.createDirectories(Paths.get(TEMP_DIR).resolve("2347"))
    }

    def cleanupSpec() {
        Path directoryPath1 = Paths.get(OUTPUTS_DIR)
        cleanupDirectoryContents(directoryPath1)

        Path directoryPath2 = Paths.get(TEMP_DIR)
        cleanupDirectoryContents(directoryPath2)
    }

    def cleanupDirectoryContents(Path directoryPath) {
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            paths.filter { !it.equals(directoryPath) }
                    .sorted(Comparator.reverseOrder())
                    .forEach(Files::deleteIfExists)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }


    @Unroll
    def "when checking local file path, it should return #result when #scenario"() {
        when:
        boolean isLinkLocalFilepathAndExists = UrlUtil.isLocalFilepathAndExists(path)

        then:
        isLinkLocalFilepathAndExists == result

        where:
        scenario                      | path                                                                                                      || result
        "link is empty"               | ""                                                                                                        || false
        "link is null"                | null                                                                                                      || false
        "link = file:///"             | "file:///"                                                                                                || false
        "link does not exist"         | "file:///" + Paths.get(OUTPUTS_DIR).resolve(RandomStringUtils.randomAlphanumeric(10) + ".txt").toString() || false
        "link does not exist-2"       | Paths.get(OUTPUTS_DIR).resolve(RandomStringUtils.randomAlphanumeric(10) + ".txt").toString()              || false
        "link is relative path"       | "/gradle/dependencies.gradle"                                                                             || false
        "link is a directory"         | OUTPUTS_DIR                                                                                               || false

        "link exists"                 | SAMPLE1_TXT_FILE                                                                                          || true
        "link is file:/// and exists" | "file:///" + SAMPLE1_TXT_FILE                                                                             || true

    }


    @Unroll
    def "when checking the url, it should return #result when #scenario"() {
        when:
        boolean isUrlHttpOrHttps = UrlUtil.isHttpOrHttpsUrl(url)

        then:
        isUrlHttpOrHttps == result

        where:
        scenario               | url                                                    || result
        "url is empty"         | ""                                                     || false
        "url is null"          | null                                                   || false
        "url is not https"     | "mailto:support@abc.com?subject=Test&body=Hello!"      || false
        "url is relative path" | "relative/path/value"                                  || false

        "url is http"          | "http://www.gutenberg.org/cache/epub/2347/pg2347.txt"  || true
        "url is https"         | "https://www.gutenberg.org/cache/epub/2347/pg2347.txt" || true

    }

    @Unroll
    def "should throw FileDownloadException when #scenario"() {
        when:
        UrlUtil.copyFileToDir(paramWebUrl, paramDestinationDir)

        then:
        thrown FileDownloadException

        where:
        scenario                                                        | paramWebUrl                                            | paramDestinationDir
        "null web url"                                                  | null                                                   | OUTPUTS_DIR
        "null destination dir"                                          | "https://abc.com"                                      | null
        "null values"                                                   | null                                                   | null
        "empty web url"                                                 | ""                                                     | OUTPUTS_DIR
        "web url does not exist"                                        | "https://fdsfdsfdfserew.com/sfdfdfdgersf.txt"          | OUTPUTS_DIR
        "destination dir does not exist"                                | "https://www.gutenberg.org/cache/epub/2347/pg2347.txt" | Paths.get(OUTPUTS_DIR).resolve(RandomStringUtils.randomAlphanumeric(10)).toString()
        "webUrl is a dir url, and that dir in destination is non-empty" | "https://www.gutenberg.org/cache/epub/2347/"           | TEST_DATA_DIR

    }


    @Unroll
    def "should be able to copy file to destination when #scenario"() {
        when:
        Path destinationPath = UrlUtil.copyFileToDir(paramWebUrl, paramDestinationDir)

        then:
        destinationPath != null

        where:
        scenario                                                                   | paramWebUrl                                                   | paramDestinationDir
        "valid values : http txt url"                                              | "http://www.gutenberg.org/cache/epub/2347/pg2347.txt"         | OUTPUTS_DIR
        "valid values : https txt url"                                             | "https://www.gutenberg.org/cache/epub/2347/pg2347.txt"        | OUTPUTS_DIR
        "valid values : https png url"                                             | "https://www.gutenberg.org/cache/epub/2347/pg2347.qrcode.png" | OUTPUTS_DIR
        "valid values : http png url"                                              | "http://www.gutenberg.org/cache/epub/2347/pg2347.qrcode.png"  | OUTPUTS_DIR
        "valid values : https dir url, and that dir in destination does not exist" | "https://www.gutenberg.org/cache/epub/2347/"                  | OUTPUTS_DIR
        "valid values : http dir url, and that dir in destination does not exist"  | "http://www.gutenberg.org/cache/epub/2347/"                   | OUTPUTS_DIR
        "valid values : https dir url, and that dir in destination is empty"       | "https://www.gutenberg.org/cache/epub/2347/"                  | TEMP_DIR
        "valid values : http dir url, and that dir in destination is empty"        | "http://www.gutenberg.org/cache/epub/2347/"                   | TEMP_DIR
        "valid values : empty dest dir"                                            | "http://www.gutenberg.org/cache/epub/2347/pg2347.qrcode.png"  | ""

    }
}

