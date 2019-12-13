package me.jubyvictor.restapi;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonProcessingHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonProcessingHandler.class);
    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public void handle(RoutingContext routingContext) {
        long start = System.currentTimeMillis();
        String json;
        try {
            JsonObject payload = routingContext.getBodyAsJson();
            User user = Json.decodeValue(payload.toBuffer(), User.class);
            LOG.info(user.toString());
            user.setUpdatedAt(System.currentTimeMillis());
            json = mapper.writeValueAsString(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        routingContext.response().setStatusCode(200).end(json);
        long end = System.currentTimeMillis();
        LOG.info("Service took {}", (end-start));

    }
}
