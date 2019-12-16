package me.jubyvictor.akkaapi;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.dispatch.MessageDispatcher;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AkkaServer extends AllDirectives {


    private static final Logger LOG = LoggerFactory.getLogger(AkkaServer.class);


    private static MessageDispatcher dispatcher;
    private static ObjectMapper mapper;

    public static void main(String[] args) {
        // boot up server using the route as defined below
        ActorSystem system = ActorSystem.create("routes");
        dispatcher = system.dispatchers().lookup("my-blocking-dispatcher");
        mapper = new ObjectMapper();

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


    private String updateUser(final User user) {
        user.setUpdatedAt(System.currentTimeMillis());
        try {
            Thread.sleep(25);
            return mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private Route createRoute() {
        return concat(
                get(() -> complete("Hello from Akka")),
                post(() -> path("up", () -> entity(Jackson.unmarshaller(User.class), usr -> completeWithFuture(CompletableFuture.supplyAsync(() -> {
                            String updateUser = updateUser(usr);
                            return HttpResponse.create().withEntity(updateUser);
                        }, dispatcher))
                )))
        );
    }
}
