package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.ProductDTO;
import com.github.dougmab.openvinylboxapi.entity.Product;
import com.github.dougmab.openvinylboxapi.exception.ExceptionFactory;
import com.github.dougmab.openvinylboxapi.repository.CategoryRepository;
import com.github.dougmab.openvinylboxapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductService(@Autowired ProductRepository repository,
                          @Autowired CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<Product> list = repository.findAll(pageable);
        return list.map((product) -> new ProductDTO(product, product.getCategories()));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = repository.findById(id).orElseThrow(() -> ExceptionFactory.entityNotFound(Product.class, id));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product product = new Product(dto);

        dto.getCategories().forEach(category -> {
            product.getCategories().add(categoryRepository.getReferenceById(category.getId()));
        });

        Product entity = repository.save(product);

        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            entity.setPrice(dto.getPrice());
            entity.setImgUrl(dto.getImgUrl());
            entity.setDate(dto.getDate());

            entity.getCategories().clear();

            Product finalEntity = entity;
            dto.getCategories().forEach(category -> {
                finalEntity.getCategories().add(categoryRepository.getReferenceById(category.getId()));
            });

            entity = repository.save(entity);

            return new ProductDTO(entity, entity.getCategories());
        } catch (EntityNotFoundException e) {
            throw ExceptionFactory.entityNotFound(Product.class, id);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw ExceptionFactory.dataIntegrityViolationForeignKey(Product.class);
        }
    }
}
