package net.ndolgov.exercise;

import org.json.simple.JSONObject;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * Message broker Facade responsible for wiring together configurable parts.
 *
 * Assumptions:
 * - no volume/latency constraints => emphasis on using standard Collection classes and APIs
 * - no concurrency constraints => the crudest means of making it thread safe
 * - no message part arrival order constraints => parts can be re-ordered but only in the absence of reads
 * - no sequence and delimiter => no more parts are expected after the consumer reads all the parts received so far
 * - dispatching the first part only => the first sequence part must be enqueued first
 */
public final class Queues implements CodingChallenge {
    private final MessageQueue[] queues;

    private final Function<String, JSONObject> parser;

    private final Function<JSONObject, JSONObject> transformer;

    private final Function<JSONObject, Void> dispatcher;

    public Queues() {
        queues = new MessageQueue[5];
        for (int i = 0; i < queues.length; i++) {
            queues[i] = new MessageQueue(new LinkedBlockingQueue<>());
        }

        parser = new Parser();
        transformer = new Transformer();
        dispatcher = new Dispatcher(queues);
    }

    @Override
    public void enqueue(String message) throws QueueException {
        if ((message == null) || (message.length() == 0)) {
            throw new QueueException("Sequence body is missing");
        }

        synchronized (this) {
            try {
                parser.andThen(transformer).andThen(dispatcher).apply(message);
            } catch (Exception e) {
                throw new QueueException("Failed to enqueue message because of: " + e.getMessage());
            }
        }
    }

    @Override
    public String next(int qNumber) throws QueueException {
        if ((qNumber < 0) || (qNumber > 4)) {
            throw new QueueException("Illegal queue number: " + qNumber);
        }

        synchronized (this) {
            try {
                return queues[qNumber].dequeue().orElseThrow(() -> new QueueException("Queue is empty: " + qNumber));
            } catch (Exception e) {
                throw new QueueException("Failed to dequeue message because of: " + e.getMessage());
            }
        }
    }
}
