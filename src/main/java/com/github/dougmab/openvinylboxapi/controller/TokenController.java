package com.github.dougmab.openvinylboxapi.controller;

import com.github.dougmab.openvinylboxapi.dto.LoginDTO;
import com.github.dougmab.openvinylboxapi.dto.TokenDTO;
import com.github.dougmab.openvinylboxapi.payload.ApiResponse;
import com.github.dougmab.openvinylboxapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final TokenService service;

    @Autowired
    public TokenController(TokenService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDTO>> login(@RequestBody LoginDTO loginDto) {
        TokenDTO token = service.generateToken(loginDto);

        return ResponseEntity.ok(ApiResponse.ok(token));
    }
}
