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
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content[0].id").isNumber(),
                        jsonPath("$.data.content[0].name").isString(),
                        jsonPath("$.data.content[0].price").isNumber(),
                        jsonPath("$.data.content[0].imgUrl").isString(),
                        jsonPath("$.data.content[0].date").isString(),
                        jsonPath("$.data.content[0].categories").isArray(),
                        jsonPath("$.data.page.totalElements").isNumber(),
                        jsonPath("$.data.page.totalPages").isNumber()
                );
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/product/{id}", existingId).accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.name").isString(),
                        jsonPath("$.data.price").isNumber(),
                        jsonPath("$.data.imgUrl").isString(),
                        jsonPath("$.data.date").isString(),
                        jsonPath("$.data.categories").isArray()

                );
    }

    @Test
    public void findByIdShouldReturn404WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/product/{id}", nonExistingId).accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
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
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.name").isString(),
                        jsonPath("$.data.price").isNumber(),
                        jsonPath("$.data.imgUrl").isString(),
                        jsonPath("$.data.date").isString(),
                        jsonPath("$.data.categories").isArray()
                );
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/product/{id}", existingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.name").isString(),
                        jsonPath("$.data.price").isNumber(),
                        jsonPath("$.data.imgUrl").isString(),
                        jsonPath("$.data.date").isString(),
                        jsonPath("$.data.categories").isArray()
                );
    }

    @Test
    public void updateShouldReturn404WhenIdDoesNotExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/product/{id}", nonExistingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
    public void deleteShouldReturn204WhenIdExists() throws Exception {
        mockMvc.perform(delete("/product/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturn400WhenIdIsDependent() throws Exception {
        mockMvc.perform(delete("/product/{id}", dependentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.status").value(400));
    }
}
