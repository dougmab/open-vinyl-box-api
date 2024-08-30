package com.github.dougmab.openvinylboxapi.entity;

import com.github.dougmab.openvinylboxapi.dto.CategoryDTO;
import com.github.dougmab.openvinylboxapi.dto.ProductDTO;

import java.time.Instant;

public class EntityFactory {

    public static Category createCategory(Long id) {
        return new Category(id, "Pop");
    }

    public static CategoryDTO createCategoryDTO(long id) {
        return new CategoryDTO(createCategory(id));
    }

    public static Product createProduct(Long id) {
        Product product = new Product(id, "Thriller", 9.99, "https://picsum.photos/200", Instant.parse("1982-11-29T10:00:00Z"));
        product.getCategories().add(createCategory(1L));
        return product;
    }

    public static ProductDTO createProductDTO(Long id) {
        return new ProductDTO(createProduct(id));
    }
}
