package com.messageintel.ingest.parse;

import com.messageintel.common.dto.IngestMessageRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Order(40)
@Component
class HexMessageParser implements MessageParser {

    @Override
    public boolean supports(IngestMessageRequest request, String trimmedPayload) {
        if ("HEX".equalsIgnoreCase(request.protocol()) || "BINARY".equalsIgnoreCase(request.protocol())) {
            return true;
        }
        return PayloadCodec.looksLikeHex(trimmedPayload);
    }

    @Override
    public ParsedPayload parse(IngestMessageRequest request, String trimmedPayload) {
        byte[] bytes = PayloadCodec.toBytes(trimmedPayload, request.encoding());
        Map<String, Object> fields = ParsedPayload.mutable();
        fields.put("byteLength", bytes.length);
        fields.put("hexLength", trimmedPayload.replaceAll("\\s+", "").length());
        if (bytes.length >= 2) {
            fields.put("firstTwoBytes", String.format("%02X%02X", bytes[0], bytes[1]));
        }
        return ParsedPayload.of("HEX", fields, "二进制/十六进制报文，长度 " + bytes.length + " 字节");
    }
}
