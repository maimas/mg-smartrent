package com.mg.smartrent.domain.models;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
public class RenterReview extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    private String userId;

    @NotNull
    @Size(min = 1, max = 100)
    private String renterId;

    @NotNull
    @Size(min = 1, max = 1000000)
    private String review;

    @Min(1)
    @Max(5)
    private Integer rating;
}
