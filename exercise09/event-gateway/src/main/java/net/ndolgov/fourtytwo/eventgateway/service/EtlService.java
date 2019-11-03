package net.ndolgov.fourtytwo.eventgateway.service;

import net.ndolgov.fourtytwo.eventgateway.domain.Event;

import java.util.concurrent.CompletableFuture;

public interface EtlService {
    CompletableFuture<Void> sendMessage(String json);

    CompletableFuture<Void> sendMessage(Event event);

    long eventCount();
}
