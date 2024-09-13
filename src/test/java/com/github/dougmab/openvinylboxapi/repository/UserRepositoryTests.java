package com.github.dougmab.openvinylboxapi.repository;

import com.github.dougmab.openvinylboxapi.dto.UserDTO;
import com.github.dougmab.openvinylboxapi.entity.EntityFactory;
import com.github.dougmab.openvinylboxapi.entity.Role;
import com.github.dougmab.openvinylboxapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
public class UserRepositoryTests {

    private final UserRepository repository;
    private final RoleRepository roleRepository;

    private Long existingId;
    private Long nonExistingId;

    @Autowired
    public UserRepositoryTests(UserRepository repository, ProductRepository productRepository, RoleRepository roleRepository) {
        this.repository = repository;
        this.roleRepository = roleRepository;
    }

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        User user = EntityFactory.createUser(null);

        String newEmail = "new@example.com";
        user.setEmail(newEmail);

        user.getRoles().clear();
        user.getRoles().add(roleRepository.getReferenceById(2L));

        user = repository.save(user);

        assertThat(user.getId()).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("Bob");
        assertThat(user.getLastName()).isEqualTo("Tester");
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getPassword()).isNotEqualTo("Test_123");
        assertThat(user.getRoles().size()).isEqualTo(1);
        Object[] roles = user.getRoles().toArray();
        assertThat(((Role) roles[0]).getAuthority()).isEqualTo("USER");
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
        Optional<User> user = repository.findById(existingId);

        assertThat(user).isNotNull();
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {
        User user = repository.findById(nonExistingId).orElse(null);

        assertThat(user).isNull();
    }

    @Test
    public void updateShouldUpdateObjectWhenIdExists() {
        User user = repository.getReferenceById(existingId);
        UserDTO dto = EntityFactory.createUserDTO(null);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        dto.setEmail("updated@example.com");
        user.setEmail(dto.getEmail());
        user.getRoles().clear();
        User finalUser = user;
        dto.getRoles().forEach(role -> finalUser.getRoles().add(roleRepository.getReferenceById(role.getId())));

        user = repository.save(user);

        assertThat(user.getId()).isEqualTo(existingId);
        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(dto.getLastName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getPassword()).isNotEqualTo("Test_123");
        assertThat(user.getRoles().size()).isEqualTo(1);
        Object[] roles = user.getRoles().toArray();
        assertThat(((Role) roles[0]).getAuthority()).isEqualTo("USER");
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);

        User user = repository.findById(existingId).orElse(null);

        assertThat(user).isNull();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteShouldDeleteUser() {
        User user = EntityFactory.createUser(null);

        user = repository.save(user);

        existingId = user.getId();

        assertThatNoException().isThrownBy(() -> {
            repository.deleteById(existingId);
        });

        assertThat(repository.findById(existingId)).isEmpty();
    }
}
