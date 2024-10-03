package com.github.dougmab.openvinylboxapi.repository;

import com.github.dougmab.openvinylboxapi.entity.RatingStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingStatisticsRepository extends JpaRepository<RatingStatistics, Long> {

    Optional<RatingStatistics> findByProductId(Long id);
}
