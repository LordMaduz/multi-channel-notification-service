package com.ruchira.notification.router;

import com.ruchira.notification.handler.NotificationHandler;
import com.ruchira.notification.service.RequestService;
import com.ruchira.notification.vo.NotificationRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterConfig {

    private static final String NOTIFICATION_PATH = "/notification";

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = NOTIFICATION_PATH, produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                            beanClass = RequestService.class, method = RequestMethod.POST, beanMethod = "handleRequest",
                            operation = @Operation(operationId = "handleRequest", responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful Operation",
                                            content = @Content(schema = @Schema(implementation = NotificationRequestVO.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid Operation")}
                            ))
            })
    public RouterFunction<ServerResponse> routes(NotificationHandler notificationHandler) {
        return RouterFunctions
                .route(POST(NOTIFICATION_PATH).and(accept(MediaType.APPLICATION_JSON))
                        , notificationHandler::triggerNotificationRequest)
                ;
    }
}
