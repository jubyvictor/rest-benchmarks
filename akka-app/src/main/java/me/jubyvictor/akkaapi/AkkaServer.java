package me.jubyvictor.akkaapi;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AkkaServer extends AllDirectives {


    private static final Logger LOG = LoggerFactory.getLogger(AkkaServer.class);


    public static void main(String[] args) {
        // boot up server using the route as defined below
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        AkkaServer app = new AkkaServer();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("0.0.0.0", 8080), materializer);

        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        try {
            System.in.read(); // let it run until user presses return
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done

    }


    private CompletionStage<User> updateUser(final User user) {
        user.setUpdatedAt(System.currentTimeMillis());
        return CompletableFuture.completedFuture(user);
    }


    private Route createRoute() {
        return
                concat(
                    post(() ->
                            path("up", () -> {
                                        long start = System.currentTimeMillis();
                                        return entity(Jackson.unmarshaller(User.class), user -> {
                                                    CompletionStage<User> futureUpdated = updateUser(user);
                                                    return onSuccess(futureUpdated, updated -> {
                                                                long end = System.currentTimeMillis();
                                                                LOG.info("Service took {}", (end-start));
                                                                return completeOK(updated, Jackson.marshaller());
                                                            }
                                                    );
                                                }
                                        );
                                    }
                            )
                    )
                );

    }
}
