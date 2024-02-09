package com.demo.wordcount.data.validator;

import com.demo.wordcount.common.FileUtil;
import com.demo.wordcount.data.request.WordCountRequest;
import com.demo.wordcount.exception.IllegalArgumentException;
import com.demo.wordcount.exception.MinValueException;
import com.demo.wordcount.exception.UnsupportedFileTypeException;
import lombok.NonNull;

public class WordCountRequestValidator {
    public static final int MIN_KVALUE = 1;

    public static void validate(@NonNull WordCountRequest request) throws IllegalArgumentException {
        if (!FileUtil.isTextFile(request.getSource())) {
            throw new UnsupportedFileTypeException(WordCountRequest.SOURCE_PARAM_NAME, request.getSource());
        }

        if (request.getKvalue() == null) {
            throw new MinValueException(WordCountRequest.KVALUE_PARAM_NAME, MIN_KVALUE);
        } else if (request.getKvalue() < MIN_KVALUE) {
            throw new MinValueException(WordCountRequest.KVALUE_PARAM_NAME, request.getKvalue(), MIN_KVALUE);
        }
    }
}