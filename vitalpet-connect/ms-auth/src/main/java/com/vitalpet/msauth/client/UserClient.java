package com.vitalpet.msauth.client;

import com.vitalpet.msauth.dto.UserRequestDTO;
import com.vitalpet.msauth.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-users")
public interface UserClient {

    @PostMapping("/api/user")
    UserResponseDTO createUser(@RequestBody UserRequestDTO dto);

    @GetMapping("/api/user/{id}")
    UserResponseDTO getUserById(@PathVariable Long id);

}
