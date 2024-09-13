package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.CategoryDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryServiceIntegrationTests {

    @Autowired
    private CategoryService service;

    @Autowired
    private CategoryRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalCategories;
    private Long independentId;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = 12L;
        independentId = 12L;
        categoryDTO = EntityFactory.createCategoryDTO(existingId);
    }

    @Test
    public void findAllPagedShouldReturnPageOfPage0Size10() {
        Pageable pageable = PageRequest.of(0, 10);
        var page = service.findAllPaged(pageable);

        assertThat(page).isNotNull();
        assertThat(page.toList()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(countTotalCategories);
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenIdExists() {
        var CategoryDTO = service.findById(existingId);

        assertThat(CategoryDTO).isNotNull();
        assertThat(CategoryDTO.getId()).isEqualTo(repository.findById(existingId).get().getId());
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.findById(nonExistingId));
    }

    @Test
    public void insertShouldPersistWithAutoincrementWhenIdIsNull() {
        categoryDTO.setId(null);
        var dto = service.insert(categoryDTO);

        assertThat(dto.getId()).isEqualTo(countTotalCategories + 1);
        assertThat(dto.getName()).isEqualTo(categoryDTO.getName());
    }

    @Test
    public void updateShouldUpdateCategoryWhenIdExists() {
        var dto = service.update(existingId, categoryDTO);

        assertThat(dto.getId()).isEqualTo(existingId);
        assertThat(dto.getName()).isEqualTo(categoryDTO.getName());
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.update(nonExistingId, categoryDTO));
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExistsAndIsNotAssociatedToForeignKey() {
        service.delete(independentId);

        assertThat(repository.count()).isEqualTo(countTotalCategories - 1);
    }

    @Test
    public void deleteShouldDoNothingWhenIdDoesNotExist() {
        service.delete(nonExistingId);

        assertThat(repository.count()).isEqualTo(countTotalCategories);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteShouldThrowDataIntegrityViolationWhenAssociatedToForeignKey() {
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> {
            service.delete(existingId);
        });
    }
}
