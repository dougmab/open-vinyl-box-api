package com.github.dougmab.openvinylboxapi.services;

import com.github.dougmab.openvinylboxapi.entities.Category;
import com.github.dougmab.openvinylboxapi.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(@Autowired CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> findAll() {
        return repository.findAll();
    }
}
