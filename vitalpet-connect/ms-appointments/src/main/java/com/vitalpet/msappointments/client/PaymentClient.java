package com.vitalpet.msappointments.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ms-payments")
public interface PaymentClient {

    //Necesito que se termine el post de payments.
}
