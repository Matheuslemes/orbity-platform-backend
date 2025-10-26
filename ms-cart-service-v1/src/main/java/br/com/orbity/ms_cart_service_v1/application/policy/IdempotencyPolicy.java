package br.com.orbity.ms_cart_service_v1.application.policy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IdempotencyPolicy {

    private static final String DEFAULT_PREFIX = "idem:cart";
    private static final String SEP = ":";

    private final String prefix;
    private final int maxLength;

    public IdempotencyPolicy() {
        this(DEFAULT_PREFIX, 200);
    }

    public IdempotencyPolicy(String prefix, int maxLength) {
        this.prefix = (prefix == null || prefix.isBlank()) ? DEFAULT_PREFIX : prefix.trim();
        this.maxLength = maxLength > 0 ? maxLength : 200;
    }

    public String key(String operation, String... parts) {
        final String op = sanitize(operation, "op");
        StringBuilder sb = new StringBuilder()
                .append(prefix).append(SEP).append(op);

        if (parts != null) {
            for (String p : parts) {
                sb.append(SEP).append(sanitize(p, "na"));
            }
        }

        String raw = sb.toString();
        if (raw.length() <= maxLength) {
            return raw;
        }

        return prefix + SEP + op + SEP + sha256Hex(raw);
    }


    private String sanitize(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        if (t.isEmpty()) return fallback;

        t = t.toLowerCase()
                .replace(':', '-')
                .replaceAll("\\s+", "-")
                .replaceAll("[\\r\\n\\t]", "");
        return t;
    }

    private String sha256Hex(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(dig.length * 2);
            for (byte b : dig) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {

            return Integer.toHexString(raw.hashCode());

        }

    }

}
