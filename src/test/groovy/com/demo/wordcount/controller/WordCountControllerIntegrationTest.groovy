package com.demo.wordcount.controller


import com.demo.wordcount.data.request.WordCountRequest
import com.demo.wordcount.service.WordCountService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Paths

import static org.hamcrest.CoreMatchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class WordCountControllerIntegrationTest extends Specification {

    private static final String SPRING_PNG = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
            .resolve("resources").resolve("test-data").resolve("files").resolve("spring.png").toString()


    @Autowired
    WordCountService wordCountService

    @Autowired
    MockMvc mockMvc

    ObjectMapper objectMapper


    def setup() {
        objectMapper = new ObjectMapper()
    }

    def "should count words"() {
        given:
        String exampleTxtFileLoc = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test")
                .resolve("resources").resolve("test-data").resolve("files").resolve("example.txt").toString()

        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(exampleTxtFileLoc)
        wordCountRequest.setFrequency(2)

        when:
        def execution = mockMvc.perform(
                post("/api/demo/word-count/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))

        then:
        execution.andExpect(status().isOk())
                .andExpect(jsonPath('$.data.wordCountDetails.size()', equalTo(2)))
                .andExpect(jsonPath('$.data.wordCountDetails.[0].word', equalTo("example")))
                .andExpect(jsonPath('$.data.wordCountDetails.[0].count', equalTo(3)))
                .andExpect(jsonPath('$.data.wordCountDetails.[1].word', equalTo("and")))
                .andExpect(jsonPath('$.data.wordCountDetails.[1].count', equalTo(2)))
                .andExpect(jsonPath('$.error', equalTo(null)))
                .andDo(MockMvcResultHandlers.print())

    }

    @Unroll
    def "should return Bad Request in counting words when request has an error"() {
        given:
        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource(SPRING_PNG)
        wordCountRequest.setFrequency(2)


        when:
        def result = mockMvc.perform(
                post("/api/demo/word-count/")
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
}
