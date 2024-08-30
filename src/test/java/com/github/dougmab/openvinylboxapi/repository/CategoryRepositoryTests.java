package com.github.dougmab.openvinylboxapi.repository;

import com.github.dougmab.openvinylboxapi.entity.Category;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class CategoryRepositoryTests {

    private final CategoryRepository repository;
    private final ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;

    @Autowired
    public CategoryRepositoryTests(CategoryRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Category category = EntityFactory.createCategory(null);

        category = repository.save(category);

        assertThat(category.getId()).isNotNull();
        assertThat(category.getName()).isEqualTo("Pop");
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Category category = repository.findById(existingId).orElse(null);

        assertThat(category).isNotNull();
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Category category = repository.findById(nonExistingId).orElse(null);

        assertThat(category).isNull();
    }

    @Test
    public void updateShouldUpdateObjectWhenIdExists() {
        Category category = EntityFactory.createCategory(existingId);

        category = repository.save(category);

        assertThat(category.getId()).isEqualTo(existingId);
        assertThat(category.getName()).isEqualTo("Pop");
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);

        Category category = repository.findById(existingId).orElse(null);

        assertThat(category).isNull();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteShouldThrowDataIntegrityViolationWhenAssociatedToForeignKey() {
        Category category = EntityFactory.createCategory(null);
        category.setName("Pop");

        category = repository.save(category);

        existingId = category.getId();

        Product product = EntityFactory.createProduct(null);

        productRepository.save(product);

        product.getCategories().clear();
        product.getCategories().add(category);

        product = productRepository.save(product);

        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> {
            repository.deleteById(existingId);
        });
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteShouldDeleteWhenForeignKeyIsAlsoDeleted() {
        Category category = EntityFactory.createCategory(null);
        category.setName("Pop");

        category = repository.save(category);

        existingId = category.getId();

        Product product = EntityFactory.createProduct(null);

        productRepository.save(product);

        product.getCategories().clear();
        product.getCategories().add(category);

        product = productRepository.save(product);

        productRepository.deleteById(product.getId());

        assertThatNoException().isThrownBy(() -> {
            repository.deleteById(existingId);
        });

        assertThat(repository.findById(existingId)).isEmpty();
    }
}
