package com.ruchira.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "camel.component.telegram")
@Configuration
@Getter
@Setter
public class CamelTelegramConfiguration {

    private String authToken;
    private String chatId;
}
