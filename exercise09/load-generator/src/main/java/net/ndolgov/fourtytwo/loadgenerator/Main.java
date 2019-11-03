package net.ndolgov.fourtytwo.loadgenerator;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Run multiple IoT device simulators to generate messages at an approximately fixed rate. Send the messages to a Kafka topic.
 */
public class Main {
    private static final int CORE_POOL_SIZE = 4;

    public static void main(String[] args) {
        final Cfg cfg = new Cfg(args);

        final Scheduler generator = new Scheduler(Executors.newScheduledThreadPool(CORE_POOL_SIZE), cfg.periodMs);

        createDevices(cfg, generator);

        waitForInterruptions(generator);
    }

    private static void createDevices(Cfg cfg, Scheduler generator) {
        //final Producer<String, String> producer = Kafka.createProducer(cfg.bootstrapServer);
        final CloseableHttpClient httpClient = createHttpClient();
        for (int i = 0; i < cfg.nDevices; i++) {
            generator.schedule(
                new IoTDevice(
                    500 + i, new int[] {10000 + i, 11000 + i, 12000 + i},
                    //new KafkaEventSender(cfg.topic, producer)));
                    new RestfulEventSender(httpClient, cfg.restfulUrl)));
        }
    }

    private static void waitForInterruptions(Scheduler generator) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                generator.shutDown();
                System.out.println("The load generator was interrupted, shutting down now ..");
                System.exit(0);
            }
        }
    }

    private static CloseableHttpClient createHttpClient() {
        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(60, TimeUnit.SECONDS);
        connManager.setDefaultMaxPerRoute(9);
        return HttpClients.createMinimal(connManager);
    }



    // TODO extract args
    private static final class Cfg {
        final int periodMs;

        final int nDevices;

        final String bootstrapServer;

        final String topic;

        final String restfulUrl;

        public Cfg(String[] args) {
            periodMs = 1_000;
            nDevices = 3;
            bootstrapServer = "localhost:9092";
            topic = "iot_device_events";
            restfulUrl = "http://localhost:8090/event";
        }
    }
}
