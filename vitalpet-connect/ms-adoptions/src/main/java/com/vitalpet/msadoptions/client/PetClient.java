package com.vitalpet.msadoptions.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-pets")
public interface PetClient {

    @GetMapping("/api/pets/{id}/exists")
    Boolean existById(@PathVariable Long id);

    @GetMapping("/api/pets/available")
    List<Object> getAvailablePets();

}
