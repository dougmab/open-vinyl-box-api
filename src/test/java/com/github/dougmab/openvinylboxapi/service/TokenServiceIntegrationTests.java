package com.github.dougmab.openvinylboxapi.service;

import com.github.dougmab.openvinylboxapi.dto.LoginDTO;
import com.github.dougmab.openvinylboxapi.entity.User;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TokenServiceIntegrationTests {

    @Autowired
    private TokenService service;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserRepository repository;

    @Test
    public void generateTokenShouldReturnValidToken() {
        LoginDTO login = new LoginDTO("admin@openvinylbox.com", "admin");
        var token = service.generateToken(login).getAccessToken();

        Jwt decodedToken = jwtDecoder.decode(token);

        User user = repository.findByEmail(login.getEmail()).get();

        assertThat(token).isNotNull();
        assertThat(decodedToken).isNotNull();
        assertThat(decodedToken.getSubject()).isEqualTo(user.getId().toString());
        // Intellij is tripping so I did this workaround
        assertThat(Optional.ofNullable(decodedToken.getClaim("scope")).get()).isEqualTo("ADMIN");
        assertThat(decodedToken.getExpiresAt()).isNotNull();
        assertThat(decodedToken.getIssuedAt()).isNotNull();

    }
}
