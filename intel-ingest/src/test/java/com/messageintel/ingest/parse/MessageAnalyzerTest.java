package com.messageintel.ingest.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messageintel.common.dto.AnalyzeResult;
import com.messageintel.common.dto.IngestMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageAnalyzerTest {

    private MessageAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new MessageAnalyzer(List.of(
                new JsonMessageParser(new ObjectMapper()),
                new XmlMessageParser(),
                new HttpMessageParser(),
                new HexMessageParser(),
                new KeyValueMessageParser()
        ));
    }

    @Test
    void analyzesJson() {
        AnalyzeResult result = analyzer.analyze(new IngestMessageRequest(
                null, "core", "risk", null, "UTF-8",
                "{\"txnId\":\"T-1001\",\"amount\":99.5}",
                List.of("prod"), null
        ));
        assertEquals("JSON", result.parserUsed());
        assertEquals("T-1001", result.message().parsedFields().get("txnId"));
    }

    @Test
    void analyzesHttp() {
        String payload = "POST /pay HTTP/1.1\nHost: bank.local\n\n{\"ok\":true}";
        AnalyzeResult result = analyzer.analyze(new IngestMessageRequest(
                null, "gw", "pay", null, "UTF-8", payload, null, null
        ));
        assertEquals("HTTP", result.parserUsed());
        assertTrue(result.message().summary().contains("POST /pay"));
    }
}
