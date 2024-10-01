package com.github.dougmab.openvinylboxapi.controller;

import com.github.dougmab.openvinylboxapi.dto.DiscountDTO;
import com.github.dougmab.openvinylboxapi.dto.ProductDTO;
import com.github.dougmab.openvinylboxapi.dto.RatingDTO;
import com.github.dougmab.openvinylboxapi.dto.UserRatingDTO;
import com.github.dougmab.openvinylboxapi.payload.ApiResponse;
import com.github.dougmab.openvinylboxapi.service.ProductService;
import com.github.dougmab.openvinylboxapi.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService service;
    private final RatingService ratingService;

    public ProductController(ProductService service, RatingService ratingService) {
        this.service = service;
        this.ratingService = ratingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> findAll(Pageable pageable) {
        Page<ProductDTO> list = service.findAllPaged(pageable);

        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> findById(@PathVariable Long id) {
        ProductDTO dto = service.findById(id);

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> insert(@RequestBody @Valid ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(ApiResponse.ok(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> update(@PathVariable Long id, @RequestBody @Valid ProductDTO newDto) {
        newDto = service.update(id, newDto);

        return ResponseEntity.ok(ApiResponse.ok(newDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    // DISCOUNT RELATED ENDPOINTS

    @PostMapping("{id}/discount")
    public ResponseEntity<ApiResponse<ProductDTO>> createDiscount(@PathVariable Long id, @RequestBody @Valid DiscountDTO discountDto) {
        ProductDTO productDto= service.createDiscountForProductId(id, discountDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/product/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(uri).body(ApiResponse.ok(productDto));
    }

    @DeleteMapping("/{id}/discount")
    public ResponseEntity<ProductDTO> deleteDiscount(@PathVariable Long id) {
        service.deleteDiscountForProductId(id);

        return ResponseEntity.noContent().build();
    }

    // RATING RELATED ENDPOINTS
    @GetMapping("{id}/rating")
    public ResponseEntity<ApiResponse<Page<UserRatingDTO>>> findRatingsByProductId(@PathVariable Long id, Pageable pageable) {
        Page<UserRatingDTO> list = ratingService.findAllUserRatingsOfProductId(id, pageable);

        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("{productId}/rating/{userId}")
    public ResponseEntity<ApiResponse<UserRatingDTO>> findRatingsByProductId(@PathVariable Long productId, @PathVariable Long userId) {
        UserRatingDTO dto = ratingService.findRatingOfProductIdAndUserId(productId, userId);

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PostMapping("{id}/rating")
    public ResponseEntity<ProductDTO> rateProduct(@PathVariable Long id, JwtAuthenticationToken jwt, @RequestBody @Valid RatingDTO ratingDto) {
        Long userId = Long.parseLong(jwt.getName());
        ratingService.addRating(id, userId, ratingDto);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/rating")
    public ResponseEntity<ProductDTO> UpdateProductRating(@PathVariable Long id, JwtAuthenticationToken jwt, @RequestBody @Valid RatingDTO ratingDto) {
        Long userId = Long.parseLong(jwt.getName());
        ratingService.updateRating(id, userId, ratingDto);

        return ResponseEntity.noContent().build();
    }
}
