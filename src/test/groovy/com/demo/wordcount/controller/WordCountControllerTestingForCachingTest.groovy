package com.demo.wordcount.controller

import com.demo.wordcount.data.request.WordCountRequest
import com.demo.wordcount.service.WordCountService
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.nio.file.Paths

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest
@AutoConfigureMockMvc
class WordCountControllerTestingForCachingTest extends Specification {

    private static final String FILES_DIR = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("files").toString()


    @Autowired
    WordCountService wordCountService

    @Autowired
    MockMvc mockMvc

    ObjectMapper objectMapper

    String username
    String password


    def setup() {
        objectMapper = new ObjectMapper()
        username = "tester"
        password = "w1nt3r101!"

    }

    def "should count words, and use cached result when supplied with the same request"() {
        given:
        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(Paths.get(FILES_DIR).resolve("example-2.txt").toString())
        wordCountRequest.setFrequency(2)

        and:
        def outContent = new ByteArrayOutputStream()
        System.setOut(new PrintStream(outContent))

        when: "a request was supplied to the API call"
        mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))

        and: "the same request was used again in calling the API"
        mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))

        then: "Calls to the same request will not create a new cache."
        1 == StringUtils.countMatches(outContent.toString(), "[Cache Event] CREATED")

    }

    def "should count words, and NOT use cached result when supplied with different requests"() {
        given: "that a different source was used in the request"
        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(Paths.get(FILES_DIR).resolve("buffer-test_more-than-max-buffer-capacity-50.txt").toString())
        wordCountRequest.setFrequency(2)

        and: "that a second different source was used in the request"
        WordCountRequest diffWordCountRequest = new WordCountRequest()
        diffWordCountRequest.setSource(Paths.get(FILES_DIR).resolve("buffer-test_equal-to-max-buffer-capacity-50.txt").toString())
        diffWordCountRequest.setFrequency(2)

        and:
        def outContent = new ByteArrayOutputStream()
        System.setOut(new PrintStream(outContent))

        when: "the request (using a different source) was supplied to the API call"
        mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))



        and: "the second request (using a another different source) was supplied to the API call"
        mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diffWordCountRequest))
                        .characterEncoding("UTF-8"))

        then: "2 caches were created. One for each API call."
        2 == StringUtils.countMatches(outContent.toString(), "[Cache Event] CREATED")

    }

    def "should count words, and use cached or uncached result depending if same or different request was used"() {
        given: "that a request uses a source that was already cached"
        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(Paths.get(FILES_DIR).resolve("example-2.txt").toString())
        wordCountRequest.setFrequency(2)

        and: "that a request uses a source that was already cached, but different frequency"
        WordCountRequest diffWordCountRequest = new WordCountRequest()
        diffWordCountRequest.setSource(Paths.get(FILES_DIR).resolve("example-2.txt").toString())
        diffWordCountRequest.setFrequency(100)

        and:
        def outContent = new ByteArrayOutputStream()
        System.setOut(new PrintStream(outContent))

        when: "the first request (with cached source and frequency) was called"
        mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))



        and: "the first request was called again"
        mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))

        and: "the 2nd request with same source but different frequency was used again in calling the API"
        mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diffWordCountRequest))
                        .characterEncoding("UTF-8"))

        then: "Only 1 cache was created"
        1 == StringUtils.countMatches(outContent.toString(), "[Cache Event] CREATED")

    }
}
