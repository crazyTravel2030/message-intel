package com.messageintel.search.domain;

import com.messageintel.common.model.MessageRecord;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(indexName = "intel-messages")
public class MessageDocument {

    @Id
    private String id;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant capturedAt;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant ingestedAt;

    @Field(type = FieldType.Keyword)
    private String source;

    @Field(type = FieldType.Keyword)
    private String destination;

    @Field(type = FieldType.Keyword)
    private String protocol;

    @Field(type = FieldType.Keyword)
    private String encoding;

    @Field(type = FieldType.Text)
    private String rawPayload;

    @Field(type = FieldType.Text, index = false)
    private String hexDump;

    @Field(type = FieldType.Text)
    private String asciiPreview;

    @Field(type = FieldType.Object, enabled = true)
    private Map<String, Object> parsedFields;

    @Field(type = FieldType.Text)
    private String summary;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Integer)
    private Integer payloadLength;

    public static MessageDocument from(MessageRecord record) {
        MessageDocument document = new MessageDocument();
        document.id = record.id();
        document.capturedAt = record.capturedAt();
        document.ingestedAt = record.ingestedAt();
        document.source = record.source();
        document.destination = record.destination();
        document.protocol = record.protocol();
        document.encoding = record.encoding();
        document.rawPayload = record.rawPayload();
        document.hexDump = record.hexDump();
        document.asciiPreview = record.asciiPreview();
        document.parsedFields = record.parsedFields();
        document.summary = record.summary();
        document.tags = record.tags();
        document.payloadLength = record.payloadLength();
        return document;
    }

    public MessageRecord toRecord() {
        return new MessageRecord(
                id, capturedAt, ingestedAt, source, destination, protocol, encoding,
                rawPayload, hexDump, asciiPreview, parsedFields, summary, tags, payloadLength
        );
    }

    public String getId() {
        return id;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public Instant getIngestedAt() {
        return ingestedAt;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public String getHexDump() {
        return hexDump;
    }

    public String getAsciiPreview() {
        return asciiPreview;
    }

    public Map<String, Object> getParsedFields() {
        return parsedFields;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getTags() {
        return tags;
    }

    public Integer getPayloadLength() {
        return payloadLength;
    }
}
