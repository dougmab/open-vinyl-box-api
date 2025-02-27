package com.github.dougmab.openvinylboxapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dougmab.openvinylboxapi.config.SecurityConfig;
import com.github.dougmab.openvinylboxapi.dto.ProductDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.service.ProductService;
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

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    private ProductDTO productDTO;
    private Page<ProductDTO> page;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        productDTO = EntityFactory.createProductDTO(1L);
        page = new PageImpl<>(List.of(productDTO));

        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 5L;

        when(service.findAllPaged(any(Pageable.class))).thenReturn(page);

        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(service.insert(any(ProductDTO.class))).thenReturn(productDTO);

        when(service.update(eq(existingId), any(ProductDTO.class))).thenReturn(productDTO);
        when(service.update(eq(nonExistingId), any(ProductDTO.class))).thenThrow(EntityNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(DataIntegrityViolationException.class).when(service).delete(dependentId);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.result.content").isArray(),
                        jsonPath("$.result.content[0].id").isNumber(),
                        jsonPath("$.result.content[0].name").isString(),
                        jsonPath("$.result.content[0].price").isNumber(),
                        jsonPath("$.result.content[0].imgUrl").isString(),
                        jsonPath("$.result.content[0].categories").isArray(),
                        jsonPath("$.result.page.totalElements").isNumber(),
                        jsonPath("$.result.page.totalPages").isNumber()
                );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/product/{id}", existingId).accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.result.id").isNumber(),
                        jsonPath("$.result.name").isString(),
                        jsonPath("$.result.price").isNumber(),
                        jsonPath("$.result.imgUrl").isString(),
                        jsonPath("$.result.categories").isArray()

                );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void findByIdShouldReturn404WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/product/{id}", nonExistingId).accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result.status").value(404));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void insertShouldReturnProductDTOWhenIdIsNull() throws Exception {
        productDTO.setId(null);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        productDTO.setId(existingId);

        mockMvc.perform(post("/product")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.result.id").isNumber(),
                        jsonPath("$.result.name").isString(),
                        jsonPath("$.result.price").isNumber(),
                        jsonPath("$.result.imgUrl").isString(),
                        jsonPath("$.result.categories").isArray()
                );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/product/{id}", existingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.result.id").isNumber(),
                        jsonPath("$.result.name").isString(),
                        jsonPath("$.result.price").isNumber(),
                        jsonPath("$.result.imgUrl").isString(),
                        jsonPath("$.result.categories").isArray()
                );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void updateShouldReturn404WhenIdDoesNotExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/product/{id}", nonExistingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result.status").value(404));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteShouldReturn204WhenIdExists() throws Exception {
        mockMvc.perform(delete("/product/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void deleteShouldReturn409WhenIdIsDependent() throws Exception {
        mockMvc.perform(delete("/product/{id}", dependentId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result.status").value(409));
    }
}
