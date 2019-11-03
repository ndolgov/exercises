package net.ndolgov.fourtytwo.loadgenerator;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public final class KafkaEventSender implements EventSender {
    private final String topic;

    private final Producer<String, String> producer;

    public KafkaEventSender(String topic, Producer<String, String> producer) {
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public void sendMessage(int deviceId, int metricId, long timestamp, double value) {
        try {
            final String key = "msg-" + deviceId + "-" + timestamp;
            final String json = toJsonEvent(deviceId, metricId, timestamp, value);
            final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, json);
            producer.send(record).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send event", e);
        }
    }
}
