package com.vitalpet.msappointments.client;

import com.vitalpet.msappointments.dto.PetResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pets")
public interface PetClient {
    @GetMapping("/api/pets/{id}/exists")
    Boolean existsById(@PathVariable Long id);

    @GetMapping("/api/pets/{id}")
    PetResponseDTO getPetById(@PathVariable Long id);
}
