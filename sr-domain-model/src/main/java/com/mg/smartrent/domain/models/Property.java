package com.mg.smartrent.domain.models;

import com.mg.smartrent.domain.enums.EnBuildingType;
import com.mg.smartrent.domain.enums.EnPropertyCondition;
import com.mg.smartrent.domain.validation.annotations.ValueOfEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class Property extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    private String userTID;

    @NotNull
    @ValueOfEnum(enumClass = EnBuildingType.class)
    private String buildingType;

    @NotNull
    @ValueOfEnum(enumClass = EnPropertyCondition.class)
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
