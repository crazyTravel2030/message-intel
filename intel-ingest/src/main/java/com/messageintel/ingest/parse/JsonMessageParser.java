package com.messageintel.ingest.parse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.messageintel.common.dto.IngestMessageRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Order(10)
@Component
class JsonMessageParser implements MessageParser {

    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    JsonMessageParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(IngestMessageRequest request, String trimmedPayload) {
        if ("JSON".equalsIgnoreCase(request.protocol())) {
            return true;
        }
        return trimmedPayload.startsWith("{") || trimmedPayload.startsWith("[");
    }

    @Override
    public ParsedPayload parse(IngestMessageRequest request, String trimmedPayload) {
        Map<String, Object> fields = ParsedPayload.mutable();
        try {
            if (trimmedPayload.startsWith("{")) {
                fields.putAll(objectMapper.readValue(trimmedPayload, MAP));
            } else {
                fields.put("items", objectMapper.readValue(trimmedPayload, Object.class));
            }
            fields.put("format", "json");
            return ParsedPayload.of("JSON", fields, "JSON 报文，字段数 " + fields.size());
        } catch (Exception ex) {
            fields.put("parseError", ex.getMessage());
            return ParsedPayload.of("JSON", fields, "JSON 解析失败");
        }
    }
}
