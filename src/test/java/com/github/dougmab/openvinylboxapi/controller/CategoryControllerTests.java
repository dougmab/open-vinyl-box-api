package com.github.dougmab.openvinylboxapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dougmab.openvinylboxapi.config.SecurityConfig;
import com.github.dougmab.openvinylboxapi.dto.CategoryDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
public class CategoryControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private String userToken;

    @MockBean
    private CategoryService service;

    private CategoryDTO categoryDTO;
    private Page<CategoryDTO> page;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        categoryDTO = EntityFactory.createCategoryDTO(1L);
        page = new PageImpl<>(List.of(categoryDTO));

        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 5L;

        adminToken = EntityFactory.createTokenDTO(EntityFactory.createAdmin(1L)).getAccessToken();
        userToken = EntityFactory.createTokenDTO(EntityFactory.createUser(2L)).getAccessToken();

        when(service.findAllPaged(any(Pageable.class))).thenReturn(page);

        when(service.findById(existingId)).thenReturn(categoryDTO);
        when(service.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(service.insert(any(CategoryDTO.class))).thenReturn(categoryDTO);

        when(service.update(eq(existingId), any(CategoryDTO.class))).thenReturn(categoryDTO);
        when(service.update(eq(nonExistingId), any(CategoryDTO.class))).thenThrow(EntityNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(DataIntegrityViolationException.class).when(service).delete(dependentId);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content[0].id").isNumber(),
                        jsonPath("$.data.content[0].name").isString(),
                        jsonPath("$.data.page.totalElements").isNumber(),
                        jsonPath("$.data.page.totalPages").isNumber()
                );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/category/{id}", existingId).accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.name").isString()
                );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void findByIdShouldReturn404WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/category/{id}", nonExistingId).accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void insertShouldReturnCategoryDTOWhenIdIsNull() throws Exception {
        categoryDTO.setId(null);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);
        categoryDTO.setId(existingId);

        mockMvc.perform(post("/category")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.name").isString()
                );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void updateShouldReturnCategoryDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/category/{id}", existingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.name").isString()
                );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void updateShouldReturn404WhenIdDoesNotExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/category/{id}", nonExistingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteShouldReturn204WhenIdExists() throws Exception {
        mockMvc.perform(delete("/category/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteShouldReturn409WhenIdIsDependent() throws Exception {
        mockMvc.perform(delete("/category/{id}", dependentId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.data.status").value(409));
    }
}
