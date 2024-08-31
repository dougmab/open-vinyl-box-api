package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.ProductDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 11L;
        productDTO = EntityFactory.createProductDTO(existingId);
        productDTO.getCategories().add(EntityFactory.createCategoryDTO(1L));
    }

    @Test
    public void findAllPagedShouldReturnPageOfPage0Size10() {
        Pageable pageable = PageRequest.of(0, 10);
        var page = service.findAllPaged(pageable);

        assertThat(page).isNotNull();
        assertThat(page.toList()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(countTotalProducts);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        var productDTO = service.findById(existingId);

        assertThat(productDTO).isNotNull();
        assertThat(productDTO.getId()).isEqualTo(repository.findById(existingId).get().getId());
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.findById(nonExistingId));
    }

    @Test
    public void insertShouldPersistWithAutoincrementWhenIdIsNull() {
        productDTO.setId(null);
        var dto = service.insert(productDTO);

        assertThat(dto.getId()).isEqualTo(countTotalProducts + 1);
        assertThat(dto.getName()).isEqualTo(productDTO.getName());
        assertThat(dto.getPrice()).isEqualTo(productDTO.getPrice());
        assertThat(dto.getImgUrl()).isEqualTo(productDTO.getImgUrl());
        assertThat(dto.getDate()).isEqualTo(productDTO.getDate());
        assertThat(dto.getCategories().get(0).getId()).isEqualTo(productDTO.getCategories().get(0).getId());
    }

    @Test
    public void updateShouldUpdateProductWhenIdExists() {
        var dto = service.update(existingId, productDTO);

        assertThat(dto.getId()).isEqualTo(existingId);
        assertThat(dto.getName()).isEqualTo(productDTO.getName());
        assertThat(dto.getPrice()).isEqualTo(productDTO.getPrice());
        assertThat(dto.getImgUrl()).isEqualTo(productDTO.getImgUrl());
        assertThat(dto.getDate()).isEqualTo(productDTO.getDate());
        assertThat(dto.getCategories().get(0).getId()).isEqualTo(productDTO.getCategories().get(0).getId());
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.update(nonExistingId, productDTO));
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);

        assertThat(repository.count()).isEqualTo(countTotalProducts - 1);
    }

    @Test
    public void deleteShouldDoNothingWhenIdDoesNotExist() {
        service.delete(nonExistingId);

        assertThat(repository.count()).isEqualTo(countTotalProducts);
    }
}
