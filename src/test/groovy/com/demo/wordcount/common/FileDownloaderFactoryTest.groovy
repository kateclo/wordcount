package com.demo.wordcount.common

import com.demo.wordcount.exception.UnresolvedSourceException
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Paths

class FileDownloaderFactoryTest extends Specification {

    def "should be able to return LocalFileDownloader if link is local file and exists"() {
        given:
        String path = Paths.get(System.getProperty("user.dir")).resolve("settings.gradle").toString()

        when:
        FileDownloader downloader = FileDownloaderFactory.getFileDownloader(path)

        then:
        downloader instanceof LocalFileDownloader
    }

    @Unroll
    def "should be able to return WebUrlFileDownloader when url is #scenario"() {
        when:
        FileDownloader downloader = FileDownloaderFactory.getFileDownloader(path)

        then:
        downloader instanceof WebUrlFileDownloader

        where:
        scenario | path
        "http"   | "http://www.google.com/"
        "https"  | "https://www.google.com/"
    }

    @Unroll
    def "should return UnsupportedLinkException when #scenario"() {
        when:
        FileDownloaderFactory.getFileDownloader(path)

        then:
        thrown UnresolvedSourceException

        where:
        scenario                       | path
        "local file does not exist"    | Paths.get(System.getProperty("user.dir")).resolve(RandomStringUtils.randomAlphanumeric(10) + ".txt").toString()
        "web url is not http or https" | "ftp://ftp.example.com/"
    }
}
