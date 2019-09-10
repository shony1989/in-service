package com.abnamro.care.event.in;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;



@SpringBootApplication
@EnableAutoConfiguration
@EnableBinding(Source.class)
public class CareEventInServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareEventInServiceApplication.class, args);
    }
}
