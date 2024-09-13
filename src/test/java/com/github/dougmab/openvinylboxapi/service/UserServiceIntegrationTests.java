package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.UserDTO;
import com.github.dougmab.openvinylboxapi.dto.UserInsertDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTests {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalUsers;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalUsers = 4L;  // 3 seeded users + 1 admin user injected by AdminInit
        userDTO = EntityFactory.createUserDTO(existingId);
    }

    @Test
    public void findAllPagedShouldReturnPageOfPage0Size10() {
        Pageable pageable = PageRequest.of(0, 10);
        var page = service.findAllPaged(pageable);

        assertThat(page).isNotNull();
        assertThat(page.toList()).hasSize(countTotalUsers.intValue());
        assertThat(page.getTotalElements()).isEqualTo(countTotalUsers);
    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists() {
        var UserDTO = service.findById(existingId);

        assertThat(UserDTO).isNotNull();
        assertThat(UserDTO.getId()).isEqualTo(repository.findById(existingId).get().getId());
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.findById(nonExistingId));
    }

    @Test
    public void insertShouldPersistWithAutoincrementWhenIdIsNull() {
        UserInsertDTO insertDto = new UserInsertDTO(null, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), "Test_123");
        var dto = service.insert(insertDto);

        assertThat(dto.getId()).isEqualTo(countTotalUsers + 1);
        assertThat(dto.getFirstName()).isEqualTo(userDTO.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(userDTO.getLastName());
        assertThat(dto.getEmail()).isEqualTo(userDTO.getEmail());
    }

    @Test
    public void updateShouldUpdateUserWhenIdExists() {
        var dto = service.update(existingId, userDTO);

        assertThat(dto.getId()).isEqualTo(existingId);
        assertThat(dto.getFirstName()).isEqualTo(userDTO.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(userDTO.getLastName());
        assertThat(dto.getEmail()).isEqualTo(userDTO.getEmail());
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.update(nonExistingId, userDTO));
    }

    @Test
    public void deleteShouldDeleteUserWhenIdExists() {
        service.delete(existingId);

        assertThat(repository.count()).isEqualTo(countTotalUsers - 1);
    }

    @Test
    public void deleteShouldDoNothingWhenIdDoesNotExist() {
        service.delete(nonExistingId);

        assertThat(repository.count()).isEqualTo(countTotalUsers);
    }
}
