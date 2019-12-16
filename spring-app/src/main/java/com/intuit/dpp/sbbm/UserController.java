package com.intuit.dpp.sbbm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private JsonProcessingService service;

    @PostMapping("/up")
    public CompletableFuture<String> update(@RequestBody User data){
        CompletableFuture<String> futureResult = service.updateUser(data);
        return  futureResult;
    }
}
