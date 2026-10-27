package com.messageintel.ingest.client;

import com.messageintel.common.api.ApiResponse;
import com.messageintel.common.model.MessageRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "intel-search")
public interface SearchIndexClient {

    @PostMapping("/api/search/messages")
    ApiResponse<MessageRecord> index(@RequestBody MessageRecord record);
}
