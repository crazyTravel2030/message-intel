package com.messageintel.ingest.parse;

import com.messageintel.common.dto.IngestMessageRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Order(50)
@Component
class KeyValueMessageParser implements MessageParser {

    private static final Pattern PAIR = Pattern.compile("([A-Za-z0-9_.-]+)\\s*[=:]\\s*([^,;\\n]+)");

    @Override
    public boolean supports(IngestMessageRequest request, String trimmedPayload) {
        return "KV".equalsIgnoreCase(request.protocol()) || PAIR.matcher(trimmedPayload).find();
    }

    @Override
    public ParsedPayload parse(IngestMessageRequest request, String trimmedPayload) {
        Map<String, Object> fields = ParsedPayload.mutable();
        Matcher matcher = PAIR.matcher(trimmedPayload);
        while (matcher.find()) {
            fields.put(matcher.group(1), matcher.group(2).trim());
        }
        return ParsedPayload.of(
                request.protocol() == null ? "KV" : request.protocol(),
                fields,
                "键值报文，解析出 " + fields.size() + " 个字段"
        );
    }
}
