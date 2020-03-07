package com.mg.smartrent.domain.models;

import com.mg.smartrent.domain.enums.EnCurrency;
import com.mg.smartrent.domain.enums.EnRentalApplicationStatus;
import com.mg.smartrent.domain.validation.annotations.PositiveDateRange;
import com.mg.smartrent.domain.validation.annotations.ValueOfEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@PositiveDateRange(start = "checkInDate", end = "checkOutDate", message = "CheckIn Date should not be greater than CheckOut Date")
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
    @ValueOfEnum(enumClass = EnRentalApplicationStatus.class)
    private String status;

    public RentalApplication() {

    }

}
