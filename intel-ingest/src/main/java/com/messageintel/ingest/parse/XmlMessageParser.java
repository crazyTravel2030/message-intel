package com.messageintel.ingest.parse;

import com.messageintel.common.dto.IngestMessageRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Order(20)
@Component
class XmlMessageParser implements MessageParser {

    @Override
    public boolean supports(IngestMessageRequest request, String trimmedPayload) {
        if ("XML".equalsIgnoreCase(request.protocol())) {
            return true;
        }
        return trimmedPayload.startsWith("<") && trimmedPayload.contains(">");
    }

    @Override
    public ParsedPayload parse(IngestMessageRequest request, String trimmedPayload) {
        Map<String, Object> fields = ParsedPayload.mutable();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setExpandEntityReferences(false);
            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(trimmedPayload.getBytes(StandardCharsets.UTF_8)));
            Element root = document.getDocumentElement();
            fields.put("root", root.getTagName());
            fields.put("elements", flatten(root));
            return ParsedPayload.of("XML", fields, "XML 报文，根节点 " + root.getTagName());
        } catch (Exception ex) {
            fields.put("parseError", ex.getMessage());
            return ParsedPayload.of("XML", fields, "XML 解析失败");
        }
    }

    private Map<String, Object> flatten(Element element) {
        Map<String, Object> node = ParsedPayload.mutable();
        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            Map<String, Object> attrs = ParsedPayload.mutable();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                attrs.put(attr.getNodeName(), attr.getNodeValue());
            }
            node.put("@attributes", attrs);
        }
        NodeList children = element.getChildNodes();
        List<Object> nested = new ArrayList<>();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element childElement) {
                nested.add(Map.of(childElement.getTagName(), flatten(childElement)));
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                text.append(child.getTextContent());
            }
        }
        String value = text.toString().trim();
        if (!value.isEmpty()) {
            node.put("value", value);
        }
        if (!nested.isEmpty()) {
            node.put("children", nested);
        }
        return node;
    }
}
