package com.mg.smartrent.domain.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@EqualsAndHashCode(callSuper = false)
public class Property extends BizItem {

    @NotNull
    @NotBlank
    private String userTID;

    @NotNull
    @NotBlank
    private String buildingType;

    @NotNull
    @NotBlank
    private String condition;

    @PositiveOrZero
    private Integer totalRooms;

    @PositiveOrZero
    private Integer totalBathRooms;

    @PositiveOrZero
    private Integer totalBalconies;

    private byte[] thumbnail;
    private boolean parkingAvailable;

    public Property() {

    }

}
