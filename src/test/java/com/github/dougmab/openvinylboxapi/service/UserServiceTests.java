package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.UserDTO;
import com.github.dougmab.openvinylboxapi.dto.UserInsertDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.entity.Role;
import com.github.dougmab.openvinylboxapi.entity.User;
import com.github.dougmab.openvinylboxapi.repository.RoleRepository;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    PageImpl<User> page;


    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 5L;
        page = new PageImpl<>(List.of(EntityFactory.createUser(1L),
                EntityFactory.createUser(2L),
                EntityFactory.createUser(3L)));

        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(repository.findById(existingId)).thenReturn(Optional.of(EntityFactory.createUser(existingId)));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.getReferenceById(existingId)).thenReturn(EntityFactory.createUser(existingId));

        var nonExistingUserRef = new User();
        nonExistingUserRef.setId(nonExistingId);

        when(repository.getReferenceById(nonExistingId)).thenReturn(nonExistingUserRef);

        when(repository.save(any(User.class))).thenReturn(EntityFactory.createUser(existingId));
        when(repository.save((nonExistingUserRef))).thenThrow(EntityNotFoundException.class);

        doNothing().when(repository).deleteById(existingId);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        when(roleRepository.getReferenceById(1L)).thenReturn(new Role(1L, "ADMIN"));
        when(roleRepository.getReferenceById(2L)).thenReturn(new Role(2L, "USER"));

        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$u06mL5aQs7J8lbnMWuIulu1zwW2.Pf.ESkAt/jPkDrTbPN12u2eHe");
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        var result = service.findAllPaged(Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists() {
        var dto = service.findById(existingId);

        assertThat(dto).isNotNull();

        verify(repository, times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.findById(nonExistingId));

        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void insertShouldReturnUserDTOWhenIdExists() {
        var tempUser = EntityFactory.createUser(existingId);
        var dto = service.insert(new UserInsertDTO(null, tempUser.getFirstName(), tempUser.getLastName(), tempUser.getEmail(), "Test_123"));

        assertThat(dto).isNotNull();

        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    public void updateShouldReturnUserDTOWhenIdExists() {
        var dto = service.update(existingId, new UserDTO(EntityFactory.createUser(existingId)));

        assertThat(dto).isNotNull();

        verify(repository, times(1)).getReferenceById(existingId);
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.update(nonExistingId, new UserDTO(EntityFactory.createUser(null))));

        verify(repository, times(1)).getReferenceById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        service.delete(existingId);

        verify(repository, times(1)).deleteById(existingId);
    }
}
