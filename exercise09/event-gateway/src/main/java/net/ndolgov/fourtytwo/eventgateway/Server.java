package net.ndolgov.fourtytwo.eventgateway;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import net.ndolgov.fourtytwo.eventgateway.service.Kafka;
import net.ndolgov.fourtytwo.eventgateway.service.KafkaEtlService;
import net.ndolgov.fourtytwo.eventgateway.web.HttpEndpoint;

public final class Server extends AllDirectives {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("EventGateway");

        final ActorMaterializer materializer = ActorMaterializer.create(system);

        Http.get(system)
            .bindAndHandle(
                new HttpEndpoint(
                    new KafkaEtlService("iot_device_events", Kafka.createProducer("localhost:9092")))
                .routes().flow(system, materializer),
                ConnectHttp.toHost("localhost", 8090),
                materializer)
            .whenComplete((binding, e) -> {
                if (e == null) {
                    System.out.println("Server online at http://localhost:8090/");
                } else {
                    System.err.println("Server failed to start");
                    e.printStackTrace(System.err);
                    system.terminate();
                    System.exit(-1);
                }
            });
    }
}


