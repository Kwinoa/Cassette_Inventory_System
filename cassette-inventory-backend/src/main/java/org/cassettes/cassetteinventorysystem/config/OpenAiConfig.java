package org.cassettes.cassetteinventorysystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Bean
    public OpenAIClient openAiClient() {
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }
}
