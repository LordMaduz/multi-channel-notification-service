package com.ruchira.notification.service;

import com.ruchira.notification.vo.NotificationRequestVO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface RequestService {
    Mono<Object> handleRequest(@RequestBody final NotificationRequestVO requestVO);
}
