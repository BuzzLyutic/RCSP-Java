package com.example.dfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class AppConfig {

    /**
     * RestTemplate с таймаутами
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // Таймаут подключения (5 секунд)
        factory.setConnectTimeout(5000);

        // Таймаут чтения (10 секунд)
        factory.setReadTimeout(10000);

        return new RestTemplate(factory);
    }
}
