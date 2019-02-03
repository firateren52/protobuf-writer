package com.eren.noddus.protobufwriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.eren.noddus.protobufwriter"})
@EnableScheduling
@EnableAsync
public class ProtobufWriterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProtobufWriterApplication.class, args);
    }

}