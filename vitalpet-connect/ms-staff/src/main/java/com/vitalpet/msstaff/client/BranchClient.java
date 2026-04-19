package com.vitalpet.msstaff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-branches") // <- Gracias a eureka no ponemos el localhost... sino que el nombre del ms
public interface BranchClient {

    @GetMapping("/api/branches/{id}/exists")
    Boolean existsById(@PathVariable Long id);
}
