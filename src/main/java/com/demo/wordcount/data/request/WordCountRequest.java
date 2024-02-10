package com.demo.wordcount.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordCountRequest {
    public static final String FREQUENCY_PARAM_NAME = "k_value";
    public static final String SOURCE_PARAM_NAME = "source";

    @NotEmpty(message = "source must not be empty or null")
    private String source;

    @JsonProperty("k_value")
    private Integer frequency;
}
