package com.transitflow.dispatch;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.transistflow.dispatch",              // your service code
        "com.transistflow.commans.configs"     // your shared KafkaConfig
})
public class DispatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(DispatchApplication.class, args);
    }
}
