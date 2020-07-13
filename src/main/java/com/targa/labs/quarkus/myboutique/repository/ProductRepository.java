package com.targa.labs.quarkus.myboutique.repository;

import com.targa.labs.quarkus.myboutique.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
