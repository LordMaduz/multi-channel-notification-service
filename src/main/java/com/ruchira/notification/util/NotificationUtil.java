package com.ruchira.notification.util;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.experimental.UtilityClass;
import org.apache.camel.Exchange;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class NotificationUtil {

    public Map<String, Object> getEmailHeaders() {
        final Map<String, Object> map = new HashMap<>();
        map.put("subject", "EMAIL_SUBJECT_CUSTOMIZE");
        map.put("to", "TO_MAIL_ADDRESS_CUSTOMIZE");
        map.put(Exchange.CONTENT_TYPE, "text/html; charset=UTF-8");

        return map;
    }

    public Object getEmailBody() {
        return "Email Received from Camel Notification Service";
    }

    public DataSource getDataSource() throws IOException {
        final File file = ResourceUtils.getFile("classpath:application.yaml");

        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            return new ByteArrayDataSource(fileInputStream.readAllBytes(), MediaType.APPLICATION_OCTET_STREAM_VALUE);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
