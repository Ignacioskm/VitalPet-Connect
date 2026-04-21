package com.vitalpet.mspets.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-users")
public interface UserClient {

    @GetMapping("/api/user/{id}/exists")
    Boolean existById(@PathVariable Long id);

    @GetMapping("/api/user/{id}/is-client")
    Boolean isClient(@PathVariable Long id);

}
