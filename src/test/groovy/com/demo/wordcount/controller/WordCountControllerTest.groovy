package com.demo.wordcount.controller

import com.demo.wordcount.data.request.WordCountRequest
import com.demo.wordcount.service.WordCountService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification

import java.nio.file.Paths

import static org.hamcrest.CoreMatchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class WordCountControllerTest extends Specification {

    private static final String DOWNLOADS_DIR = Paths.get(System.getProperty("user.dir")).resolve("downloads").toString()

    private static final String ABC_TXT = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("files").resolve("abc.txt").toString()

    private static final String SPRING_PNG = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("files").resolve("spring.png").toString()


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


    def "should count words"() {
        given:
        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(ABC_TXT)
        wordCountRequest.setFrequency(5)

        when:
        def execution = mockMvc.perform(
                post("/api/demo/word-count/")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))

        then:
        execution.andExpect(status().isOk())
                .andExpect(jsonPath('$.data.wordCountDetails.size()', equalTo(4)))
                .andExpect(jsonPath('$.data.wordCountDetails.[0].word', equalTo("abc")))
                .andExpect(jsonPath('$.data.wordCountDetails.[0].count', equalTo(6)))
                .andExpect(jsonPath('$.data.wordCountDetails.[1].word', equalTo("123abc")))
                .andExpect(jsonPath('$.data.wordCountDetails.[1].count', equalTo(3)))
                .andExpect(jsonPath('$.data.wordCountDetails.[2].word', equalTo("abc123abc")))
                .andExpect(jsonPath('$.data.wordCountDetails.[2].count', equalTo(2)))
                .andExpect(jsonPath('$.data.wordCountDetails.[3].word', equalTo("abc123")))
                .andExpect(jsonPath('$.data.wordCountDetails.[3].count', equalTo(1)))
                .andExpect(jsonPath('$.error', equalTo(null)))
                .andDo(MockMvcResultHandlers.print())

    }

    def "should return Bad Request in counting words when IllegalArgumentException is thrown"() {
        given:
        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(SPRING_PNG)
        wordCountRequest.setFrequency(2)


        when:
        def result = mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))


        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.data', equalTo(null)))
                .andExpect(jsonPath('$.error.code', equalTo(400)))
                .andExpect(jsonPath('$.error.message', equalTo(String.format("File type not supported : {source=%s}", SPRING_PNG))))
                .andDo(MockMvcResultHandlers.print())


    }


    def "should return Internal Server error in counting words when handleIllegalOperationException"() {
        given:
        String link = "https://this-does-not-exist.com/ffdljfdgjfdljgfdkgjfd.txt"

        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(link)
        wordCountRequest.setFrequency(2)


        when:
        def result = mockMvc.perform(
                post("/api/demo/word-count/")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))


        then:
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath('$.data', equalTo(null)))
                .andExpect(jsonPath('$.error.code', equalTo(500)))
                .andExpect(jsonPath('$.error.message', equalTo(String.format("Error downloading %s to %s", link, DOWNLOADS_DIR))))
                .andDo(MockMvcResultHandlers.print())


    }

    def "should encounter an error when request is not authorized"() {
        given:
        String sourceFile = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
                .resolve("resources").resolve("test-data").resolve("files").resolve("abc.txt").toString()

        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(sourceFile)
        wordCountRequest.setFrequency(2)

        when:
        def result = mockMvc.perform(
                post("/api/demo/word-count/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))


        then:
        result.andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print())


    }
}
