package com.dormnet.resourceservice.model;

public record ResourceRequest(
        Long id,
        String name,
        String status,
        boolean available
) {
}
