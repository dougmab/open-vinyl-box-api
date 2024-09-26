package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.RatingDTO;
import com.github.dougmab.openvinylboxapi.dto.UserRatingDTO;
import com.github.dougmab.openvinylboxapi.entity.Product;
import com.github.dougmab.openvinylboxapi.entity.Rating;
import com.github.dougmab.openvinylboxapi.entity.RatingStatistics;
import com.github.dougmab.openvinylboxapi.exception.ExceptionFactory;
import com.github.dougmab.openvinylboxapi.repository.ProductRepository;
import com.github.dougmab.openvinylboxapi.repository.RatingRepository;
import com.github.dougmab.openvinylboxapi.repository.RatingStatisticsRepository;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {

    private final RatingRepository repository;
    private final ProductRepository productRepository;
    private final RatingStatisticsRepository statisticsRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository repository, ProductRepository productRepository, RatingStatisticsRepository statisticsRepository, UserRepository userRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.statisticsRepository = statisticsRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<UserRatingDTO> findUserRatingsOfProductId(Long productId, Pageable pageable) {
        return repository.findRatingsByProductId(productId, pageable);
    }

    @Transactional
    public void addRating(Long productId, Long userId, RatingDTO ratingDTO) {
        if (repository.existsByProductIdAndUserId(productId, userId))
            throw new EntityExistsException("Rating already exists");

        Rating rating = new Rating(ratingDTO);
        Product product = productRepository.findById(productId).orElseThrow(() -> ExceptionFactory.entityNotFound(Product.class, productId));
        rating.setProduct(product);
        rating.setUser(userRepository.getReferenceById(userId));

        repository.save(rating);

        RatingStatistics ratingStatistics = product.getRatingStatistics();
        if (ratingStatistics == null) {
            ratingStatistics = new RatingStatistics(product);
        }

        ratingStatistics.addRating(rating);

        statisticsRepository.save(ratingStatistics);
    }

    @Transactional
    public void updateRating(Long productId, Long userId, RatingDTO ratingDTO) {
        Rating rating = repository.findByProductIdAndUserId(productId, userId).orElseThrow(() -> new EntityNotFoundException("Rating not found"));
        Short oldRatingValue = rating.getRatingValue();
        rating.setRatingValue(ratingDTO.getValue());
        rating.setComment(ratingDTO.getComment());

        repository.save(rating);

        RatingStatistics statistics = statisticsRepository.findByProductId(productId).orElseThrow(() -> ExceptionFactory.entityNotFound(RatingStatistics.class, productId));
        statistics.updateRating(oldRatingValue, ratingDTO.getValue());

        statisticsRepository.save(statistics);
    }
}
