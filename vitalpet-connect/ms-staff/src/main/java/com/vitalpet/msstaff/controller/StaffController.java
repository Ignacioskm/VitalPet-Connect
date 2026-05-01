package com.vitalpet.msstaff.controller;

import com.vitalpet.msstaff.dto.StaffRequestDTO;
import com.vitalpet.msstaff.dto.StaffResponseDTO;
import com.vitalpet.msstaff.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping
    public ResponseEntity<List<StaffResponseDTO>> getAll(){
        return ResponseEntity.ok(staffService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(staffService.getById(id));
    }

    @PostMapping
    public ResponseEntity<StaffResponseDTO> create(@RequestBody StaffRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> update(@PathVariable Long id,@RequestBody StaffRequestDTO dto){
        return ResponseEntity.ok(staffService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        staffService.desactivate(id);
        return ResponseEntity.noContent().build();
    }

    //Listar staffs por sede
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<StaffResponseDTO>> findStaffByBranchId(@Valid @PathVariable Long branchId){
        return ResponseEntity.ok(staffService.findStaffByBranchId(branchId));
    }

    //Endpoint de consumo para verificar si existe el staff
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> staffExistsById(@PathVariable Long id){
        return ResponseEntity.ok(staffService.staffExistsById(id));
    }
}
