package com.demo.wordcount.counter

import com.demo.wordcount.counter.service.ConcurrentFileBufferWordCounter
import com.demo.wordcount.exception.FileReadingException
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

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
        "example.txt"                    | Paths.get(FILES_DIR).resolve("example.txt") | 2              || 2            | ['example': 3, 'and': 2]
        "abc.txt (different word forms)" | Paths.get(FILES_DIR).resolve("abc.txt")     | 5              || 4            | ['abc': 6, '123abc': 3, 'abc123abc': 2, 'abc123': 1]
        "holmes.txt (large file)"        | Paths.get(FILES_DIR).resolve("holmes.txt")  | 5              || 5            | ['the': 5632, 'i': 3036, 'and': 3020, 'to': 2747, 'of': 2660]

    }

    @Unroll
    def "should throw NullPointerException where input/s #scenario" () {
        given:
        WordCounter counter = new ConcurrentFileBufferWordCounter()

        when:
        counter.countUniqueAndValidWords(mapValue, contentValue)

        then:
        thrown NullPointerException

        where:
        scenario           | mapValue                                 | contentValue
        "have null values" | null                                     | null
        "has null map"     | null                                     | "content 101"
        "has null content" | new ConcurrentHashMap<String, Integer>() | null

    }

    def "should be able to update an empty count map"() {
        given:
        WordCounter counter = new ConcurrentFileBufferWordCounter()
        String content = "abc _defg_ hij, klmn! oPQr 78stu ABC -DEFg!!! __Hij## ##klmn..."
        Map<String, Integer> countMap = new ConcurrentHashMap<>()

        when:
        counter.countUniqueAndValidWords(countMap, content)

        then:
        countMap.size() == 6
        countMap.get("abc") == 2
        countMap.get("defg") == 1
        countMap.get("hij") == 1
        countMap.get("klmn") == 2
        countMap.get("opqr") == 1
        countMap.get("78stu") == 1

    }

    def "should be able to update count map"() {
        given:
        WordCounter counter = new ConcurrentFileBufferWordCounter()
        String content = "12 abc _defg_ hij, klmn! oPQr 78stu ABC -DEFg!!! __Hij## ##klmn..."
        Map<String, Integer> countMap = ['xyz': 5, 'abc' : 4, 'hij' : 3]

        when:
        counter.countUniqueAndValidWords(countMap, content)

        then:
        countMap.size() == 8
        countMap.get("12") == 1
        countMap.get("xyz") == 5
        countMap.get("abc") == 6
        countMap.get("defg") == 1
        countMap.get("hij") == 4
        countMap.get("klmn") == 2
        countMap.get("opqr") == 1
        countMap.get("78stu") == 1

    }

    @Unroll
    def "should be able to handle empty content when content = #scenario"() {
        given:
        WordCounter counter = new ConcurrentFileBufferWordCounter()
        String content = contentVal
        Map<String, Integer> countMap = new ConcurrentHashMap<String, Integer>()

        when:
        counter.countUniqueAndValidWords(countMap, content)

        then:
        countMap.size() == 0

        where:
        scenario        | contentVal
        "empty"         | ""
        "all spaces"    | "             "
        "all non-words" | ", ? . # % & _ ! @ \$ ^ * ( ) - _ + =   "

    }

}
