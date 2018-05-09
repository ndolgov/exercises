package net.ndolgov.exercise;

import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static net.ndolgov.exercise.MessageKeys.FIRST_PART;
import static net.ndolgov.exercise.MessageKeys.HASH_GENERATED;
import static net.ndolgov.exercise.MessageKeys.PART_CORRELATION_ID;
import static net.ndolgov.exercise.MessageKeys.PART_INDEX;
import static net.ndolgov.exercise.MessageKeys.PRIVATE_PREFIX;
import static net.ndolgov.exercise.MessageKeys.SPECIAL;

/**
 * Route a processed message to appropriate queue from a small static set. The dispatcher can be configured with
 * any number of rules as long as the dispatching strategy is to use the first matching rule found by sequential search.
 *
 * Only the first part of a multi-part message is dispatched. The rest are directed to the same queue for as long as
 * the queue remembers the sequence.
 */
final class Dispatcher implements Function<JSONObject, Void> {
    private static final String MAGIC_STRING = "citoxiuQ";

    private final MessageQueue[] queues;

    private final Function<Map<String, Object>, Optional<Integer>>[] rules;

    public Dispatcher(MessageQueue[] queues) {
        this.queues = queues;
        this.rules = createRules();
    }

    @Override
    public Void apply(JSONObject message) {
        final String correlationId = (String) message.get(PART_CORRELATION_ID);
        if (correlationId == null) {
            findQueue(message).enqueue(message.toJSONString());
        } else {
            enqueuePart(message, correlationId);
        }

        return null;
    }

    private void enqueuePart(JSONObject message, String correlationId) {
        final long partId = partId(message);
        final MessageQueue queue = (partId == FIRST_PART) ? findQueue(message) : findQueue(correlationId); // dispatch the first message in a sequence only
        queue.enqueue(message.toJSONString(), correlationId, partId);
    }

    // it's cheap and allows a MQ to clean up part dispatching state internally
    private MessageQueue findQueue(String correlationId) {
        for (int i = 0; i < queues.length; i++) {
            if (queues[i].isSequenceKnown(correlationId)) {
                return queues[i];
            }
        }

        throw new IllegalArgumentException("No queue found for sequence: " + correlationId);
    }

    private MessageQueue findQueue(JSONObject message) {
        for (Function<Map<String, Object>, Optional<Integer>> rule : rules) {
            final Optional<Integer> decision = rule.apply(message);
            if (decision.isPresent()) {
                return queues[decision.get()];
            }
        }

        throw new IllegalStateException("No default rule was found");
    }

    private static long partId(JSONObject message) {
        final Long partId = (Long) message.get(PART_INDEX);
        if (partId == null) {
            throw new IllegalArgumentException("Part index must be present for a sequence message");
        }
        return partId;
    }

    /**
     * @return message dispatching rules that are supposed to be applied sequentially until the first match is found
     */
    private static Function<Map<String, Object>, Optional<Integer>>[] createRules() {
        final Function<Map<String, Object>, Optional<Integer>>[] rules = new Function[5];
        int index = 0;

        rules[index++] = msg -> (msg.containsKey(SPECIAL)) ? Optional.of(0) : Optional.empty();

        rules[index++] = msg -> (msg.containsKey(HASH_GENERATED)) ? Optional.of(1) : Optional.empty();

        rules[index++] = msg -> {
            final boolean hasMagicString = msg.entrySet().stream().
                filter(e -> !e.getKey().startsWith(PRIVATE_PREFIX)).
                anyMatch(e -> {
                    if (e.getValue() instanceof String) {
                        final String value = (String) e.getValue();
                        return value.contains(MAGIC_STRING);
                    }

                    return false;
                });

            return hasMagicString ? Optional.of(2) : Optional.empty();
        };

        rules[index++] = msg -> {
            final boolean hasIntValue = msg.entrySet().stream().
                filter(e -> !e.getKey().startsWith(PRIVATE_PREFIX)).
                anyMatch(e -> (e.getValue() instanceof Long));

            return hasIntValue ? Optional.of(3) : Optional.empty();
        };

        rules[index] = msg -> Optional.of(4);

        return rules;
    }
}
