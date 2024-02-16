package com.demo.wordcount.controller;

import com.demo.wordcount.controller.response.Response;
import com.demo.wordcount.data.request.WordCountRequest;
import com.demo.wordcount.data.response.WordCountDetails;
import com.demo.wordcount.data.response.WordCountResponse;
import com.demo.wordcount.exception.IllegalOperationException;
import com.demo.wordcount.service.WordCountService;
import com.demo.wordcount.exception.IllegalArgumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequestMapping("/api/demo/word-count/")
@RestController
public class WordCountController {

    @Autowired
    private WordCountService wordCountService;

    @Operation(summary = "Count words in a given file",
            description = "Download a txt file from a given location, then count the words in the txt file, " +
                          " and return the top k most words in descending order")
    @ApiResponse(responseCode = "200", description = "Successfully processed the text file, and the k top most words are returned")
    @ApiResponse(responseCode = "400", description = "Failed to process request due to invalid request data")
    @ApiResponse(responseCode = "500", description = "Encountered error while processing the request, to submit to developer for investigation ")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<WordCountResponse> countWords(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Contains the data request", required = true)
            @Valid @RequestBody WordCountRequest request)
            throws IllegalArgumentException, IllegalOperationException {
        List<WordCountDetails> details = wordCountService.count(request);
        return Response.of(new WordCountResponse(details));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<String> welcome() {
        return Response.of("Hello! Please use a POST request for the word counting. Thanks!");
    }
}
