package com.targa.labs.quarkus.myboutique.repository;

import com.targa.labs.quarkus.myboutique.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
