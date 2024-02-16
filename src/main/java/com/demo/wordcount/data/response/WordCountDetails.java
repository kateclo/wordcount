package com.demo.wordcount.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WordCountDetails {
    @Schema(description = "a word in the file", example = "example")
    private String word;

    @Schema(description = "the number of times the word has occurred in the file", example = "3")
    private int count;
}
