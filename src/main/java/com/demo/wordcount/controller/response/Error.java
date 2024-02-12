package com.demo.wordcount.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Error {
    @Getter
    private final int code;
    @Getter
    private final String message;
}

