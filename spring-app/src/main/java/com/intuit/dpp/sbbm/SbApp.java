package com.intuit.dpp.sbbm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SbApp {
    public static void main(String[] args) {
        SpringApplication.run(SbApp.class, args);
    }


}
