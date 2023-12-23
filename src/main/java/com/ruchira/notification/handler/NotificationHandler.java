package com.ruchira.notification.handler;

import com.ruchira.notification.service.RequestService;
import com.ruchira.notification.vo.NotificationRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class NotificationHandler {

    private final RequestService requestService;
    public Mono<ServerResponse> triggerNotificationRequest(ServerRequest serverRequest) {

        Mono<Object> mono = serverRequest.bodyToMono(NotificationRequestVO.class).flatMap(requestService::handleRequest);
        return Mono
                .from(mono)
                .flatMap(p -> ServerResponse
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(p))
                );
    }
}
