package com.ruchira.notification.service.impl;

import com.ruchira.notification.service.NotificationSenderService;
import com.ruchira.notification.service.RequestService;
import com.ruchira.notification.util.ResponseUtility;
import com.ruchira.notification.vo.NotificationRequestVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRequestService implements RequestService {

    private final NotificationSenderService notificationSenderService;
    private final ResponseUtility responseUtility;

    private static final String EMAIL_CHANNEL = "EMAIL";
    private static final String WHATSAPP_CHANNEL = "WHATSAPP";
    private static final String TELEGRAM_CHANNEL = "TELEGRAM";
    private static final String TWILIO_CHANNEL = "TWILIO";

    @Override
    public Mono<Object> handleRequest(final NotificationRequestVO notificationRequestVO) {
        if (notificationRequestVO.getChannel().equalsIgnoreCase(EMAIL_CHANNEL)) {
            CompletableFuture.runAsync(notificationSenderService::sendNotificationEmail);
        } else if (notificationRequestVO.getChannel().equalsIgnoreCase(WHATSAPP_CHANNEL)) {
            CompletableFuture.runAsync(notificationSenderService::sendWhatsappNotification);
        } else if (notificationRequestVO.getChannel().equalsIgnoreCase(TELEGRAM_CHANNEL)) {
            CompletableFuture.runAsync(notificationSenderService::sendTelegramNotification);
        } else if (notificationRequestVO.getChannel().equalsIgnoreCase(TWILIO_CHANNEL)) {
            CompletableFuture.runAsync(notificationSenderService::sendSMSNotification);
        }
        return Mono.just(responseUtility.successResponse("SUCCESS", "200"));
    }
}
