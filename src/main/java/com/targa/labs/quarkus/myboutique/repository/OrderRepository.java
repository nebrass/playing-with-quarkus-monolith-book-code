package com.targa.labs.quarkus.myboutique.repository;

import com.targa.labs.quarkus.myboutique.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCartCustomerId(Long customerId);

    Optional<Order> findByPaymentId(Long id);

    List<Order> findAllByPriceBetween(BigDecimal min, BigDecimal max);
}
