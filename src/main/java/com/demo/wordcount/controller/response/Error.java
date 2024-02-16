package com.demo.wordcount.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Error {

    @Schema(description = "Error code", example = "400")
    @Getter
    private final int code;

    @Schema(description = "Description about the error", example = "Field `source` must not be empty or null")
    @Getter
    private final String message;
}

