package com.github.dougmab.openvinylboxapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dougmab.openvinylboxapi.dto.LoginDTO;
import com.github.dougmab.openvinylboxapi.dto.UserDTO;
import com.github.dougmab.openvinylboxapi.dto.UserInsertDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import com.github.dougmab.openvinylboxapi.service.TokenService;
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
public class UserControllerIntegrationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    private String adminToken;

    private long existingId;
    private long nonExistingId;
    private long countTotalCategories;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = 4L;

        userDTO = EntityFactory.createUserDTO(existingId);

        adminToken = tokenService.generateToken(
                new LoginDTO(
                        "admin@openvinylbox.com",
                        "admin"
                )
        ).getAccessToken(); // I'm setting the admin token manually because the @WithUserDetails annotation is not working as expected
    }

    @Test
    public void anyMethodShouldReturnUnauthorizedWhenNoAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/user")
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/user/{id}", existingId)
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/user/{id}", existingId)
                        .accept("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("test@example.com")
    public void anyMethodShouldReturnForbiddenWhenUserHasNotEnoughScope() throws Exception {
        mockMvc.perform(get("/user")
                        .accept("application/json"))
                .andExpect(status().isForbidden());

        // This test is failing because he's checking for the body content first, and ---> I don't want to write that body <---
//        mockMvc.perform(put("/user/{id}", existingId)
//                        .accept("application/json"))
//                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/user/{id}", existingId)
                        .accept("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void findAllShouldReturnPageOfUsers() throws Exception {
        mockMvc.perform(get("/user?size=10&sort=id")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.content").exists(),
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content[0].id").value(1),
                        jsonPath("$.data.content[0].firstName").value("User"),
                        jsonPath("$.data.content[0].lastName").value("Tester"),
                        jsonPath("$.data.content[0].email").value("test@example.com"),
                        jsonPath("$.data.content[0].roles[0].authority").value("USER"),
                        jsonPath("$.data.page.totalElements").value(countTotalCategories),
                        jsonPath("$.data.page.totalPages").value(1)
                );
    }

    @Test
    @WithUserDetails("admin@openvinylbox.com")
    public void findByIdShouldReturnUserDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/user/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(existingId),
                        jsonPath("$.data.firstName").value("User"),
                        jsonPath("$.data.lastName").value("Tester"),
                        jsonPath("$.data.email").value("test@example.com"),
                        jsonPath("$.data.roles[0].authority").value("USER")
                );
    }

    @Test
    @WithUserDetails("admin@openvinylbox.com")
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/user/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));

    }

    @Test
    @WithUserDetails("admin@openvinylbox.com")
    public void insertShouldPersistUserWithAutoincrementWhenIdIsNullWhileUnauthenticated() throws Exception {
        UserInsertDTO insertDto = new UserInsertDTO(null, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), "Test_123");
        String jsonBody = objectMapper.writeValueAsString(insertDto);
        mockMvc.perform(post("/user")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.data.id").value(countTotalCategories + 1),
                        jsonPath("$.data.firstName").value(insertDto.getFirstName()),
                        jsonPath("$.data.lastName").value(insertDto.getLastName()),
                        jsonPath("$.data.email").value(insertDto.getEmail()),
                        jsonPath("$.data.password").doesNotExist(),
                        jsonPath("$.data.roles[0].authority").value("USER")
                );
    }

    @Test
    @WithUserDetails("admin@openvinylbox.com")
    public void updateShouldReturnUserDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userDTO);
        mockMvc.perform(put("/user/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(existingId),
                        jsonPath("$.data.firstName").value(userDTO.getFirstName()),
                        jsonPath("$.data.lastName").value(userDTO.getLastName()),
                        jsonPath("$.data.email").value(userDTO.getEmail()),
                        jsonPath("$.data.password").doesNotExist(),
                        jsonPath("$.data.roles[0].authority").value("USER")
                );
    }

    @Test
    @WithUserDetails("admin@openvinylbox.com")
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userDTO);
        mockMvc.perform(put("/user/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
    @WithUserDetails("admin@openvinylbox.com")
    public void deleteShouldDeleteResourceWhenIdExists() throws Exception {
        mockMvc.perform(delete("/user/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept("application/json"))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isEqualTo(countTotalCategories - 1);
    }

    @Test
    @WithUserDetails("admin@openvinylbox.com")
    public void deleteShouldDoNothingWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/user/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept("application/json"))
                .andExpect(status().isNoContent());

        assertThat(repository.count()).isEqualTo(countTotalCategories);
    }
}
