package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.CategoryDTO;
import com.github.dougmab.openvinylboxapi.entity.Category;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    PageImpl<Category> page;


    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 5L;
        page = new PageImpl<>(List.of(EntityFactory.createCategory(1L),
                EntityFactory.createCategory(2L),
                EntityFactory.createCategory(3L)));

        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(repository.findById(existingId)).thenReturn(Optional.of(EntityFactory.createCategory(existingId)));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.getReferenceById(existingId)).thenReturn(EntityFactory.createCategory(existingId));

        var nonExistingCategoryRef = new Category();
        nonExistingCategoryRef.setId(nonExistingId);

        when(repository.getReferenceById(nonExistingId)).thenReturn(nonExistingCategoryRef);

        when(repository.save(any(Category.class))).thenReturn(EntityFactory.createCategory(existingId));
        when(repository.save((nonExistingCategoryRef))).thenThrow(EntityNotFoundException.class);

        doNothing().when(repository).deleteById(existingId);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        var result = service.findAllPaged(Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenIdExists() {
        var dto = service.findById(existingId);

        assertThat(dto).isNotNull();

        verify(repository, times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.findById(nonExistingId));

        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void insertShouldReturnCategoryDTOWhenIdExists() {
        var dto = service.insert(new CategoryDTO(EntityFactory.createCategory(existingId)));

        assertThat(dto).isNotNull();

        verify(repository, times(1)).save(any(Category.class));
    }

    @Test
    public void updateShouldReturnCategoryDTOWhenIdExists() {
        var dto = service.update(existingId, new CategoryDTO(EntityFactory.createCategory(existingId)));

        assertThat(dto).isNotNull();

        verify(repository, times(1)).getReferenceById(existingId);
        verify(repository, times(1)).save(any(Category.class));
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.update(nonExistingId, new CategoryDTO(EntityFactory.createCategory(null))));

        verify(repository, times(1)).getReferenceById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        service.delete(existingId);

        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowDataIntegrityViolationWhenAssociatedToForeignKey() {
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> {
            service.delete(dependentId);
        });

        verify(repository, times(1)).deleteById(dependentId);
    }
}
