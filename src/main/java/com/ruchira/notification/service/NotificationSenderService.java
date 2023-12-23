package com.ruchira.notification.service;


import com.ruchira.notification.config.CamelMailConfiguration;
import com.ruchira.notification.util.NotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.attachment.DefaultAttachment;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationSenderService {

    private final CamelMailConfiguration configuration;

    public void sendNotificationEmail() {

        try (final CamelContext context = new DefaultCamelContext()) {

            final String uri = String.format("smtp://%s:%d?username=%s&password=%s&from=%s&mail.smtp.starttls.enable=%b&mail.smtp.auth=%b&mail.smtp.ssl.enable=%b",
                    configuration.getHost(), configuration.getPort(), configuration.getUserName(),
                    configuration.getPassword(), configuration.getUserName(),
                    configuration.getStarttls(), configuration.getAuth(), configuration.getSsl());

            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    from("seda:start")
                            .to(uri)
                            .log("Email sent with content ${in.body}");
                }
            });

            context.start();
            Endpoint endpoint = context.getEndpoint("seda:start");

            Exchange exchange = endpoint.createExchange();
            AttachmentMessage in = exchange.getIn(AttachmentMessage.class);

            DefaultAttachment attachment = new DefaultAttachment(NotificationUtil.getDataSource());
            attachment.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=application.yaml");
            in.addAttachmentObject("application.yaml", attachment);

            in.setHeaders(NotificationUtil.getEmailHeaders());
            in.setBody(NotificationUtil.getEmailBody());
            ProducerTemplate producer = context.createProducerTemplate();
            producer.asyncSend(endpoint, exchange);
            context.stop();
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

    }

}
