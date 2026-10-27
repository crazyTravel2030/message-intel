package com.messageintel.ingest.parse;

import java.util.LinkedHashMap;
import java.util.Map;

record ParsedPayload(String protocol, Map<String, Object> fields, String summary) {

    static ParsedPayload of(String protocol, Map<String, Object> fields, String summary) {
        return new ParsedPayload(protocol, fields == null ? Map.of() : fields, summary);
    }

    static Map<String, Object> mutable() {
        return new LinkedHashMap<>();
    }
}
