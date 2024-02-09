package com.demo.wordcount.controller;

import com.demo.wordcount.controller.response.Response;
import com.demo.wordcount.data.request.WordCountRequest;
import com.demo.wordcount.data.response.WordCountDetails;
import com.demo.wordcount.data.response.WordCountResponse;
import com.demo.wordcount.exception.IllegalOperationException;
import com.demo.wordcount.service.WordCountService;
import com.demo.wordcount.exception.IllegalArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<WordCountResponse> countWords(@Valid @RequestBody WordCountRequest request)
            throws IllegalArgumentException, IllegalOperationException {
        List<WordCountDetails> details = wordCountService.count(request);
        return Response.of(new WordCountResponse(details));
    }
}
