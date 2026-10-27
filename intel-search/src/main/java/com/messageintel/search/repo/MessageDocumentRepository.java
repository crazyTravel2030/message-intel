package com.messageintel.search.repo;

import com.messageintel.search.domain.MessageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MessageDocumentRepository extends ElasticsearchRepository<MessageDocument, String> {
}
