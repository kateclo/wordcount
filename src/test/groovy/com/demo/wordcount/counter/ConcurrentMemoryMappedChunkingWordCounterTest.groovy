package com.demo.wordcount.counter

import com.demo.wordcount.counter.service.ConcurrentMemoryMappedChunkingWordCounter
import com.demo.wordcount.exception.FileReadingException
import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.ByteBuffer
import java.nio.file.Path
import java.nio.file.Paths

class ConcurrentMemoryMappedChunkingWordCounterTest extends Specification {
    private static final String FILES_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("files").toString()

    ConcurrentMemoryMappedChunkingWordCounter counter

    def setup() {
        counter = new ConcurrentMemoryMappedChunkingWordCounter()
        counter.bufferSizeInBytes = 1048576
        counter.maxChunkedWordLength = 100
    }

    def "should throw an exception when #scenario"() {
        given:
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
        when:
        Map<String, Integer> result = counter.retrieveTopFrequentWords(fileValue, frequencyValue)

        then:
        result.size() == expectedSize
        result == expectedMap


        where:
        scenario                         | fileValue                                        | frequencyValue || expectedSize | expectedMap
        "example.txt"                    | Paths.get(FILES_DIR).resolve("example.txt")      | 2               | 2            | ['example': 3, 'and': 2]
        "abc.txt (different word forms)" | Paths.get(FILES_DIR).resolve("abc.txt")          | 5               | 4            | ['abc': 6, '123abc':3, 'abc123abc': 2, 'abc123': 1]
        "holmes.txt (large file)"        | Paths.get(FILES_DIR).resolve("holmes.txt")       | 5               | 5            | ['the': 5632, 'i': 3036, 'and': 3020, 'to': 2747, 'of': 2660]
    }

    @Unroll
    def "should be able to retrieve the top frequent words (using suggested buffer settings) when #scenario"() {
        when:
        Map<String, Integer> result = counter.retrieveTopFrequentWords(fileValue, frequencyValue)

        then:
        result.size() == expectedSize
        result == expectedMap


        where:
        scenario                                               | fileValue                                           | frequencyValue || expectedSize | expectedMap
        "file is less than buffer size of 1MB"                 | Paths.get(FILES_DIR).resolve("example.txt")         | 2               | 2            | ['example': 3, 'and': 2]
        "file is 1MB"                                          | Paths.get(FILES_DIR).resolve("file_1128KB.txt")     | 5               | 5            | ['apple': 17667, 'lemon': 17617, 'banana': 17561, 'elderberry': 17560, 'honeydew': 17526]
        "file is 2MB"                                          | Paths.get(FILES_DIR).resolve("file_2286KB.txt")     | 5               | 5            | ['lemon': 35851, 'apple': 35643, 'elderberry': 35593, 'fig': 35500, 'cherry': 35478]
        "file is greater than buffer size and 100-length word" | Paths.get(FILES_DIR).resolve("file_100-length.txt") | 5               | 5            | ['abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv': 26600, 'apple': 33, 'banana': 32, 'cherry': 31, 'date': 23]
        "file is greater than buffer size and 152-length word" | Paths.get(FILES_DIR).resolve("file_152-length.txt") | 5               | 5            | ['abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz': 26600, 'apple': 33, 'banana': 32, 'cherry': 31, 'date': 23]

    }

    @Unroll
    def "should be able to retrieve the top frequent words when #scenario"() {
        given:
        counter.bufferSizeInBytes = 40
        counter.maxChunkedWordLength = 10

        when:
        Map<String, Integer> result = counter.retrieveTopFrequentWords(fileValue, frequencyValue)

        then:
        result.size() == expectedSize
        result == expectedMap


        where:
        scenario                                               | fileValue                                                                        | frequencyValue || expectedSize | expectedMap
        "buffer was not chunked"                               | Paths.get(FILES_DIR).resolve("buffer-test_less-than-max-buffer-capacity-50.txt") | 5               | 3            | ['hi': 4, 'hello': 3, 'hei': 1]
        "buffer was chunked, last word size = max word length" | Paths.get(FILES_DIR).resolve("buffer-test_equal-to-max-buffer-capacity-50.txt")  | 5               | 3            | ['a': 6, 'once': 3, 'abcdefghijklmnopqrstuv': 1]
        "buffer was chunked, last word size > max word length" | Paths.get(FILES_DIR).resolve("buffer-test_more-than-max-buffer-capacity-50.txt") | 5               | 4            | ['once': 3, 'tuvwxyzab': 2, 'abcdefghijklmn': 1, 'thiswillbecutoff': 1]

    }

    @Unroll
    def "should be able to set the limit of the characters to be processed in the buffer when #scenario" () {
        given:
        int bufferSize = 40
        int maxWordLength = 10

        long fileSize = bufferContent.length()
        long position = 0
        int bufferCapacity = bufferSize + maxWordLength

        ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity)

        long remainingSizeToProc = fileSize - position
        long bytesToRead = Math.min(bufferSize, remainingSizeToProc)
        int actualBufferCapacity = Math.min(bufferCapacity, (int) remainingSizeToProc)


        byte[] temp = (bufferContent.substring(0, (int) actualBufferCapacity)).getBytes()
        buffer.clear()
        buffer.put(temp, 0, actualBufferCapacity)
        buffer.flip()

        when:
        int overlapLength = counter.handleOverlappedWord(buffer, fileSize, position, bytesToRead, bufferCapacity, maxWordLength)

        then:
        overlapLength == expLength
        buffer.limit() == charactersReadInTheBuffer

        where:
        scenario                                                  | bufferContent                                           || expLength | charactersReadInTheBuffer
        "buffer was not chunked"                                  | "Hello"                                                 || 0         | 5
        "buffer was chunked, input buffer size = max buffer size" | "Once upon a time tuvwxyzab abcdefghijklmnopqrstuvw"    || 10        | 50
        "buffer was chunked, input buffer size > max buffer size" | "Once upon a time tuvwxyzab abcdefghijklmnopqrstuvwxyz" || 10        | 50
    }
}
