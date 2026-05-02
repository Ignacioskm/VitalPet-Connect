package com.vitalpet.msnotifications.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-users")
public interface UserClient {
    @GetMapping("/api/user/{id}/email")
    String getEmailById(@PathVariable Long id);
}
