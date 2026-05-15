package com.vitalpet.mspayments.repository;

import com.vitalpet.mspayments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    List<Payment> findByUserIdAndStatusName(Long id, String statusName);

}
