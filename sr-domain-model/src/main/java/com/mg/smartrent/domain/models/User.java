package com.mg.smartrent.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mg.smartrent.domain.enums.EnGender;
import com.mg.smartrent.domain.enums.EnUserStatus;
import com.mg.smartrent.domain.validation.annotations.ValueOfEnum;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
public class User extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull
    @Past
    public Date dateOfBirth;

    @NotNull
    @ValueOfEnum(enumClass = EnGender.class)
    private String gender;

    @NotNull
    @Email
    @Size(min = 1, max = 100)
    private String email;

    @NotNull
    @Size(min = 6, max = 1000)
    @Getter(onMethod_ = {@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)})
    private String password;

    @ValueOfEnum(enumClass = EnUserStatus.class)
    private String status;

    @Setter(onMethod_ = {@JsonProperty(access = JsonProperty.Access.READ_ONLY)})
    private boolean enabled;


}
