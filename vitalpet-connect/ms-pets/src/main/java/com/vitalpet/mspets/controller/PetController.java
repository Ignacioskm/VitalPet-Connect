package com.vitalpet.mspets.controller;


import com.vitalpet.mspets.dto.PetRequestDTO;
import com.vitalpet.mspets.dto.PetResponseDTO;
import com.vitalpet.mspets.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> getAll() {
        return ResponseEntity.ok(petService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> getById(@PathVariable Long id) {
     return ResponseEntity.ok(petService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PetResponseDTO> create(@RequestBody PetRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> update(@PathVariable Long id, @RequestBody PetRequestDTO dto) {
        return ResponseEntity.ok(petService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PetResponseDTO> delete(@PathVariable Long id) {
        petService.deactivate(id);
        return ResponseEntity.noContent().build();

    }

}
