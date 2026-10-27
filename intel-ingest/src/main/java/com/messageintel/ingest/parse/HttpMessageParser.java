package com.messageintel.ingest.parse;

import com.messageintel.common.dto.IngestMessageRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Order(30)
@Component
class HttpMessageParser implements MessageParser {

    @Override
    public boolean supports(IngestMessageRequest request, String trimmedPayload) {
        if ("HTTP".equalsIgnoreCase(request.protocol())) {
            return true;
        }
        String first = firstLine(trimmedPayload).toUpperCase(Locale.ROOT);
        return first.startsWith("GET ") || first.startsWith("POST ") || first.startsWith("PUT ")
                || first.startsWith("DELETE ") || first.startsWith("PATCH ") || first.startsWith("HTTP/");
    }

    @Override
    public ParsedPayload parse(IngestMessageRequest request, String trimmedPayload) {
        Map<String, Object> fields = ParsedPayload.mutable();
        String[] parts = trimmedPayload.split("\\r?\\n\\r?\\n", 2);
        String headerBlock = parts[0];
        String body = parts.length > 1 ? parts[1] : "";
        String[] lines = headerBlock.split("\\r?\\n");
        fields.put("startLine", lines[0]);
        Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 1; i < lines.length; i++) {
            int idx = lines[i].indexOf(':');
            if (idx > 0) {
                headers.put(lines[i].substring(0, idx).trim(), lines[i].substring(idx + 1).trim());
            }
        }
        fields.put("headers", headers);
        fields.put("body", body);
        fields.put("bodyLength", body.length());
        return ParsedPayload.of("HTTP", fields, "HTTP 报文：" + lines[0]);
    }

    private String firstLine(String payload) {
        int nl = payload.indexOf('\n');
        return nl < 0 ? payload : payload.substring(0, nl).trim();
    }
}
