package com.targa.labs.quarkus.myboutique.repository;

import com.targa.labs.quarkus.myboutique.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
