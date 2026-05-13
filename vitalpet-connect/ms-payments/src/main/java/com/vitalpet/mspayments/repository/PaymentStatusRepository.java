package com.vitalpet.mspayments.repository;

import com.vitalpet.mspayments.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus,Long> {
    Optional<PaymentStatus> findByName(String name);
}
