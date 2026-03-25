package com.vitalpet.mspets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsPetsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsPetsApplication.class, args);
    }
}
