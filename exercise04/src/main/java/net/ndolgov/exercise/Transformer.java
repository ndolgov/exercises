package net.ndolgov.exercise;

import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.ndolgov.exercise.MessageKeys.HASH_ORIGINAL;
import static net.ndolgov.exercise.MessageKeys.PRIVATE_PREFIX;

/**
 * Apply the required transformations to a given message, return the result as a new field map
 */
final class Transformer implements Function<JSONObject, JSONObject> {
    private static final boolean PRIVATE = true;

    @Override
    public JSONObject apply(JSONObject message) {
        final Set<Entry<String, Object>> entries = message.entrySet();

        // (T4) Transformation rules must ignore the values of fields whose names begin with an underscore
        final Map<Boolean, List<Entry<String, Object>>> isPrivateToEntries = entries.stream().
            collect(Collectors.partitioningBy((Predicate<Entry<String, Object>>)
                entry -> entry.getKey().startsWith(PRIVATE_PREFIX)));

        final List<Entry<String, Object>> privateEntries = isPrivateToEntries.getOrDefault(PRIVATE, Collections.emptyList());
        final Optional<Entry<String, Object>> fieldToHash = privateEntries.stream().
            filter(e -> e.getKey().equals(HASH_ORIGINAL)).
            findFirst();

        final List<Entry<String, Object>> regularEntries = isPrivateToEntries.getOrDefault(!PRIVATE, Collections.emptyList());

        final JSONObject allEntries = Stream.
            concat(
                privateEntries.stream(),
                regularEntries.stream().
                    map(ReverseStringValues.INSTANCE).
                    map(InvertNumericValues.INSTANCE)).
            collect(Collectors.toMap(
                Entry::getKey,
                Entry::getValue,
                (k,v) -> { throw new IllegalStateException("Duplicate key: " + k); },
                JSONObject::new));

        return Hasher.appendHashField(allEntries, fieldToHash);
    }

    /**
     * (T1) reverse any string value in the message that contains the exact string Quixotic
     */
    private static final class ReverseStringValues implements Function<Entry<String, Object>, Entry<String, Object>> {
        private static final String MAGIC_STRING = "Quixotic";

        static final Function<Entry<String, Object>, Entry<String, Object>> INSTANCE = new ReverseStringValues();

        @Override
        public Entry<String, Object> apply(Entry<String, Object> entry) {
            if (entry.getValue() instanceof String) {
                final String value = (String) entry.getValue();
                if (value.contains(MAGIC_STRING)) {
                    entry.setValue(new StringBuffer(value).reverse().toString());
                }
            }

            return entry;
        }
    }

    /**
     * (T2) replace any integer values with the value produced by computing the bitwise negation of that integer's value
     */
    private static final class InvertNumericValues implements Function<Entry<String, Object>, Entry<String, Object>> {
        static final Function<Entry<String, Object>, Entry<String, Object>> INSTANCE = new InvertNumericValues();

        @Override
        public Entry<String, Object> apply(Entry<String, Object> entry) {
            if (entry.getValue() instanceof Long) {
                final Long value = (Long) entry.getValue();
                entry.setValue(~value);
            }

            return entry;
        }
    }
}
