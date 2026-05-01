package com.vitalpet.msbranches.controller;


import com.vitalpet.msbranches.dto.BranchesRequestDTO;
import com.vitalpet.msbranches.dto.BranchesResponseDTO;
import com.vitalpet.msbranches.service.ServiceBranches;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class ControllerBranches {

    @Autowired
    private ServiceBranches serviceBranches;

    @GetMapping
    public ResponseEntity<List<BranchesResponseDTO>> getAll() {
        return ResponseEntity.ok(serviceBranches.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchesResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceBranches.getById(id));
    }

    @PostMapping
    public ResponseEntity<BranchesResponseDTO> create(@RequestBody BranchesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceBranches.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchesResponseDTO> update(@PathVariable Long id, @RequestBody BranchesRequestDTO dto) {
        return ResponseEntity.ok(serviceBranches.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceBranches.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> branchExistsById(@PathVariable Long id){
        return ResponseEntity.ok(serviceBranches.branchExistsById(id));
    }

    //Listar sucursales por ciudad
    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<BranchesResponseDTO>> branchByCity(@Valid @PathVariable Long cityId){
        return ResponseEntity.ok(serviceBranches.findByCityId(cityId));
    }
}
