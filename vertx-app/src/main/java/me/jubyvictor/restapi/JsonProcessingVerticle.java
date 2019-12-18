package me.jubyvictor.restapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonProcessingVerticle extends AbstractVerticle {

    private EventBus eventBus;
    private static final Logger LOG = LoggerFactory.getLogger(JsonProcessingVerticle.class);

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.eventBus = vertx.eventBus();
    }

    @Override
    public void start() throws Exception {
        super.start();
        this.eventBus.consumer("tx_json", message -> {
            LOG.info("Got message");
            JsonObject payload = (JsonObject)message.body();
            User user = Json.decodeValue(payload.toBuffer(), User.class);
            user.setUpdatedAt(System.currentTimeMillis());
            message.reply(Json.encode(user));
            LOG.info("Sent response");
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }


}
