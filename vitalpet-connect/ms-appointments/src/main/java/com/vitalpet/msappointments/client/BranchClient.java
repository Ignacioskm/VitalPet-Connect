package com.vitalpet.msappointments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-branches")
public interface BranchClient {
    @GetMapping("/api/branches/{id}/exists")
    Boolean existsById(@PathVariable Long id);
}
