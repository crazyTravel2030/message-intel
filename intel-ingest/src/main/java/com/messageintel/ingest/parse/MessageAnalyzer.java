package com.messageintel.ingest.parse;

import com.messageintel.common.dto.AnalyzeResult;
import com.messageintel.common.dto.IngestMessageRequest;
import com.messageintel.common.model.MessageRecord;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageAnalyzer {

    private final List<MessageParser> parsers;

    public MessageAnalyzer(List<MessageParser> parsers) {
        this.parsers = parsers;
    }

    public AnalyzeResult analyze(IngestMessageRequest request) {
        String payload = request.rawPayload() == null ? "" : request.rawPayload().trim();
        ParsedPayload parsed = selectParser(request, payload).parse(request, payload);
        byte[] bytes = PayloadCodec.toBytes(payload, request.encoding());

        Map<String, Object> fields = new LinkedHashMap<>(parsed.fields());
        if (request.extraFields() != null) {
            fields.putAll(request.extraFields());
        }

        List<String> tags = request.tags() == null ? new ArrayList<>() : new ArrayList<>(request.tags());
        tags.add(parsed.protocol().toLowerCase());

        MessageRecord record = new MessageRecord(
                UUID.randomUUID().toString(),
                request.capturedAt() == null ? Instant.now() : request.capturedAt(),
                Instant.now(),
                request.source(),
                request.destination(),
                parsed.protocol(),
                request.encoding() == null ? "UTF-8" : request.encoding(),
                payload,
                PayloadCodec.toHexDump(bytes),
                PayloadCodec.asciiPreview(bytes),
                fields,
                parsed.summary(),
                tags,
                bytes.length
        );
        return new AnalyzeResult(record, parsed.protocol());
    }

    private MessageParser selectParser(IngestMessageRequest request, String payload) {
        return parsers.stream()
                .filter(parser -> parser.supports(request, payload))
                .findFirst()
                .orElseGet(FallbackParser::new);
    }

    private static final class FallbackParser implements MessageParser {
        @Override
        public boolean supports(IngestMessageRequest request, String trimmedPayload) {
            return true;
        }

        @Override
        public ParsedPayload parse(IngestMessageRequest request, String trimmedPayload) {
            Map<String, Object> fields = ParsedPayload.mutable();
            fields.put("lineCount", trimmedPayload.isBlank() ? 0 : trimmedPayload.split("\\R").length);
            String protocol = request.protocol() == null || request.protocol().isBlank() ? "TEXT" : request.protocol();
            return ParsedPayload.of(protocol, fields, "文本报文，长度 " + trimmedPayload.length());
        }
    }
}
