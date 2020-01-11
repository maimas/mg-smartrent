package com.mg.smartrent.domain.models;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class PropertyListing extends BizItem {

    @NotNull
    @NotBlank
    private String userTID;

    @NotNull
    @NotBlank
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
