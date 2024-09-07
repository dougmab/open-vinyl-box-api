package com.github.dougmab.openvinylboxapi.dto;

import com.github.dougmab.openvinylboxapi.validation.annotation.Password;

public class UserInsertDTO extends UserDTO {

    @Password
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
