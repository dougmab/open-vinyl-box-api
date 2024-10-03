package com.github.dougmab.openvinylboxapi.repository;

import com.github.dougmab.openvinylboxapi.dto.UserRatingDTO;
import com.github.dougmab.openvinylboxapi.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByProductIdAndUserId(Long productId, Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    @Query("SELECT new com.github.dougmab.openvinylboxapi.dto.UserRatingDTO(u.id, u.firstName, u.lastName, r.ratingValue, r.comment, r.createdAt) FROM Rating r JOIN r.user u WHERE r.product.id = :productId AND r.user.id = :userId")
    Optional<UserRatingDTO> findUserRatingByProductIdAndUserId(Long productId, Long userId);

    @Query("SELECT new com.github.dougmab.openvinylboxapi.dto.UserRatingDTO(u.id, u.firstName, u.lastName, r.ratingValue, r.comment, r.createdAt) FROM Rating r JOIN r.user u WHERE r.product.id = :productId")
    Page<UserRatingDTO> findRatingsByProductId(Long productId, Pageable pageable);
}
