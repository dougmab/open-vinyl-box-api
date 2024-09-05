package com.github.dougmab.openvinylboxapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserInsertDTO extends UserDTO {

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 80, message = "Password must be between 6 and 80 characters")
    private String password;

    public UserInsertDTO() {
        super();
    }

    public UserInsertDTO(Long id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
