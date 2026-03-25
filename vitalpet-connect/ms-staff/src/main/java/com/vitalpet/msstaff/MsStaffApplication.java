package com.vitalpet.msstaff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsStaffApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsStaffApplication.class, args);
    }
}
