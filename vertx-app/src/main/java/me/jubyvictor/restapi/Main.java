package me.jubyvictor.restapi;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {


    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Vertx vertx;
    private Router router;
    private HttpServer server;
    private JsonProcessingHandler jsonProcessingHandler;
    private EventBus eventBus;




    private static final String ROOT = "/";
    private static final String UPDATE = "/up";

    private static final int PORT = 8080;


    public Main() {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        server = vertx.createHttpServer();
        router = Router.router(vertx);
        jsonProcessingHandler = new JsonProcessingHandler();
    }


    private void start() {

        DeploymentOptions options = new DeploymentOptions()
                .setWorker(true)
                //.setInstances(Runtime.getRuntime().availableProcessors())
                .setInstances(4)
                .setWorkerPoolName("the-specific-pool")
                .setWorkerPoolSize(4);;

        vertx.deployVerticle(()-> new JsonProcessingVerticle(), options, res->{
                if (res.succeeded()) {
                    System.out.println("Deployment id is: " + res.result());
                } else {
                    System.out.println("Deployment failed!");
                }
        });


        //root
        router.get(ROOT).produces("application/json").handler(ctx -> {
            ctx.response().end("{}");
        });
        //update
        router.get(UPDATE).produces("application/json").method(HttpMethod.POST)
                .handler(BodyHandler.create())
                .handler((routingContext)->{
                    LOG.info("Got request");
                    eventBus.request("tx_json",  routingContext.getBodyAsJson(), resp ->{
                        routingContext.response().setStatusCode(200).end((String)resp.result().body());
                    });
                });
                //.blockingHandler(jsonProcessingHandler);


        server.requestHandler(router).listen(PORT, httpServerAsyncResult -> {
            if (httpServerAsyncResult.succeeded()) {
                LOG.info("Started HTTP server on {} ", PORT);
            } else {
                LOG.error("Could not start HTTP server on {} ", PORT);
            }
        });
    }



    public static void main(String[] args) {
        LOG.info("Starting http API server!");
        Main self = new Main();
        self.start();
    }

}
