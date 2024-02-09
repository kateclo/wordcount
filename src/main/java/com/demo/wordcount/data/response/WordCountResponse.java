package com.demo.wordcount.data.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WordCountResponse {

    public WordCountResponse(List<WordCountDetails> data) {
        wordCountDetails = new ArrayList<>();

        if (data != null) {
            wordCountDetails.addAll(data
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
    }

    @Getter
    private final List<WordCountDetails> wordCountDetails;
}
