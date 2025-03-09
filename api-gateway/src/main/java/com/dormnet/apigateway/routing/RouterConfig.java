package com.dormnet.apigateway.routing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.POST;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class RouterConfig {


    @Bean
    public RouterFunction<ServerResponse> sportServiceRoute(@Value("${router.url.sport_service}") String sportServiceUrl) {
        return GatewayRouterFunctions.route("sport_service")
                .route(path("/api/sport"), http(sportServiceUrl))
                .route(path("/api/sport/{eventId}/add_entries"), http(sportServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> reservationServiceRoutes(@Value("${router.url.reservation_service}") String reservationServiceUrl) {
        return GatewayRouterFunctions.route("reservation_service")
                .route(path("/api/reservation/reserve"), http(reservationServiceUrl))
                .route(path("/api/reservation/drop"), http(reservationServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> resourceServiceRoutes(@Value("${router.url.resource_service}") String resourceServiceUrl) {
        return GatewayRouterFunctions.route("resource_service")
                .route(path("/api/resource/available"), http(resourceServiceUrl))
                .route(POST("/api/resource/makeUnavailable"), http(resourceServiceUrl))
                .route(POST("/api/resource/makeAvailable"), http(resourceServiceUrl))
                .route(POST("/api/resource/add"), http(resourceServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> accommodationServiceRoutes(@Value("${router.url.accommodation_service}") String accommodationServiceUrl) {
        return GatewayRouterFunctions.route("accommodation_service")
                .route(POST("/api/resident"), http(accommodationServiceUrl))
                .route(POST("/api/resident/{id}/unassign"), http(accommodationServiceUrl))
                .route(POST("/api/resident/{residentId}/assign/{roomId}"), http(accommodationServiceUrl))
                .route(path("/api/resident"), http(accommodationServiceUrl))
                .route(POST("/api/rooms"), http(accommodationServiceUrl))
                .route(path("/api/rooms"), http(accommodationServiceUrl))
                .build();
    }


}
