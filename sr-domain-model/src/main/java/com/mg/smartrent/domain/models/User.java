package com.mg.smartrent.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mg.smartrent.domain.enums.EnGender;
import com.mg.smartrent.domain.enums.EnUserStatus;
import io.leangen.graphql.annotations.GraphQLIgnore;
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
    private Date dateOfBirth;

    @NotNull
    private EnGender gender;

    @NotNull
    @Email
    @Size(min = 1, max = 100)
    private String email;

    @NotNull
    @Size(min = 6, max = 1000)
    @JsonIgnore
//    @Getter(onMethod_ = {@JsonProperty(access = JsonProperty.Access.READ_ONLY), @GraphQLIgnore})
//    @Setter(onMethod_ = {@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)})
    @Getter(onMethod_ = {@JsonIgnore, @GraphQLIgnore})
    @Setter(onMethod_ = {@JsonProperty(access = JsonProperty.Access.READ_WRITE)})
    /**
     * Json annotations are used to address security concerns:
     *  - when MODEL is converted in to JSON - password is ignored
     *  - when JSON is converted in to MODEL - password present
     * */
    private String password;

    @NotNull
    private EnUserStatus status;

    private boolean enabled;


}
