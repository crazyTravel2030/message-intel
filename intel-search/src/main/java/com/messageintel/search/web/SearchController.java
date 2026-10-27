package com.messageintel.search.web;

import com.messageintel.common.api.ApiResponse;
import com.messageintel.common.dto.PagedResult;
import com.messageintel.common.dto.SearchQuery;
import com.messageintel.common.model.MessageRecord;
import com.messageintel.search.service.MessageSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final MessageSearchService searchService;

    public SearchController(MessageSearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/messages")
    public ApiResponse<MessageRecord> index(@RequestBody MessageRecord record) {
        return ApiResponse.ok(searchService.index(record));
    }

    @GetMapping("/messages/{id}")
    public ApiResponse<MessageRecord> get(@PathVariable String id) {
        return searchService.findById(id)
                .map(ApiResponse::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "报文不存在"));
    }

    @GetMapping("/messages")
    public ApiResponse<PagedResult<MessageRecord>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String protocol,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        SearchQuery query = new SearchQuery(q, protocol, source, destination, from, to, page, size);
        return ApiResponse.ok(searchService.search(query));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.ok(searchService.stats());
    }
}
