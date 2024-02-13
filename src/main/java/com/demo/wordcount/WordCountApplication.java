package com.demo.wordcount;

import com.demo.wordcount.config.WordCountConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WordCountApplication {
    public static void main(String[] args) {
        WordCountConfiguration.registerMySqlDriver();
        SpringApplication.run(WordCountApplication.class, args);
    }
}
