package com.vitalpet.msadoptions.controller;

import com.vitalpet.msadoptions.dto.AdoptionRequestDTO;
import com.vitalpet.msadoptions.dto.AdoptionResponseDTO;
import com.vitalpet.msadoptions.dto.PetResponseDTO;
import com.vitalpet.msadoptions.service.AdoptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {

    @Autowired private AdoptionService adoptionService;

    @GetMapping
    public ResponseEntity<List<AdoptionResponseDTO>> getAll(){
        return ResponseEntity.ok(adoptionService.getAll());
    }

    @PostMapping
    public ResponseEntity<AdoptionResponseDTO> create(@Valid @RequestBody AdoptionRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(adoptionService.create(dto));
    }

    // Usamos RequestParam para simular qué trabajador está haciendo la acción
    @PutMapping("/{id}/approve")
    public ResponseEntity<AdoptionResponseDTO> approve(@PathVariable Long id, @RequestParam Long staffId){
        return ResponseEntity.ok(adoptionService.approve(id,staffId));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<AdoptionResponseDTO> reject(@PathVariable Long id, @RequestParam Long staffId){
        return ResponseEntity.ok(adoptionService.reject(id,staffId));
    }

    @GetMapping("/available/pets")
    public ResponseEntity<List<PetResponseDTO>> getAvailablePets(){
        return ResponseEntity.ok(adoptionService.getAvailablePets());
    }
}
