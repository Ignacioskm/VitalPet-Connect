package com.vitalpet.mspayments.repository;

import com.vitalpet.mspayments.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long> {
    Optional<PaymentMethod> findByName(String name);
}
