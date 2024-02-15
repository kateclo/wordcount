package com.demo.wordcount.service

import com.demo.wordcount.counter.service.ConcurrentFileBufferWordCounter
import com.demo.wordcount.counter.service.ConcurrentMemoryMappedChunkingWordCounter
import com.demo.wordcount.exception.IllegalArgumentException
import com.demo.wordcount.data.request.WordCountRequest
import com.demo.wordcount.exception.IllegalOperationException
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Paths

class WordCountServiceTest extends Specification {
    private static final String FILES_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("files").toString()

    WordCountService wordCountService

    ConcurrentMemoryMappedChunkingWordCounter counter

    def setup() {
        wordCountService = new WordCountService()
        wordCountService.downloadsDir = "downloads"
        wordCountService.counterMode = "CONCURRENT_CHUNKED"
        wordCountService.wordCounters = [new ConcurrentFileBufferWordCounter(), new ConcurrentMemoryMappedChunkingWordCounter()]
    }

    @Unroll
    def "should be able to count when source is #scenario"() {
        given:
        WordCountRequest request = new WordCountRequest()
        request.setSource(Paths.get(FILES_DIR).resolve("abc.txt").toString())
        request.setFrequency(5)

        and:
        wordCountService.counterMode = counterModeVal

        when:
        wordCountService.count(request)

        then:
        noExceptionThrown()

        where:
        scenario     | sourceVal                                              | counterModeVal
        "from local" | Paths.get(FILES_DIR).resolve("abc.txt").toString()     | "CONCURRENT_CHUNKED"
        "from url"   | "https://www.gutenberg.org/cache/epub/2347/pg2347.txt" | "CONCURRENT_CHUNKED"
        "from local" | Paths.get(FILES_DIR).resolve("abc.txt").toString()     | "CONCURRENT"
        "from url"   | "https://www.gutenberg.org/cache/epub/2347/pg2347.txt" | "CONCURRENT"
    }

    @Unroll
    def "should throw #expException when #scenario"() {
        given:
        WordCountRequest request = requestVal

        when:
        wordCountService.count(request)

        then:
        thrown expException

        where:
        scenario                                        | requestVal                                                                                               || expException
        "null request"                                  | null                                                                                                     || NullPointerException
        "request has a validation error in source"      | new WordCountRequest("abc.data", 5)                                                                      || IllegalArgumentException
        "request has a validation error in frequency"   | new WordCountRequest("xyz.txt", -1)                                                                      || IllegalArgumentException
        "downloading a file that doesn't exist"         | new WordCountRequest(Paths.get(FILES_DIR).resolve(RandomStringUtils.randomAlphabetic(10)).toString(), 5) || IllegalArgumentException
    }


    @Unroll
    def "should throw IllegalOperationException when #scenario"() {
        given:
        WordCountRequest request = new WordCountRequest()
        request.setSource(sourceVal)
        request.setFrequency(5)

        and:
        wordCountService.downloadsDir = downloadsDir
        wordCountService.counterMode = modeVal

        when:
        wordCountService.count(request)

        then:
        thrown IllegalOperationException

        where:
        scenario                                        | downloadsDir | modeVal                                         | sourceVal
        "downloads dir is null"                         | null         | new ConcurrentMemoryMappedChunkingWordCounter() | Paths.get(FILES_DIR).resolve("abc.txt").toString()
        "downloading a file that is not supported link" | "downloads"  | "CONCURRENT_CHUNKED"                            | "https://ddsdsadslfdsf.com/sdsfdfd.txt"
        "counter mode is not supported"                 | "downloads"  | "DKjdjfdlfkdsdffldjfdlf"                        | Paths.get(FILES_DIR).resolve("abc.txt").toString()

    }


}
