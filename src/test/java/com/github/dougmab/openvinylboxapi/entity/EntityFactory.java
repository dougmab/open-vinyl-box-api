package com.github.dougmab.openvinylboxapi.entity;

import com.github.dougmab.openvinylboxapi.dto.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityFactory {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static Category createCategory(Long id) {
        return new Category(id, "Pop");
    }

    public static CategoryDTO createCategoryDTO(long id) {
        return new CategoryDTO(createCategory(id));
    }

    public static Product createProduct(Long id) {
        Product product = new Product(id, "Thriller", 9.99, "https://picsum.photos/200", Instant.parse("1982-11-29T10:00:00Z"));
        product.getCategories().add(createCategory(1L));
        return product;
    }

    public static ProductDTO createProductDTO(Long id) {
        return new ProductDTO(createProduct(id));
    }

    public static TokenDTO createTokenDTO(User user) {
        String scopes = user.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        long expiresInSeconds = 60 * 5;

        var claims = JwtClaimsSet.builder()
                .issuer("open-vinyl-box-api-test")
                .subject(user.getId().toString())
                .expiresAt(now.plusSeconds(expiresInSeconds))
                .issuedAt(now)
                .claim("scope", scopes)
                .build();

        String tokenString = new Jwt("open-vinyl-box-api-test", now, now.plusSeconds(expiresInSeconds),
                Collections.singletonMap("alg", "none"), claims.getClaims()).getTokenValue();

        return new TokenDTO(tokenString, expiresInSeconds);
    }

    public static UserDTO createAdminDTO(Long id) {
        UserDTO adminDto =  new UserDTO(id, "Admin", "", "admin@admin.com");
        adminDto.getRoles().add(new RoleDTO(1L, Role.Authorities.ADMIN.name()));

        return adminDto;
    }

    public static User createAdmin(Long id) {
        String password = Objects.requireNonNullElse(System.getenv("ADMIN_PASSWORD"), "Test_123");
        User admin =  new User(id, "Admin", "", "admin@admin.com", passwordEncoder.encode(password));
        admin.getRoles().add(new Role(1L, Role.Authorities.ADMIN.name()));

        return admin;
    }

    public static UserDTO createUserDTO(Long id) {
        UserDTO userDto = new UserDTO(id, "Admin", "", "admin@admin.com");
        userDto.getRoles().add(new RoleDTO(2L, Role.Authorities.USER.name()));

        return userDto;
    }

    public static User createUser(Long id) {
        User user = new User(id, "Bob", "Tester", "test@example.com", passwordEncoder.encode("Test_123"));
        user.getRoles().add(new Role(2L, Role.Authorities.USER.name()));

        return user;
    }
}
