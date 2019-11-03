package net.ndolgov.fourtytwo.eventgateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ndolgov.fourtytwo.eventgateway.domain.Event;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class KafkaEtlService implements EtlService {
    private final String topic;

    private final Producer<String, String> producer;

    private final AtomicLong eventCount = new AtomicLong(0);

    private final ObjectMapper mapper;

    public KafkaEtlService(String topic, Producer<String, String> producer) {
        this.topic = topic;
        this.producer = producer;
        this.mapper =  new ObjectMapper();
    }

    @Override
    public long eventCount() {
        return eventCount.get();
    }

    @Override
    public CompletableFuture<Void> sendMessage(Event event) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String key = "msg-" + System.currentTimeMillis();
                final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, mapper.writeValueAsString(event));
                producer.send(record).get();
                eventCount.incrementAndGet();

                return null;
            } catch (Exception e) {
                throw new RuntimeException("Failed to send event", e);
            }
        });

    }

    public CompletableFuture<Void> sendMessage(String json) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String key = "msg-" + System.currentTimeMillis();
                final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, json);
                producer.send(record).get();
                eventCount.incrementAndGet();

                return null;
            } catch (Exception e) {
                throw new RuntimeException("Failed to send event", e);
            }
        });
    }

}
