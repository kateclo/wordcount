package com.demo.wordcount.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class Response<T> {

    @Schema(description = "Output of the data processing")
    @Getter
    private final T data;

    @Schema(description = "Contains the details of the encountered error or exception during data processing. " +
            "Value is null if there are no errors.")
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
