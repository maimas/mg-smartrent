package com.mg.smartrent.domain.models;


import com.mg.smartrent.domain.enums.EnGender;
import com.mg.smartrent.domain.validation.annotations.PhoneNumber;
import com.mg.smartrent.domain.validation.annotations.ValueOfEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class Renter extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    public String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    public String lastName;

    @NotNull
    @Past
    public Date dateOfBirth;

    @NotNull
    @Email
    @Size(min = 1, max = 100)
    public String email;

    @NotNull
    @Size(min = 1, max = 100)
    @PhoneNumber
    public String phoneNumber;

    @NotNull
    @ValueOfEnum(enumClass = EnGender.class)
    public String gender;

    public Renter() {

    }
}
