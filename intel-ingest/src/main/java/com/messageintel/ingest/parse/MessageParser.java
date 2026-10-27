package com.messageintel.ingest.parse;

import com.messageintel.common.dto.IngestMessageRequest;

interface MessageParser {

    boolean supports(IngestMessageRequest request, String trimmedPayload);

    ParsedPayload parse(IngestMessageRequest request, String trimmedPayload);
}
