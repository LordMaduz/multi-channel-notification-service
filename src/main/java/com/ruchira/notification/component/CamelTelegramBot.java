package com.ruchira.notification.component;

import com.ruchira.notification.config.CamelTelegramConfiguration;
import com.ruchira.notification.util.NotificationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CamelTelegramBot {

    private final CamelTelegramConfiguration telegramConfiguration;

    @PostConstruct
    public void listenToTelegramBot() {
        try (final CamelContext context = new DefaultCamelContext()) {
            final String uri = String.format("telegram:bots?authorizationToken=%s&chatId=%s",
                    telegramConfiguration.getAuthToken(), telegramConfiguration.getChatId());

            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {

                    from(uri)
                            .process(exchange -> {
                                final OutgoingTextMessage msg = NotificationUtil.getTelegramMessage(exchange);
                                exchange.getIn().setBody(msg);
                            })
                            .to(uri);
                }
            });

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

}
