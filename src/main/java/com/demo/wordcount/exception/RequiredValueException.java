package com.demo.wordcount.exception;

public class RequiredValueException extends IllegalArgumentException {

    public RequiredValueException(String field) {
        super(String.format("Field `%s` must not be empty or null", field));
    }
}
