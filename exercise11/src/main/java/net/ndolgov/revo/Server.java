package net.ndolgov.revo;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import net.ndolgov.revo.service.AccountServiceImpl;
import net.ndolgov.revo.web.HttpEndpoint;

public final class Server extends AllDirectives {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("ChargingSessionServer");

        final ActorMaterializer materializer = ActorMaterializer.create(system);

        Http.get(system)
            .bindAndHandle(
                new HttpEndpoint(
                    new AccountServiceImpl(),
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


