package com.vitalpet.msclinical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsClinicalApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsClinicalApplication.class, args);
    }
}
