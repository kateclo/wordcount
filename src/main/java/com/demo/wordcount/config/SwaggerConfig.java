package com.demo.wordcount.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Configuration
@Controller
public class SwaggerConfig {

    @RequestMapping("/swagger")
    public String redirectSwaggerMainPage() {
        return "redirect:/swagger-ui/index.html";
    }


    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("word-count")
                .pathsToMatch("/api/**")
                .build();
    }
}
