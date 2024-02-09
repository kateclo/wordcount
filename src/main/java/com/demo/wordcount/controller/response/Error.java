package com.demo.wordcount.controller.response;

import lombok.Getter;

public class Error {
    @Getter
    private final int code;
    @Getter
    private final String message;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

