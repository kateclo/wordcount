package com.demo.wordcount.controller.response;

import lombok.Getter;

public class Response<T> {
    @Getter
    private final T data;
    @Getter
    private final Error error;

    private Response(T data) {
        this.data = data;
        this.error = null;
    }

    private Response(Error error) {
        this.data = null;
        this.error = error;
    }

    public static <T> Response<T> of(T data) {
        return new Response<>(data);
    }

    public static <T> Response<T> error(int code, String message) {
        Error error = new Error(code, message);
        return new Response<>(error);
    }
}
