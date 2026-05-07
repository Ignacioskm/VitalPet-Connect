package com.vitalpet.msadoptions.client;

import com.vitalpet.msadoptions.dto.PetResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "ms-pets")
public interface PetClient {

    @GetMapping("/api/pets/{id}/exists")
    Boolean existById(@PathVariable Long id);

    @GetMapping("/api/pets/available")
    List<PetResponseDTO> getAvailablePets();

    @PutMapping("/api/pets/{petId}/owner/{userId}")
    Void updateOwner(@PathVariable Long petId, @PathVariable Long userId);
}
