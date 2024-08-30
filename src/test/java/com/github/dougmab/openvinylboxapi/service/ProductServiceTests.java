package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.ProductDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.entity.Product;
import com.github.dougmab.openvinylboxapi.repository.ProductRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    PageImpl<Product> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 5L;
        page = new PageImpl<>(List.of(EntityFactory.createProduct(1L),
                EntityFactory.createProduct(2L),
                EntityFactory.createProduct(3L)));

        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(repository.findById(existingId)).thenReturn(Optional.of(EntityFactory.createProduct(existingId)));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.getReferenceById(existingId)).thenReturn(EntityFactory.createProduct(existingId));

        var nonExistingProductRef = new Product();
        nonExistingProductRef.setId(nonExistingId);

        when(repository.getReferenceById(nonExistingId)).thenReturn(nonExistingProductRef);

        when(repository.save(any(Product.class))).thenReturn(EntityFactory.createProduct(existingId));
        when(repository.save((nonExistingProductRef))).thenThrow(EntityNotFoundException.class);

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
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        var dto = service.findById(existingId);

        assertThat(dto).isNotNull();

        verify(repository, times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.findById(nonExistingId));

        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void insertShouldReturnProductDTO() {
        var dto = service.insert(new ProductDTO(EntityFactory.createProduct(null)));

        assertThat(dto).isNotNull();

        verify(repository, times(1)).save(any());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        var dto = service.update(existingId, new ProductDTO(EntityFactory.createProduct(existingId)));

        assertThat(dto).isNotNull();

        verify(repository, times(1)).getReferenceById(existingId);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    public void updateShouldThrowExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.update(nonExistingId, new ProductDTO(EntityFactory.createProduct(null))));

        verify(repository, times(1)).getReferenceById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        assertThatCode(() -> service.delete(existingId)).doesNotThrowAnyException();

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
