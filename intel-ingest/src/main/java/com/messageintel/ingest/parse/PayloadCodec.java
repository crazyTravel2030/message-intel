package com.messageintel.ingest.parse;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Locale;
import java.util.regex.Pattern;

final class PayloadCodec {

    private static final Pattern HEX_BODY = Pattern.compile("^[0-9a-fA-F\\s]+$");

    private PayloadCodec() {
    }

    static Charset charsetOf(String encoding) {
        if (encoding == null || encoding.isBlank()) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception ignored) {
            return StandardCharsets.UTF_8;
        }
    }

    static boolean looksLikeHex(String raw) {
        String compact = raw.replaceAll("\\s+", "");
        return compact.length() >= 8 && compact.length() % 2 == 0 && HEX_BODY.matcher(raw.trim()).matches();
    }

    static byte[] toBytes(String raw, String encoding) {
        if (looksLikeHex(raw)) {
            String compact = raw.replaceAll("\\s+", "");
            return HexFormat.of().parseHex(compact);
        }
        return raw.getBytes(charsetOf(encoding));
    }

    static String toHexDump(byte[] bytes) {
        StringBuilder dump = new StringBuilder();
        for (int i = 0; i < bytes.length; i += 16) {
            dump.append(String.format(Locale.ROOT, "%08X  ", i));
            int lineEnd = Math.min(i + 16, bytes.length);
            for (int j = i; j < i + 16; j++) {
                if (j < lineEnd) {
                    dump.append(String.format(Locale.ROOT, "%02X ", bytes[j]));
                } else {
                    dump.append("   ");
                }
                if (j == i + 7) {
                    dump.append(' ');
                }
            }
            dump.append(" |");
            for (int j = i; j < lineEnd; j++) {
                int b = bytes[j] & 0xFF;
                dump.append(b >= 32 && b < 127 ? (char) b : '.');
            }
            dump.append('|').append('\n');
        }
        return dump.toString();
    }

    static String asciiPreview(byte[] bytes) {
        StringBuilder preview = new StringBuilder();
        int limit = Math.min(bytes.length, 512);
        for (int i = 0; i < limit; i++) {
            int b = bytes[i] & 0xFF;
            if (b == '\n' || b == '\r' || b == '\t' || (b >= 32 && b < 127)) {
                preview.append((char) b);
            } else {
                preview.append('.');
            }
        }
        if (bytes.length > limit) {
            preview.append("...");
        }
        return preview.toString();
    }
}
