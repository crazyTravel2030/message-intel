package com.messageintel.common.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record MessageRecord(
        String id,
        Instant capturedAt,
        Instant ingestedAt,
        String source,
        String destination,
        String protocol,
        String encoding,
        String rawPayload,
        String hexDump,
        String asciiPreview,
        Map<String, Object> parsedFields,
        String summary,
        List<String> tags,
        Integer payloadLength
) {
}
