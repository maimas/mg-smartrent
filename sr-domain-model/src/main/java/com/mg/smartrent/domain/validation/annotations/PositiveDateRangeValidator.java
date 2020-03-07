package com.mg.smartrent.domain.validation.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Date;


public class PositiveDateRangeValidator implements ConstraintValidator<PositiveDateRange, Object> {

    private String start;
    private String end;

    @Override
    public void initialize(PositiveDateRange constraint) {
        start = constraint.start();
        end = constraint.end();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Date startDate = (Date) getFieldValue(object, start);
            Date endDate = (Date) getFieldValue(object, end);

            if ((startDate == null && endDate != null) || (startDate != null && endDate == null)) {
                return false;
            }
            if (startDate == null && endDate == null) {//if both are null then consider as valid case
                return true;
            }
            return startDate.before(endDate) || startDate.equals(endDate);

        } catch (Exception e) {
            return false;
        }
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

}
