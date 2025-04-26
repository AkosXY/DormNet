package com.dormnet.reservationservice.mock;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@UtilityClass
public class ResourceClientStub {

    public static void stubIsAvailable(Long id, boolean isAvailable) {
        stubFor(get(urlEqualTo("/api/resource/available?id=" + id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.valueOf(isAvailable))));
    }
}
