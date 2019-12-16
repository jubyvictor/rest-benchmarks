package me.jubyvictor.restapi;

import io.vertx.core.Vertx;
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


    private static final String ROOT = "/";
    private static final String UPDATE = "/up";

    private static final int PORT = 8080;


    public Main() {
        vertx = Vertx.vertx();
        server = vertx.createHttpServer();
        router = Router.router(vertx);
        jsonProcessingHandler = new JsonProcessingHandler();
    }


    private void start() {
        //root
        router.get(ROOT).produces("application/json").handler(ctx -> {
            ctx.response().end("{}");
        });
        //update
        router.get(UPDATE).produces("application/json").method(HttpMethod.POST)
                .handler(BodyHandler.create())
                .blockingHandler(jsonProcessingHandler);


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
