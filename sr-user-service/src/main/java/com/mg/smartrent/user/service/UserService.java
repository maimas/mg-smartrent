package com.mg.smartrent.user.service;

import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.enums.EnUserStatus;
import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.domain.validation.ModelBusinessValidationException;
import com.mg.smartrent.domain.validation.ModelValidationException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.enrich;
import static com.mg.smartrent.domain.validation.ModelValidator.validate;

@Service
@Validated
@Log4j2
public class UserService {

    private QueryService<User> queryService;
    private PasswordEncoder passwordEncoder;


    public UserService(QueryService<User> queryService, PasswordEncoder passwordEncoder) {
        this.queryService = queryService;
        this.passwordEncoder = passwordEncoder;
    }


    public User save(@NotNull User model) throws ModelValidationException {
        log.info("Saving user: " + model.getEmail());
        if (model.getId() != null) {
            throw new ModelBusinessValidationException("Could not save an existent user.");
        }
        if (StringUtils.isBlank(model.getPassword())) {
            throw new ModelValidationException("User could not be saved. Password not specified.");
        }
        model.setPassword(passwordEncoder.encode(model.getPassword()));
        model.setStatus(EnUserStatus.Pending.name());

        enrich(model);
        validate(model);
        return queryService.save(model);
    }


    public User update(@NotNull User model) throws ModelValidationException {
        log.info("Updating user: " + model.getEmail());
        if (model.getId() == null || findByTrackingId(model.getTrackingId()) == null) {
            throw new ModelBusinessValidationException("Could not update. User does not exists.");
        }
        if (StringUtils.isBlank(model.getPassword())) {
            throw new ModelValidationException("User could not be updated. Password not specified.");
        }
        model.setPassword(passwordEncoder.encode(model.getPassword()));

        enrich(model);
        validate(model);
        return queryService.save(model);
    }

    public User findByTrackingId(@NotNull @NotBlank String trackingId) {
        log.info("Searching user by trackingId: " + trackingId);
        return queryService.findOneBy("trackingId", trackingId, User.class);
    }


    public User findByEmail(@NotNull @NotBlank @Email String email) {
        log.info("Searching user by trackingId: " + email);
        return queryService.findOneBy("email", email, User.class);
    }

}
