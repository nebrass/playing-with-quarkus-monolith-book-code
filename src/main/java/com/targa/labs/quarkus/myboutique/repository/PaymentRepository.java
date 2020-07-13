package com.targa.labs.quarkus.myboutique.repository;

import com.targa.labs.quarkus.myboutique.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsPaymentByOrderPriceBetween(BigDecimal min, BigDecimal max);
}
