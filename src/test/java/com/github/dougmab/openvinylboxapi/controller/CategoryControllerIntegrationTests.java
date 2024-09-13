package com.github.dougmab.openvinylboxapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dougmab.openvinylboxapi.dto.CategoryDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.CategoryRepository;
import com.github.dougmab.openvinylboxapi.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CategoryControllerIntegrationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private TokenService tokenService;

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
    public void anyMethodShouldReturnUnauthorizedWhenNoAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/category")
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/category")
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/category")
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/category")
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("test@example.com")
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
    @WithUserDetails("test@example.com")
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
    @WithUserDetails("test@example.com")
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/category/{id}", nonExistingId)
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));

    }

    @Test
    @WithUserDetails("test@example.com")
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
    @WithUserDetails("test@example.com")
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
    @WithUserDetails("test@example.com")
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
    @WithUserDetails("test@example.com")
    public void deleteShouldDeleteResourceWhenIdExistsAndIsNotAssociatedToForeignKey() throws Exception {
        mockMvc.perform(delete("/category/{id}", independentId)
                        .accept("application/json"))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isEqualTo(countTotalCategories - 1);
    }

    @Test
    @WithUserDetails("test@example.com")
    public void deleteShouldDoNothingWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/category/{id}", nonExistingId)
                        .accept("application/json"))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isEqualTo(countTotalCategories);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails("test@example.com")
    public void deleteShouldThrowDataIntegrityViolationWhenAssociatedToForeignKey() throws Exception {
        mockMvc.perform(delete("/category/{id}", existingId)

                        .accept("application/json"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.data.status").value(409));
    }
}
