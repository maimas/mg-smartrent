package com.mg.smartrent.domain.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BizItem {

    @NotNull
    @NotBlank
    public String firstName;

    @NotNull
    @NotBlank
    public String lastName;

    @NotNull
    @NotBlank
    @Email
    public String email;

    @NotNull
    @NotBlank
    public String password;

    @NotNull
    @NotBlank
    public String status;

    public boolean enabled;


    public User() {

    }
}
