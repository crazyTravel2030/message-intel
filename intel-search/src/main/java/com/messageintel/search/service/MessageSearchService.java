package com.messageintel.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.messageintel.common.dto.PagedResult;
import com.messageintel.common.dto.SearchQuery;
import com.messageintel.common.model.MessageRecord;
import com.messageintel.search.domain.MessageDocument;
import com.messageintel.search.repo.MessageDocumentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageSearchService {

    private final MessageDocumentRepository repository;
    private final ElasticsearchOperations operations;

    public MessageSearchService(MessageDocumentRepository repository, ElasticsearchOperations operations) {
        this.repository = repository;
        this.operations = operations;
    }

    public MessageRecord index(MessageRecord record) {
        return repository.save(MessageDocument.from(record)).toRecord();
    }

    public Optional<MessageRecord> findById(String id) {
        return repository.findById(id).map(MessageDocument::toRecord);
    }

    public PagedResult<MessageRecord> search(SearchQuery query) {
        Query esQuery = Query.of(q -> q.bool(buildBool(query)));
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(esQuery)
                .withPageable(PageRequest.of(query.page(), query.size()))
                .build();
        SearchHits<MessageDocument> hits = operations.search(nativeQuery, MessageDocument.class);
        var items = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(MessageDocument::toRecord)
                .toList();
        return new PagedResult<>(items, hits.getTotalHits(), query.page(), query.size());
    }

    public Map<String, Object> stats() {
        long total = repository.count();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", total);
        stats.put("index", "intel-messages");
        return stats;
    }

    private BoolQuery buildBool(SearchQuery query) {
        BoolQuery.Builder bool = new BoolQuery.Builder();
        if (query.q() != null && !query.q().isBlank()) {
            bool.must(m -> m.multiMatch(mm -> mm
                    .query(query.q())
                    .fields("rawPayload", "asciiPreview", "summary", "parsedFields.*")));
        } else {
            bool.must(m -> m.matchAll(ma -> ma));
        }
        if (hasText(query.protocol())) {
            bool.filter(f -> f.term(t -> t.field("protocol").value(query.protocol())));
        }
        if (hasText(query.source())) {
            bool.filter(f -> f.term(t -> t.field("source").value(query.source())));
        }
        if (hasText(query.destination())) {
            bool.filter(f -> f.term(t -> t.field("destination").value(query.destination())));
        }
        if (query.from() != null || query.to() != null) {
            bool.filter(f -> f.range(r -> r.date(d -> {
                d.field("capturedAt");
                if (query.from() != null) {
                    d.gte(query.from().toString());
                }
                if (query.to() != null) {
                    d.lte(query.to().toString());
                }
                return d;
            })));
        }
        return bool.build();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
