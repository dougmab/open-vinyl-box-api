package com.github.dougmab.openvinylboxapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dougmab.openvinylboxapi.dto.ProductDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductControllerIntegrationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 11L;

        productDTO = EntityFactory.createProductDTO(existingId);
        productDTO.getCategories().add(EntityFactory.createCategoryDTO(1L));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void findAllShouldReturnPageOfProducts() throws Exception {
        mockMvc.perform(get("/product?size=10")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.content").exists(),
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content[0].id").value(1),
                        jsonPath("$.data.content[0].name").value("The Best of Miles Davis"),
                        jsonPath("$.data.content[0].price").value(9.99),
                        jsonPath("$.data.content[0].imgUrl").value("https://picsum.photos/200"),
                        jsonPath("$.data.content[0].date").isString(),
                        jsonPath("$.data.content[0].categories").isArray(),
                        jsonPath("$.data.content[0].categories[0].id").value(1),
                        jsonPath("$.data.content[0].categories[0].name").value("Jazz"),
                        jsonPath("$.data.page.totalElements").value(countTotalProducts),
                        jsonPath("$.data.page.totalPages").value(2)
                );
    }

    @Test
    @WithUserDetails("test@example.com")
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/product/{id}", existingId)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(1),
                        jsonPath("$.data.name").value("The Best of Miles Davis"),
                        jsonPath("$.data.price").value(9.99),
                        jsonPath("$.data.imgUrl").value("https://picsum.photos/200"),
                        jsonPath("$.data.date").isString(),
                        jsonPath("$.data.categories").isArray(),
                        jsonPath("$.data.categories[0].id").value(1),
                        jsonPath("$.data.categories[0].name").value("Jazz")
                );
    }

    @Test
    @WithUserDetails("test@example.com")
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/product/{id}", nonExistingId)
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));

    }

    @Test
    @WithUserDetails("test@example.com")
    public void insertShouldPersistProductWithAutoincrementWhenIdIsNull() throws Exception {
        productDTO.setId(null);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        mockMvc.perform(post("/product")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.data.id").value(countTotalProducts + 1),
                        jsonPath("$.data.name").value(productDTO.getName()),
                        jsonPath("$.data.price").value(productDTO.getPrice()),
                        jsonPath("$.data.imgUrl").value(productDTO.getImgUrl()),
                        jsonPath("$.data.date").isString(),
                        jsonPath("$.data.categories").isArray(),
                        jsonPath("$.data.categories[0].id").value(productDTO.getCategories().get(0).getId())
                );
    }

    @Test
    @WithUserDetails("test@example.com")
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        mockMvc.perform(put("/product/{id}", existingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(existingId),
                        jsonPath("$.data.name").value(productDTO.getName()),
                        jsonPath("$.data.price").value(productDTO.getPrice()),
                        jsonPath("$.data.imgUrl").value(productDTO.getImgUrl()),
                        jsonPath("$.data.date").isString(),
                        jsonPath("$.data.categories").isArray(),
                        jsonPath("$.data.categories[0].id").value(productDTO.getCategories().get(0).getId())
                );
    }

    @Test
    @WithUserDetails("test@example.com")
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        mockMvc.perform(put("/product/{id}", nonExistingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void deleteShouldDeleteResourceWhenIdExists() throws Exception {
        mockMvc.perform(delete("/product/{id}", existingId)
                        .accept("application/json"))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isEqualTo(countTotalProducts - 1);
    }

    @Test
    @WithUserDetails("test@example.com")
    public void deleteShouldDoNothingWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/product/{id}", nonExistingId)
                        .accept("application/json"))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isEqualTo(countTotalProducts);
    }
}
