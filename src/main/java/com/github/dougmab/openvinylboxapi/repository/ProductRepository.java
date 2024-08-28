package com.github.dougmab.openvinylboxapi.repository;

import com.github.dougmab.openvinylboxapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}
