package com.vitalpet.mspets.owner;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-users")
public interface UserOwner {

    @GetMapping("/api/users/{id}/exists")
    Boolean existByid(@PathVariable Long id);
}
