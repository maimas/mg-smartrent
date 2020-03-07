package com.mg.smartrent.domain.validation.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PositiveDateRangeValidator.class})
public @interface PositiveDateRange {

    String message() default "Invalid date range.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String start();

    String end();
}