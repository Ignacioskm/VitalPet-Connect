package com.vitalpet.msclinical.controller;

import com.vitalpet.msclinical.dto.ClinicalRecordRequestDTO;
import com.vitalpet.msclinical.dto.ClinicalRecordResponseDTO;
import com.vitalpet.msclinical.service.ClinicalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinical")
public class ClinicalController {

    @Autowired
    private ClinicalService clinicalService;


    //Crear nueva ficha con receta
    @PostMapping
    public ResponseEntity<ClinicalRecordResponseDTO> createRecord(@RequestBody @Valid ClinicalRecordRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(clinicalService.create(dto));
    }

    //Obtener el historial completo de una mascota
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<ClinicalRecordResponseDTO>> getPetHistory(@PathVariable Long petId){
        return ResponseEntity.ok(clinicalService.getHistoryByPet(petId));
    }
}
