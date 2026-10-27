package com.messageintel.search.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@ConditionalOnProperty(name = "intel.search.storage", havingValue = "elasticsearch")
@EnableElasticsearchRepositories(basePackages = "com.messageintel.search.repo")
public class ElasticsearchConfig {
}
