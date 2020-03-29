package com.mg.smartrent.domain.validation;

import com.mg.smartrent.domain.models.BizItem;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ModelValidator {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    public static void validate(BizItem model) throws ModelValidationException {
        Set<ConstraintViolation<BizItem>> violations = validator.validate(model);

        if (violations.size() > 0) {
            StringBuilder errors = new StringBuilder();
            violations.forEach(it -> {
                String msg = String.format("Field \"%s\" has an invalid value value \"%s\". [%s]", it.getPropertyPath(), it.getInvalidValue(), it.getMessage());
                errors.append(msg).append("\n");
            });
            throw new ModelValidationException(errors.toString());
        }

    }
}
