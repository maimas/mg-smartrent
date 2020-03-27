package com.mg.smartrent.domain.models;


import com.mysema.query.annotations.QueryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@QueryEntity
public class RenterReview extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    private String userTID;

    @NotNull
    @Size(min = 1, max = 100)
    private String renterTID;

    @NotNull
    @Size(min = 1, max = 1000000)
    private String review;

    @Min(1)
    @Max(5)
    private Integer rating;
}
