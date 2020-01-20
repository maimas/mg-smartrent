package com.mg.smartrent.domain.models;

import com.mg.smartrent.domain.enums.EnUserStatus;
import com.mg.smartrent.domain.validation.annotations.ValueOfEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    public String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    public String lastName;

    @NotNull
    @Email
    @Size(min = 1, max = 100)
    public String email;

    @NotNull
    @Size(min = 6, max = 1000)
    public String password;

    @NotNull
    @ValueOfEnum(enumClass = EnUserStatus.class)
    public String status;

    public boolean enabled;


    public User() {

    }
}
