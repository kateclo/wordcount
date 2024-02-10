package com.demo.wordcount.data.validator;

import com.demo.wordcount.common.FileUtil;
import com.demo.wordcount.data.request.WordCountRequest;
import com.demo.wordcount.exception.IllegalArgumentException;
import com.demo.wordcount.exception.MinValueException;
import com.demo.wordcount.exception.UnsupportedFileTypeException;
import lombok.NonNull;

public class WordCountRequestValidator {
    public static final int MIN_FREQUENCY_VALUE = 1;

    public static void validate(@NonNull WordCountRequest request) throws IllegalArgumentException {

        // TODO: call validate()
        if (!FileUtil.hasTxtFileExtension(request.getSource())) {
            throw new UnsupportedFileTypeException(WordCountRequest.SOURCE_PARAM_NAME, request.getSource());
        }

        if (request.getFrequency() == null) {
            throw new MinValueException(WordCountRequest.FREQUENCY_PARAM_NAME, MIN_FREQUENCY_VALUE);
        } else if (request.getFrequency() < MIN_FREQUENCY_VALUE) {
            throw new MinValueException(WordCountRequest.FREQUENCY_PARAM_NAME, request.getFrequency(), MIN_FREQUENCY_VALUE);
        }
    }
}