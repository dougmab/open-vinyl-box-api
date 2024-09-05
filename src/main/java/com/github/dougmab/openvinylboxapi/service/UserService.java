package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.UserDTO;
import com.github.dougmab.openvinylboxapi.dto.UserInsertDTO;
import com.github.dougmab.openvinylboxapi.entity.User;
import com.github.dougmab.openvinylboxapi.exception.ExceptionFactory;
import com.github.dougmab.openvinylboxapi.repository.RoleRepository;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

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

        dto.getRoles().forEach(role -> {
            user.getRoles().add(roleRepository.getReferenceById(role.getId()));
        });

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
}
