package com.mg.smartrent.domain.models;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class Renter extends BizItem {

    @NotNull
    @NotBlank
    public String firstName;

    @NotNull
    @NotBlank
    public String lastName;

    @NotNull
    @Past
    public Date dateOfBirth;

    @NotNull
    @NotBlank
    @Email
    public String email;

    @NotNull
    @NotBlank
    public String phoneNumber;

    @NotNull
    @NotBlank
    public String gender;

    public Renter() {

    }
}
