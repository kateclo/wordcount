package com.demo.wordcount.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("com.demo.wordcount")
public class WordCountConfiguration {

    public static void registerMySqlDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new Error("MySQL driver 'com.mysql.cj.jdbc.Driver' not found on classpath", e);
        }
    }
}
