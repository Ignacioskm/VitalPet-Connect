package com.vitalpet.msadoptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsAdoptionsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsAdoptionsApplication.class, args);
    }
}
