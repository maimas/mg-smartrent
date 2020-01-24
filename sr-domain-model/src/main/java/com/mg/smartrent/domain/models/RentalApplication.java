package com.mg.smartrent.domain.models;

import com.mg.smartrent.domain.enums.EnCurrency;
import com.mg.smartrent.domain.validation.annotations.ValueOfEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class RentalApplication extends BizItem {

    @NotNull
    @Size(min = 1, max = 100)
    private String renterUserTID;

    @NotNull
    @Size(min = 1, max = 100)
    private String propertyTID;

    @Positive
    private Integer price;

    @NotNull
    @ValueOfEnum(enumClass = EnCurrency.class)
    private String currency;

    @NotNull
    private Date checkInDate;

    @NotNull
    private Date checkOutDate;

    @NotNull
    @ValueOfEnum(enumClass = EnCurrency.class)
    private String status;

    public RentalApplication() {

    }

}
