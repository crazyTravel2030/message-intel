package com.messageintel.common.dto;

import java.time.Instant;

public record SearchQuery(
        String q,
        String protocol,
        String source,
        String destination,
        Instant from,
        Instant to,
        int page,
        int size
) {
    public SearchQuery {
        page = Math.max(page, 0);
        size = size <= 0 ? 20 : Math.min(size, 100);
    }
}
