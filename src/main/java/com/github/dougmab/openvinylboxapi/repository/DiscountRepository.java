package com.github.dougmab.openvinylboxapi.repository;

import com.github.dougmab.openvinylboxapi.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
