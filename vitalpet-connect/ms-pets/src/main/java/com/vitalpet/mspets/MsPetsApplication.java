package com.vitalpet.mspets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsPetsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsPetsApplication.class, args);
    }
}
