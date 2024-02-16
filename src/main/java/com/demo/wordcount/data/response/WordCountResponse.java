package com.demo.wordcount.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "list of words and their number of occurrences in the file",
            example = "[{\"word\":\"example\",\"count\":3}]")
    @Getter
    private final List<WordCountDetails> wordCountDetails;
}
