package net.ndolgov.exercise;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int PERIOD = 5 * 60 * 1_000;

    private static final String CONFIGURATION_PATH = "latest.cfg";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Server(String url) {
        this.scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    Poller.download(url, CONFIGURATION_PATH);
                    Handler.handle(Parser.parse(CONFIGURATION_PATH));
                } catch (Exception e) {
                    System.out.println("Polling cycle failed");
                    e.printStackTrace();
                }
            },
            100,
            PERIOD,
            TimeUnit.MILLISECONDS);
    }
}
