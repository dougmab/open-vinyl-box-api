package com.github.dougmab.openvinylboxapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dougmab.openvinylboxapi.config.SecurityConfig;
import com.github.dougmab.openvinylboxapi.dto.UserDTO;
import com.github.dougmab.openvinylboxapi.dto.UserInsertDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import com.github.dougmab.openvinylboxapi.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;


    @MockBean
    private UserRepository repository;

    private UserDTO userDTO;
    private Page<UserDTO> page;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        userDTO = EntityFactory.createUserDTO(1L);
        page = new PageImpl<>(List.of(userDTO));

        existingId = 1L;
        nonExistingId = 1000L;

        when(service.findAllPaged(any(Pageable.class))).thenReturn(page);

        when(service.findById(existingId)).thenReturn(userDTO);
        when(service.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(service.insert(any(UserInsertDTO.class))).thenReturn(userDTO);

        when(service.update(eq(existingId), any(UserDTO.class))).thenReturn(userDTO);
        when(service.update(eq(nonExistingId), any(UserDTO.class))).thenThrow(EntityNotFoundException.class);

        doNothing().when(service).delete(existingId);


        // UniqueEmailValidator stuff
        var tempUser = EntityFactory.createUser(existingId);
        tempUser.setEmail(userDTO.getEmail());
        when(repository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(tempUser));
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ADMIN"})
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.content").isArray(),
                        jsonPath("$.data.content[0].id").isNumber(),
                        jsonPath("$.data.content[0].firstName").isString(),
                        jsonPath("$.data.content[0].lastName").isString(),
                        jsonPath("$.data.content[0].email").isString(),
                        jsonPath("$.data.content[0].roles[0].authority").value("USER"),
                        jsonPath("$.data.page.totalPages").isNumber()
                );
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ADMIN"})
    public void findByIdShouldReturnUserDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/user/{id}", existingId).accept("application/json"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.firstName").isString(),
                        jsonPath("$.data.lastName").isString(),
                        jsonPath("$.data.email").isString(),
                        jsonPath("$.data.roles[0].authority").value("USER")
                );
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ADMIN"})
    public void findByIdShouldReturn404WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/user/{id}", nonExistingId).accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ADMIN"})
    public void insertShouldReturnUserDTOWhenIdIsNull() throws Exception {
        // differing email just to ignore UniqueEmailValidator
        UserInsertDTO insertDTO = new UserInsertDTO(null, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail() + "2", "Test_123");
        String jsonBody = objectMapper.writeValueAsString(insertDTO);
        userDTO.setId(existingId);

        mockMvc.perform(post("/user")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.firstName").isString(),
                        jsonPath("$.data.lastName").isString(),
                        jsonPath("$.data.email").isString(),
                        jsonPath("$.data.roles[0].authority").value("USER")
                );
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ADMIN"})
    public void updateShouldReturnUserDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(put("/user/{id}", existingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").isNumber(),
                        jsonPath("$.data.firstName").isString(),
                        jsonPath("$.data.lastName").isString(),
                        jsonPath("$.data.email").isString(),
                        jsonPath("$.data.roles[0].authority").value("USER")
                );
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ADMIN"})
    public void updateShouldReturn404WhenIdDoesNotExists() throws Exception {
        userDTO.setEmail(userDTO.getEmail() + "3");
        String jsonBody = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(put("/user/{id}", nonExistingId)
                        .accept("application/json")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.status").value(404));
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_ADMIN"})
    public void deleteShouldReturn204WhenIdExists() throws Exception {
        mockMvc.perform(delete("/user/{id}", existingId))
                .andExpect(status().isNoContent());
    }
}
