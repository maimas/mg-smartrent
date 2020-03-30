package com.mg.smartrent.domain.models;


import com.mg.smartrent.domain.validation.annotations.PositiveDateRange;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@PositiveDateRange(start = "checkInDate", end = "checkOutDate", message = "CheckIn Date should not be greater than CheckOut Date")
@FieldNameConstants
public class PropertyListing extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    private String userId;

    @NotNull
    @Size(min = 1, max = 100)
    private String propertyId;

    @PositiveOrZero
    private long price;

    @PositiveOrZero
    private int totalViews;

    @NotNull
    private Date checkInDate;

    @NotNull
    private Date checkOutDate;

    private boolean listed;

}
