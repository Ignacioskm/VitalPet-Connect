package com.vitalpet.msclinical.controller;

import com.vitalpet.msclinical.dto.ClinicalResponseDTO;
import com.vitalpet.msclinical.service.ClinicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClinicalController {
    @Autowired
    private ClinicalService clinicalService;


}
