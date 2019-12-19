package com.intuit.dpp.sbbm;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class JsonProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(JsonProcessingService.class);
    private ObjectMapper mapper = new ObjectMapper();



    @Async
    public CompletableFuture<String> updateUser(User user) throws RuntimeException {
        long start = System.currentTimeMillis();
        String json;
        try {
            Thread.sleep(25);
            user.setUpdatedAt(System.currentTimeMillis());
            json = mapper.writeValueAsString(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long end = System.currentTimeMillis();
        LOG.info("Px took {}", (end-start));
        return CompletableFuture.completedFuture(json);
    }
}
