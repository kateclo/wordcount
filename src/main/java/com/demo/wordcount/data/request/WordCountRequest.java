package com.demo.wordcount.data.request;

import com.demo.wordcount.common.FileUtil;
import com.demo.wordcount.exception.IllegalArgumentException;
import com.demo.wordcount.exception.MinValueException;
import com.demo.wordcount.exception.RequiredValueException;
import com.demo.wordcount.exception.UnsupportedFileTypeException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordCountRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String FREQUENCY_PARAM_NAME = "k_value";
    public static final String SOURCE_PARAM_NAME = "source";
    public static final int MIN_FREQUENCY_VALUE = 1;

    @Schema(description = "The txt file location. File must exists. Either accessible via local directory or a URL",
            example = "https://valid.url.com/existingFile.txt OR D:\\windows\\file\\example.txt")
    @Getter
    @NotEmpty(message = "source must not be empty or null")
    private String source;

    @Schema(description = "The number indicating the the top most words in the given txt file. Must be greater than 0, and whole number.",
            example = "1")
    @Getter
    @Setter
    @JsonProperty("k_value")
    private Integer frequency;


    @Override
    public boolean equals(Object objToCheck) {
        if (this == objToCheck) {
            return true;
        }

        if (objToCheck == null || getClass() != objToCheck.getClass()) {
            return false;
        }

        WordCountRequest countRequest = (WordCountRequest) objToCheck;
        if (source != null) {
            return source.equalsIgnoreCase(countRequest.source) && Objects.equals(frequency, countRequest.frequency);
        } else {
            return Objects.equals(null, countRequest.source) && Objects.equals(frequency, countRequest.frequency);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, frequency);
    }

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
