package com.argus.configuration;

import com.argus.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestServiceConfiguration {
    @Value("${salt}")
    private String salt;
    @Bean
    public CryptoTool getCryptoTool() {
        return new CryptoTool(salt);
    }
}
