package com.dormnet.apigateway.routing;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> sportServiceRoute() {
        return GatewayRouterFunctions.route("sport_service")
                .route(RequestPredicates.path("/api/sport"), http("http://localhost:8080"))
                .route(RequestPredicates.path("/api/sport/{eventId}/add_entries"), http("http://localhost:8080"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> reservationServiceRoutes() {
        return GatewayRouterFunctions.route("reservation_service")
                .route(RequestPredicates.path("/api/reservation/reserve"), http("http://localhost:8081"))
                .route(RequestPredicates.path("/api/reservation/drop"), http("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> resourceServiceRoutes() {
        return GatewayRouterFunctions.route("resource_service")
                .route(RequestPredicates.path("/api/resource/available"), http("http://localhost:8082"))
                .route(RequestPredicates.POST("/api/resource/makeUnavailable"), http("http://localhost:8082"))
                .route(RequestPredicates.POST("/api/resource/makeAvailable"), http("http://localhost:8082"))
                .route(RequestPredicates.POST("/api/resource/admin/add"), http("http://localhost:8082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> accommodationServiceRoutes() {
        return GatewayRouterFunctions.route("accommodation_service")
                .route(RequestPredicates.POST("/api/resident"), http("http://localhost:8083"))
                .route(RequestPredicates.POST("/api/resident/{id}/unassign"), http("http://localhost:8083"))
                .route(RequestPredicates.POST("/api/resident/{residentId}/assign/{roomId}"), http("http://localhost:8083"))

                .route(RequestPredicates.path("/api/resident"), http("http://localhost:8083"))

                .route(RequestPredicates.POST("/api/rooms"), http("http://localhost:8083"))
                .route(RequestPredicates.path("/api/rooms"), http("http://localhost:8083"))
                .build();
    }



}
