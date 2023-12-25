package com.ruchira.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "camel.component.whatsapp")
@Configuration
@Getter
@Setter
public class CamelWhatsappConfiguration {

    private String phoneId;
    private String accessToken;
}
