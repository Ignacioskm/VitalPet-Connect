package com.vitalpet.mspayments.controller;

import com.vitalpet.mspayments.dto.PaymentRequestDTO;
import com.vitalpet.mspayments.dto.PaymentResponseDTO;
import com.vitalpet.mspayments.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAll(){
        return ResponseEntity.ok(paymentService.getAll());
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(paymentRequestDTO));
    }

    //Traer pendientes
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<PaymentResponseDTO>> getPendingUser(@PathVariable Long userId){
        return ResponseEntity.ok(paymentService.getPendingByUser(userId));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<PaymentResponseDTO> pay(@PathVariable Long id, @RequestParam String method){
        return ResponseEntity.ok(paymentService.pay(id,method));
    }

    @PutMapping("/{id}/refund")
    public ResponseEntity<PaymentResponseDTO> refund(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refund(id));
    }
}
