package com.demo.wordcount.controller

import com.demo.wordcount.data.request.WordCountRequest
import com.demo.wordcount.data.response.WordCountDetails
import com.demo.wordcount.exception.UnsupportedFileTypeException
import com.demo.wordcount.service.WordCountService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.equalTo
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(WordCountController)
class WordCountControllerTest extends Specification {

    @MockBean
    WordCountService wordCountService

    @Autowired
    MockMvc mockMvc

    ObjectMapper objectMapper


    def setup() {
        objectMapper = new ObjectMapper()
    }

    def "should count words"() {
        given:
        WordCountRequest wordCountRequest = new WordCountRequest()
        List<WordCountDetails> details = []
        details.add(new WordCountDetails("abc", 15))
        details.add(new WordCountDetails("def", 10))

        when:
        when(wordCountService.count(any(WordCountRequest.class))).thenReturn(details)

        def execution = mockMvc.perform(
                post("/api/demo/word-count/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))

        then:
        execution.andExpect(status().isOk())
                .andExpect(jsonPath('$.data.wordCountDetails.[0].word', equalTo("abc")))
                .andExpect(jsonPath('$.data.wordCountDetails.[0].count', equalTo(15)))
                .andExpect(jsonPath('$.data.wordCountDetails.[1].word', equalTo("def")))
                .andExpect(jsonPath('$.data.wordCountDetails.[1].count', equalTo(10)))
                .andExpect(jsonPath('$.error', equalTo(null)))
                .andDo(MockMvcResultHandlers.print())

    }

    def "should be able to handle when an Exception is thrown"() {
        given:
        WordCountRequest wordCountRequest = new WordCountRequest()
        wordCountRequest.setSource("abc.png")
        wordCountRequest.setFrequency(5)

        when:
        when(wordCountService.count(any(WordCountRequest.class))).thenThrow(new UnsupportedFileTypeException("source", "abc.png"))

        def result = mockMvc.perform(
                post("/api/demo/word-count/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wordCountRequest))
                        .characterEncoding("UTF-8"))


        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.data', equalTo(null)))
                        .andExpect(jsonPath('$.error.code', equalTo(400)))
                .andExpect(jsonPath('$.error.message', equalTo("File type not supported : {source=abc.png}")))
                .andDo(MockMvcResultHandlers.print())
    }
}
