package com.vitalpet.mspets.controller;


import com.vitalpet.mspets.dto.PetRequestDTO;
import com.vitalpet.mspets.dto.PetResponseDTO;
import com.vitalpet.mspets.dto.SpeciesResponseDTO;
import com.vitalpet.mspets.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired private PetService petService;

    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> getAll() {
        return ResponseEntity.ok(petService.getAll());
    }

    @GetMapping("/species")
    public ResponseEntity<List<SpeciesResponseDTO>> getAllSpecies(){return ResponseEntity.ok(petService.getAllSpecies());}

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> getById(@PathVariable Long id) {
     return ResponseEntity.ok(petService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PetResponseDTO> create(@Valid @RequestBody PetRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PetRequestDTO dto) {
        return ResponseEntity.ok(petService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PetResponseDTO> delete(@PathVariable Long id) {
        petService.deactivate(id);
        return ResponseEntity.noContent().build();

    }

    //Listar mascotas por dueño
    @GetMapping("/{id}/owner")
    public ResponseEntity<List<PetResponseDTO>> getPetsByOwnerId(@PathVariable Long id){
        return ResponseEntity.ok(petService.getByOwnerId(id));
    }

    //Verificar si la mascota existe
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> petExists(@PathVariable Long id){
        return ResponseEntity.ok(petService.petExists(id));
    }

    //Verificar si la mascota sigue estando disponible para Adopción por medio del Owner ID 8si es nulo)
    @GetMapping("/available")
    public ResponseEntity<List<PetResponseDTO>> getAvailablePets() {
        return ResponseEntity.ok(petService.getAvailablePets());
    }

    @PutMapping("/{petId}/owner/{userId}")
    public ResponseEntity<Void> assignOwner(@PathVariable Long petId, @PathVariable Long userId){
        petService.assignOwner(petId,userId);
        return ResponseEntity.noContent().build();
    }
}
