package com.demo.wordcount.counter

import com.demo.wordcount.counter.service.ConcurrentFileBufferWordCounter
import com.demo.wordcount.exception.FileReadingException
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.Paths

class ConcurrentFileBufferWordCounterTest extends Specification {
    private static final String FILES_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("files").toString()

    def "should throw an exception when #scenario"() {
        given:
        WordCounter counter = new ConcurrentFileBufferWordCounter()
        Path fileDoesNotExist = filepathValue

        when:
        counter.retrieveTopFrequentWords(fileDoesNotExist, 2)

        then:
        thrown result

        where:
        scenario              | filepathValue                                                                                      || result
        "file does not exist" | Paths.get(System.getProperty("user.dir")).resolve(RandomStringUtils.randomAlphabetic(20) + ".txt") || FileReadingException
        "file is null"        | null                                                                                               || NullPointerException
    }

    @Unroll
    def "should be able to retrieve the top frequent words when file = #scenario"() {
        given:
        WordCounter counter = new ConcurrentFileBufferWordCounter()

        when:
        Map<String, Integer> result = counter.retrieveTopFrequentWords(fileValue, frequencyValue)

        then:
        result.size() == expectedSize
        result == expectedMap


        where:
        scenario                         | fileValue                                   | frequencyValue || expectedSize | expectedMap
        "example.txt"                    | Paths.get(FILES_DIR).resolve("example.txt") | 2               | 2            | ['example': 3, 'and': 2]
        "abc.txt (different word forms)" | Paths.get(FILES_DIR).resolve("abc.txt")     | 5               | 3            | ['abc': 6, 'abc123abc': 2, 'abc123': 1]
        "holmes.txt (large file)"        | Paths.get(FILES_DIR).resolve("holmes.txt")  | 5               | 5            | ['the': 5632, 'i': 3036, 'and': 3020, 'to': 2747, 'of': 2660]

    }

}
