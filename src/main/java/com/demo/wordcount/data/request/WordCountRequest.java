package com.demo.wordcount.data.request;

import com.demo.wordcount.common.FileUtil;
import com.demo.wordcount.exception.IllegalArgumentException;
import com.demo.wordcount.exception.MinValueException;
import com.demo.wordcount.exception.RequiredValueException;
import com.demo.wordcount.exception.UnsupportedFileTypeException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;


@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordCountRequest {
    public static final String FREQUENCY_PARAM_NAME = "k_value";
    public static final String SOURCE_PARAM_NAME = "source";
    public static final int MIN_FREQUENCY_VALUE = 1;

    @Getter
    @NotEmpty(message = "source must not be empty or null")
    private String source;

    @Getter
    @Setter
    @JsonProperty("k_value")
    private Integer frequency;


    public void setSource(String newValue) {
        if (newValue != null) {
            source = newValue.strip();
        }
    }

    public void validate() throws IllegalArgumentException {
        if (source == null || source.isBlank()) {
            throw new RequiredValueException(SOURCE_PARAM_NAME);
        }

        if (!FileUtil.hasTxtFileExtension(source)) {
            throw new UnsupportedFileTypeException(SOURCE_PARAM_NAME, source);
        }

        if (frequency == null) {
            throw new MinValueException(FREQUENCY_PARAM_NAME, MIN_FREQUENCY_VALUE);
        } else if (frequency < MIN_FREQUENCY_VALUE) {
            throw new MinValueException(FREQUENCY_PARAM_NAME, frequency, MIN_FREQUENCY_VALUE);
        }
    }

}
