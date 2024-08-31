package com.github.dougmab.openvinylboxapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dougmab.openvinylboxapi.dto.CategoryDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerIntegrationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository repository;

    private long existingId;
    private long nonExistingId;
    private long independentId;
    private long countTotalCategories;

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
    public void findAllShouldReturnPageOfCategories() throws Exception {
        // By default, id is sorted in ascending order by the name. I'm changing that for test purposes
        mockMvc.perform(get("/category?size=10&sort=id")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.content").exists(),
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content[0].id").value(1),
                        jsonPath("$.data.content[0].name").value("Jazz"),
                        jsonPath("$.data.page.totalElements").value(countTotalCategories),
                        jsonPath("$.data.page.totalPages").value(2)
                );
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/category/{id}", existingId)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(1),
                        jsonPath("$.data.name").value("Jazz")
                );
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/category/{id}", nonExistingId)
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));

    }

    @Test
    public void insertShouldPersistProductWithAutoincrementWhenIdIsNull() throws Exception {
        categoryDTO.setId(null);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);
        mockMvc.perform(post("/category")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.data.id").value(countTotalCategories + 1),
                        jsonPath("$.data.name").value(categoryDTO.getName())
                );
    }

    @Test
    public void updateShouldReturnCategoryDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);
        mockMvc.perform(put("/category/{id}", existingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(existingId),
                        jsonPath("$.data.name").value(categoryDTO.getName())
                );
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);
        mockMvc.perform(put("/category/{id}", nonExistingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExistsAndIsNotAssociatedToForeignKey() throws Exception {
        mockMvc.perform(delete("/category/{id}", independentId)
                        .accept("application/json"))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isEqualTo(countTotalCategories - 1);
    }

    @Test
    public void deleteShouldDoNothingWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/category/{id}", nonExistingId)
                        .accept("application/json"))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isEqualTo(countTotalCategories);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteShouldThrowDataIntegrityViolationWhenAssociatedToForeignKey() throws Exception {
        mockMvc.perform(delete("/category/{id}", existingId)
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.status").value(400));
    }
}
