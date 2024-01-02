package com.ruchira.notification.service;


import com.ruchira.notification.config.CamelMailConfiguration;
import com.ruchira.notification.config.CamelTelegramConfiguration;
import com.ruchira.notification.config.CamelTwilioConfiguration;
import com.ruchira.notification.config.CamelWhatsappConfiguration;
import com.ruchira.notification.util.NotificationUtil;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.attachment.DefaultAttachment;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.component.twilio.TwilioComponent;
import org.apache.camel.component.whatsapp.model.TemplateMessageRequest;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import static org.apache.camel.builder.Builder.constant;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationSenderService {

    private final CamelMailConfiguration configuration;
    private final CamelWhatsappConfiguration whatsappConfiguration;
    private final CamelTelegramConfiguration telegramConfiguration;
    private final CamelTwilioConfiguration twilioConfiguration;

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

    public void sendWhatsappNotification() {
        try (final CamelContext context = new DefaultCamelContext()) {

            final String uri = String.format("whatsapp://%s?authorizationToken=%s",
                    whatsappConfiguration.getPhoneId(), whatsappConfiguration.getAccessToken());

            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {

                    from("seda:start")
                            .to(uri)
                            .log("Whatsapp Message sent with content ${in.body}").end();

                }
            });

            context.start();
            final TemplateMessageRequest templateMessageRequest = NotificationUtil.getMessageRequest();
            ProducerTemplate producer = context.createProducerTemplate();
            producer.asyncRequestBody("seda:start", templateMessageRequest);
            context.stop();
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    public void sendTelegramNotification() {
        try (final CamelContext context = new DefaultCamelContext()) {

            final String uri = String.format("telegram:bots?authorizationToken=%s&chatId=%s",
                    telegramConfiguration.getAuthToken(), telegramConfiguration.getChatId());
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {

                    from("seda:start")
                            .to(uri)
                            .log("Telegram Message sent with content ${in.body}");

                }
            });

            context.start();

            final OutgoingTextMessage outgoingMessage = NotificationUtil.getTelegramMessage(null);
            ProducerTemplate producer = context.createProducerTemplate();
            producer.asyncRequestBody("seda:start", outgoingMessage);
            context.stop();


        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    public void sendSMSNotification() {
        try (final CamelContext context = new DefaultCamelContext()) {

            setTwilioConfiguration(context);
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {

                    from("seda:start")
                            .setHeader("CamelTwilioTo", value(NotificationUtil.twilioDestinationPhoneNumber(), PhoneNumber.class))
                            .setHeader("CamelTwilioFrom", value(twilioConfiguration.getPhoneNumber(), PhoneNumber.class))
                            .setHeader("CamelTwilioBody", value(NotificationUtil.twilioMessage(), String.class))
                            .to("twilio://message/creator")
                            .log("SMS Message sent with content ${in.body}");

                }
            });

            context.start();

            ProducerTemplate producer = context.createProducerTemplate();
            producer.asyncRequestBody("seda:start", NotificationUtil.twilioMessage());
            context.stop();

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private void setTwilioConfiguration(final CamelContext context) {
        final TwilioComponent twilio = context.getComponent("twilio", TwilioComponent.class);
        twilio.setUsername(twilioConfiguration.getAccountSid());
        twilio.setPassword(twilioConfiguration.getAuthToken());
    }

    private <T> ValueBuilder value(final String value, final Class<T> type) {
        if (type == PhoneNumber.class) {
            return constant(new PhoneNumber(value));
        } else if (type == String.class) {
            return constant(value);
        }
        return null;
    }

}
