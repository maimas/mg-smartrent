package com.mg.smartrent.domain.models;


import com.mg.smartrent.domain.enums.EnGender;
import com.mg.smartrent.domain.validation.annotations.PhoneNumber;
import com.mg.smartrent.domain.validation.annotations.ValueOfEnum;
import com.mysema.query.annotations.QueryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@QueryEntity
public class Renter extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @Past
    private Date dateOfBirth;

    @NotNull
    @Email
    @Size(min = 1, max = 100)
    private String email;

    @Size(min = 1, max = 100)
    @PhoneNumber
    private String phoneNumber;

    @NotNull
    @ValueOfEnum(enumClass = EnGender.class)
    private String gender;

}
