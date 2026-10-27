package com.messageintel.search.service;

import com.messageintel.common.dto.PagedResult;
import com.messageintel.common.dto.SearchQuery;
import com.messageintel.common.model.MessageRecord;
import com.messageintel.search.store.MessageStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageSearchService {

    private final MessageStore store;
    private final String storage;

    public MessageSearchService(MessageStore store, @Value("${intel.search.storage:memory}") String storage) {
        this.store = store;
        this.storage = storage;
    }

    public MessageRecord index(MessageRecord record) {
        return store.save(record);
    }

    public Optional<MessageRecord> findById(String id) {
        return store.findById(id);
    }

    public PagedResult<MessageRecord> search(SearchQuery query) {
        return store.search(query);
    }

    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", store.count());
        stats.put("storage", storage);
        stats.put("index", "intel-messages");
        return stats;
    }
}
