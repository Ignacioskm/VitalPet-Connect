package com.vitalpet.msclinical.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-staff")
public interface StaffClient {

    @GetMapping("/api/staff/{id}/exists")
    Boolean existsById(@PathVariable Long id);

}
