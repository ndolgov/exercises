package net.ndolgov.evbox;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import net.ndolgov.evbox.domain.Statistics;
import net.ndolgov.evbox.service.ChargingSessionServiceImpl;
import net.ndolgov.evbox.web.HttpEndpoint;

import java.time.Clock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static net.ndolgov.evbox.service.Windows.tumblingWindow;

public final class Server extends AllDirectives {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("ChargingSessionServer");

        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Http.get(system)
            .bindAndHandle(
                new HttpEndpoint(
                    new ChargingSessionServiceImpl(
                        tumblingWindow(new Statistics(0, 0), 60, scheduler),
                        Clock.systemUTC()),
                    EntityStreamingSupport.json(),
                    Jackson.marshaller())
                        .routes().flow(system, materializer),
                ConnectHttp.toHost("localhost", 8080),
                materializer)
            .whenComplete((binding, e) -> {
                if (e == null) {
                    System.out.println("Server online at http://localhost:8080/");
                } else {
                    System.err.println("Server failed to start");
                    e.printStackTrace(System.err);
                    system.terminate();
                    System.exit(-1);
                }
            });
    }
}


