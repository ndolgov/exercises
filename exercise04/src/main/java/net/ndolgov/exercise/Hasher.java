package net.ndolgov.exercise;

import org.json.simple.JSONObject;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static net.ndolgov.exercise.MessageKeys.HASH_GENERATED;

/**
 * Calculate the base64-encoded SHA-256 digest of the UTF-8-encoded value of target field.
 */
final class Hasher {
    private static final String SHA_256 = "SHA-256";

    /**
     * (T3) Add a field hash to any message that has a field _hash
     * @param fields message fields
     * @param fieldToHash reference to the filed to hash
     * @return updated message
     */
    public static JSONObject appendHashField(JSONObject fields, Optional<Map.Entry<String, Object>> fieldToHash) {
        fieldToHash.map(entry -> {
            try {
                final String calculatedHash = calculateHash(valueToHash(fields, entry));

                final String existingHashValue = (String) fields.get(HASH_GENERATED);
                if (!((existingHashValue == null) || (existingHashValue.equals(calculatedHash)))) {
                    throw new RuntimeException("Hash code mismatch, existing=" + existingHashValue + " calculated=" + calculatedHash);
                }

                fields.put(HASH_GENERATED, calculatedHash);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to append hash code field", e);
            }

            return entry; // no-op to keep compiler happy
        });

        return fields;
    }

    private static String calculateHash(String valueToHash) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(SHA_256);
            digest.update(valueToHash.getBytes());
            return Base64.getEncoder().encodeToString(digest.digest());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to calculate hash code", e);
        }
    }

    private static String valueToHash(JSONObject fields, Map.Entry<String, Object> hashField) {
        final String fieldToHashKey = (String) hashField.getValue();
        final String valueToHash = (String) fields.get(fieldToHashKey);
        if (valueToHash == null) {
            throw new IllegalArgumentException("Sequence field to hash not found: " + fieldToHashKey);
        }
        return valueToHash;
    }
}
