package com.demo.wordcount.exception;

public class MinValueException extends IllegalArgumentException {
    public MinValueException(String field, int value, int minValue) {
        super(String.format("Field {%s=%d} needs to be greater than or equal %d", field, value, minValue));
    }

    public MinValueException(String field, int minValue) {
        super(String.format("Field {%s=null} needs to be greater than or equal %d", field, minValue));
    }
}