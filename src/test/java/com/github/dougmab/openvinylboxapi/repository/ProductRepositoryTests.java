package com.github.dougmab.openvinylboxapi.repository;

import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTests {

    private final ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;

    @Autowired
    public ProductRepositoryTests(ProductRepository repository) {
        this.repository = repository;
    }

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Product product = repository.findById(existingId).orElse(null);

        assertThat(product).isNotNull();
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Product product = repository.findById(nonExistingId).orElse(null);

        assertThat(product).isNull();
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = EntityFactory.createProduct(null);

        product = repository.save(product);

        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo("Thriller");
        assertThat(product.getPrice()).isEqualTo(9.99);
        assertThat(product.getImgUrl()).isEqualTo("https://picsum.photos/200");
    }

    @Test
    public void updateShouldUpdateObjectWhenIdExists() {
        Product product = EntityFactory.createProduct(existingId);

        product = repository.save(product);

        assertThat(product.getId()).isEqualTo(existingId);
        assertThat(product.getName()).isEqualTo("Thriller");
        assertThat(product.getPrice()).isEqualTo(9.99);
        assertThat(product.getImgUrl()).isEqualTo("https://picsum.photos/200");
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        Long existingId = 1L;

        repository.deleteById(existingId);

        assertThat(repository.findById(existingId)).isEmpty();
    }
}
