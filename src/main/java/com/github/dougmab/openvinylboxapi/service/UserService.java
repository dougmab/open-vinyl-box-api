package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.UserDTO;
import com.github.dougmab.openvinylboxapi.dto.UserInsertDTO;
import com.github.dougmab.openvinylboxapi.entity.Role;
import com.github.dougmab.openvinylboxapi.entity.User;
import com.github.dougmab.openvinylboxapi.exception.ExceptionFactory;
import com.github.dougmab.openvinylboxapi.repository.RoleRepository;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, RoleRepository RoleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = RoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        Page<User> list = repository.findAll(pageable);
        return list.map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User entity = repository.findById(id).orElseThrow(() -> ExceptionFactory.entityNotFound(User.class, id));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User user = new User(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.getRoles().add(roleRepository.findByAuthority(Role.Authorities.USER.name()));

        try {
            User entity = repository.save(user);

            return new UserDTO(entity);
        } catch (DataIntegrityViolationException e) {
            throw ExceptionFactory.dataIntegrityViolationUniqueField(User.class, "email");
        }
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            entity.setFirstName(dto.getFirstName());
            entity.setLastName(dto.getLastName());
            entity.setEmail(dto.getEmail());

            entity.getRoles().clear();

            User finalEntity = entity;
            dto.getRoles().forEach(role -> {
                finalEntity.getRoles().add(roleRepository.getReferenceById(role.getId()));
            });

            entity = repository.save(entity);

            // Must commit changes so the email unique constraint is enforced
            repository.flush();

            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw ExceptionFactory.entityNotFound(User.class, id);
        } catch (DataIntegrityViolationException e) {
            throw ExceptionFactory.dataIntegrityViolationUniqueField(User.class, "email");
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw ExceptionFactory.dataIntegrityViolationForeignKey(User.class);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(username);

        if (user.isEmpty()) {
            logger.error("User not found: {}", username);
            throw new UsernameNotFoundException("Email not found");
        }

        logger.info("User found: {}", username);

        return user.get();
    }
}
