package com.vitalpet.msappointments.client;

import com.vitalpet.msappointments.dto.PaymentRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-payments")
public interface PaymentClient {

    //Necesito que se termine el post de payments.
    @PostMapping("/api/payments")
    void createPayment(@RequestBody PaymentRequestDTO request);
}
