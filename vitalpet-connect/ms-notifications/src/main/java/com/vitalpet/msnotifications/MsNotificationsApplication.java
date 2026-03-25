package com.vitalpet.msnotifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsNotificationsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsNotificationsApplication.class, args);
    }
}
