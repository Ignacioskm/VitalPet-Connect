package com.vitalpet.msbranches;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsBranchesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsBranchesApplication.class, args);
    }
}
