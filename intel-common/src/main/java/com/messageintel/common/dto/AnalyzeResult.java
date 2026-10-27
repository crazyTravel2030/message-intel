package com.messageintel.common.dto;

import com.messageintel.common.model.MessageRecord;

public record AnalyzeResult(MessageRecord message, String parserUsed) {
}
