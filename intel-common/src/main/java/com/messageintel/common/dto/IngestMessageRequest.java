package com.messageintel.common.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record IngestMessageRequest(
        Instant capturedAt,
        String source,
        String destination,
        String protocol,
        String encoding,
        @NotBlank(message = "rawPayload 不能为空") String rawPayload,
        List<String> tags,
        Map<String, Object> extraFields
) {
}
