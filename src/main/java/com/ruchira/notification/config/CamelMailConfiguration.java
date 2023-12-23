package com.ruchira.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "camel.component.mail")
@Configuration
@Getter
@Setter
public class CamelMailConfiguration {
    private String host;
    private Integer port;
    private String userName;
    private String password;
    private String from;
    private Boolean starttls;
    private Boolean auth;
    private Boolean ssl;
}
