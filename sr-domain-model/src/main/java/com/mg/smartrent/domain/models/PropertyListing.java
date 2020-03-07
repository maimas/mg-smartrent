package com.mg.smartrent.domain.models;


import com.mg.smartrent.domain.validation.annotations.PositiveDateRange;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@PositiveDateRange(start = "checkInDate", end = "checkOutDate", message = "CheckIn Date should not be greater than CheckOut Date")
public class PropertyListing extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    private String userTID;

    @NotNull
    @Size(min = 1, max = 100)
    private String propertyTID;

    @PositiveOrZero
    private long price;

    @PositiveOrZero
    private int totalViews;

    @NotNull
    private Date checkInDate;

    @NotNull
    private Date checkOutDate;

    private boolean listed;

    public PropertyListing() {

    }
}
