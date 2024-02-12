package com.demo.wordcount.common

import com.demo.wordcount.exception.FileCreationException
import com.demo.wordcount.exception.FileDownloadException
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

class FileUtilTest extends Specification {

    private static final String OUTPUTS2_DIR =
            Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
                    .resolve("resources").resolve("test-data").resolve("outputs-2").toString()

    private static final String SAMPLE1_TXT_FILE =
            Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
                    .resolve("resources").resolve("test-data").resolve("2347").resolve("sample-1.txt").toString()

    private static final String TEST_DATA_DIR =
            Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
                    .resolve("resources").resolve("test-data").toString()


    def cleanupSpec() {
        Path directoryPath = Paths.get(OUTPUTS2_DIR)

        // clean up contents only
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            paths.filter { !it.equals(directoryPath) }
                    .sorted(Comparator.reverseOrder())
                    .forEach(Files::deleteIfExists)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }


    @Unroll
    def "should return #result when #scenario"() {
        when:
        boolean hasTxtFileExtension = FileUtil.hasTxtFileExtension(path)

        then:
        hasTxtFileExtension == result

        where:
        scenario               | path              || result
        "path name is empty"   | ""                || false
        "path name is null"    | null              || false
        "path name is txt"     | "filename.txt"    || true
        "path name is txt - 2" | "C:/abc/some.txt" || true
        "path name is not txt" | "abc.png"         || false
        "path name is a dir"   | "C:/abc/def"      || false

    }

    @Unroll
    def "should throw FileDownloadException when #scenario"() {
        when:
        FileUtil.copyFileToDir(paramSourceFile, paramDestinationDir)

        then:
        thrown FileDownloadException

        where:
        scenario                     | paramSourceFile                                                                                | paramDestinationDir
        "null source"                | null                                                                                           | OUTPUTS2_DIR
        "null destination"           | SAMPLE1_TXT_FILE                                                                               | null
        "null values"                | null                                                                                           | null
        "source does not exist"      | Paths.get(TEST_DATA_DIR).resolve(RandomStringUtils.randomAlphanumeric(10) + ".txt").toString() | OUTPUTS2_DIR
        "source is a url"            | "https://www.gutenberg.org/cache/epub/2347/pg2347.qrcode.png"                                  | OUTPUTS2_DIR
        "destination does not exist" | SAMPLE1_TXT_FILE                                                                               | Paths.get(OUTPUTS2_DIR).resolve(RandomStringUtils.randomAlphanumeric(10)).toString()
        "destination is a file"      | SAMPLE1_TXT_FILE                                                                               | Paths.get(TEST_DATA_DIR).resolve("2347").resolve("sample-2.txt").toString()

    }

    @Unroll
    def "file should be copied when #scenario"() {
        when:
        Path destinationPath = FileUtil.copyFileToDir(paramSourceFile, paramDestinationDir)

        then:
        destinationPath != null
        noExceptionThrown()

        where:
        scenario                | paramSourceFile  | paramDestinationDir
        "empty source"          | ""               | OUTPUTS2_DIR
        "empty destination"     | SAMPLE1_TXT_FILE | ""
        "empty values"          | ""               | ""
        "source is a directory" | TEST_DATA_DIR    | OUTPUTS2_DIR
        "source is a file"      | SAMPLE1_TXT_FILE | OUTPUTS2_DIR

    }

    @Unroll
    def "should throw FileCreationException in creating directory when #scenario"() {
        when:
        FileUtil.createDirectory(mainDir, subDir)

        then:
        thrown FileCreationException

        where:
        scenario                   | mainDir          | subDir
        "null main dir"            | null             | "temp-sub-dir"
        "null subDir"              | OUTPUTS2_DIR     | null
        "all null values"          | null             | null
        "main dir is a file"       | SAMPLE1_TXT_FILE | "temp-sub-dir"
        "sub dir is a file"        | OUTPUTS2_DIR     | SAMPLE1_TXT_FILE
        "sub dir has invalid name" | OUTPUTS2_DIR     | "\nhas-new-line"

    }

    @Unroll
    def "directory should be created when #scenario"() {
        when:
        Path createDirectory = FileUtil.createDirectory(mainDir, subDir)

        then:
        createDirectory != null
        noExceptionThrown()

        where:
        scenario                               | mainDir                                                                            | subDir
        "empty main dir"                       | ""                                                                                 | "temp-sub-dir"
        "empty subDir"                         | OUTPUTS2_DIR                                                                       | ""
        "all empty values"                     | ""                                                                                 | ""
        "main dir exists"                      | OUTPUTS2_DIR                                                                       | "temp-sub-dir-2"
        "main dir does not exist"              | Paths.get(OUTPUTS2_DIR).resolve(RandomStringUtils.randomAlphabetic(10)).toString() | "temp-sub-dir-3"
        "sub dir already exists"               | TEST_DATA_DIR                                                                      | "outputs-2"
        "sub dir already exists and not empty" | TEST_DATA_DIR                                                                      | "2347"

    }

}
