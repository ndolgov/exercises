package net.ndolgov.exercise;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

/**
 * A queue allocating a single slot for both singular and multi-part messages. Additional parts can be appended to an
 * existing "multi-part slot" as long as it has not been cleaned up. The clean up is triggered by the first read access
 * that finds nor pending parts in the "multi-part slot".
 *
 * The queue has rudimentary support for out of order parts. Assuming no interleaving reads, out of order writes
 * (for parts other than the first one) will be inserted into a correct position in the sequence.
 *
 * Keep state for multi-part messages to (A) remember which queue to use for upcoming message parts (B) append a part
 * in O(1) of queue size.
 *
 * Queue and sequence state clean up is triggered lazily. If a dequeue call encounters an empty queue slot it performs
 * cleanup and retries with another slot.
 */
final class MessageQueue {
    private final Queue<QueuedMessage> queue;

    private final Map<String, Sequence> correlationIdToSequence;

    public MessageQueue(Queue<QueuedMessage> queue) {
        this.queue = queue;
        this.correlationIdToSequence = new HashMap<>(128);
    }

    public void enqueue(String msg, String correlationId, long partId) {
        if (partId == MessageKeys.FIRST_PART) {
            final Sequence sequence = new Sequence(msg, correlationId, partId);
            queue.add(sequence);
            correlationIdToSequence.put(correlationId, sequence);
        } else {
            final Sequence sequence = correlationIdToSequence.get(correlationId);
            if (sequence == null) {
                throw new IllegalArgumentException("No state found for sequence part: " + partId + " of sequence: " + correlationId);
            }

            sequence.append(msg, partId);
        }
    }

    public void enqueue(String msg) {
        queue.add(new Singular(msg));
    }

    public Optional<String> dequeue() {
        if (queue.isEmpty()) {
            return Optional.empty();
        }

        final QueuedMessage head = queue.peek();

        if (head.hasNext()) {
            return Optional.of(head.next());
        } else {
            final QueuedMessage removed = queue.remove(); // found an empty slot so clean up state and retry
            correlationIdToSequence.remove(removed.correlationId()); // no harm if a singular

            return dequeue();
        }
    }

    public boolean isSequenceKnown(String correlationId) {
        return correlationIdToSequence.containsKey(correlationId);
    }

    /**
     * Queue item type
     */
    private interface QueuedMessage extends Iterator<String> {
        String correlationId();
    }

    /**
     * Atomic message
     */
    private static final class Singular implements QueuedMessage {
        private String message;

        private Singular(String message) {
            this.message = message;
        }

        @Override
        public String correlationId() {
            return message;
        }

        @Override
        public boolean hasNext() {
            return message != null;
        }

        @Override
        public String next() {
            final String msg = this.message;
            this.message = null;
            return msg;
        }
    }

    /**
     * Multi-part message
     */
    private static final class Sequence implements QueuedMessage {
        private final String correlationId;

        private SequencePart head;

        public Sequence(String msg, String correlationId, long partId) {
            this.correlationId = correlationId;
            this.head = new SequencePart(msg, partId);
        }

        @Override
        public String correlationId() {
            return correlationId;
        }

        @Override
        public boolean hasNext() {
            return head != null;
        }

        @Override
        public String next() {
            final SequencePart next = head;
            head = head.next;
            return next.message;
        }

        public void append(String msg, long partId) {
            final SequencePart part = new SequencePart(msg, partId);

            if (head == null) {
                head = part;
            } else {
                final long parentIndex = part.index - 1; // rudimentary support for out of order parts

                SequencePart current = head;
                while ((current.index != parentIndex) && (current.next != null)) {
                    current = current.next;
                }
                part.next = current.next;
                current.next = part;
            }
        }

        /**
         * Linked list node representing a message part
         */
        private static final class SequencePart {
            final String message;
            final long index;
            SequencePart next;

            private SequencePart(String msg, long index) {
                this.message = msg;
                this.index = index;
                this.next = null;
            }
        }
    }
}
