package com.mg.smartrent.domain.models;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.Date;

@Data
@NoArgsConstructor
@FieldNameConstants
public abstract class BizItem {

    private BigInteger id;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 100, message = "TrackingID must be between 5 and 100 characters")
    private String trackingId;

    @NotNull
    @PastOrPresent
    private Date createdDate;

    @NotNull
    private Date modifiedDate;
}
