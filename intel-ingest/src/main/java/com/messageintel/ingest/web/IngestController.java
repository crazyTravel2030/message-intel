package com.messageintel.ingest.web;

import com.messageintel.common.api.ApiResponse;
import com.messageintel.common.dto.AnalyzeResult;
import com.messageintel.common.dto.IngestMessageRequest;
import com.messageintel.common.model.MessageRecord;
import com.messageintel.ingest.client.SearchIndexClient;
import com.messageintel.ingest.parse.MessageAnalyzer;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ingest")
public class IngestController {

    private final MessageAnalyzer analyzer;
    private final SearchIndexClient searchIndexClient;

    public IngestController(MessageAnalyzer analyzer, SearchIndexClient searchIndexClient) {
        this.analyzer = analyzer;
        this.searchIndexClient = searchIndexClient;
    }

    @PostMapping("/analyze")
    public ApiResponse<AnalyzeResult> analyze(@Valid @RequestBody IngestMessageRequest request) {
        return ApiResponse.ok(analyzer.analyze(request));
    }

    @PostMapping("/messages")
    public ApiResponse<MessageRecord> ingest(@Valid @RequestBody IngestMessageRequest request) {
        AnalyzeResult analyzed = analyzer.analyze(request);
        ApiResponse<MessageRecord> indexed = searchIndexClient.index(analyzed.message());
        return indexed.success() ? indexed : ApiResponse.fail(indexed.message());
    }

    @PostMapping("/messages/batch")
    public ApiResponse<List<MessageRecord>> ingestBatch(@Valid @RequestBody List<IngestMessageRequest> requests) {
        List<MessageRecord> saved = new ArrayList<>();
        for (IngestMessageRequest request : requests) {
            AnalyzeResult analyzed = analyzer.analyze(request);
            ApiResponse<MessageRecord> indexed = searchIndexClient.index(analyzed.message());
            if (indexed.success() && indexed.data() != null) {
                saved.add(indexed.data());
            }
        }
        return ApiResponse.ok(saved);
    }
}
