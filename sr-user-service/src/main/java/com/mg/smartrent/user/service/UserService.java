package com.mg.smartrent.user.service;

import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.enums.EnUserStatus;
import com.mg.smartrent.domain.models.BizItem.Fields;
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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.enrich;
import static com.mg.smartrent.domain.validation.ModelValidator.validate;

@Service
@Log4j2
@Validated
public class UserService {

    private QueryService<User> queryService;
    private PasswordEncoder passwordEncoder;


    public UserService(QueryService<User> queryService, PasswordEncoder passwordEncoder) {
        this.queryService = queryService;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(@NotNull User model) throws ModelValidationException {
        log.debug("Creating new user...");

        if (findById(model.getId()).isPresent()) {
            throw new ModelBusinessValidationException("User ID already exists.");
        }
        if (findByEmail(model.getEmail()).isPresent()) {
            throw new ModelBusinessValidationException("User email already in use.");
        }
        if (StringUtils.isBlank(model.getPassword())) {
            throw new ModelBusinessValidationException("Password not specified.");
        }
        model.setPassword(passwordEncoder.encode(model.getPassword()));
        model.setStatus(EnUserStatus.Pending);
        model.setEnabled(false);

        enrich(model);
        validate(model);
        User dbUser = queryService.save(model);
        log.debug("User created {}", dbUser);

        return dbUser;
    }

    public User update(@NotNull User model) throws ModelValidationException {
        log.debug("Updating user...");

        User dbUser = findById(model.getId()).orElseThrow(() -> new ModelBusinessValidationException(String.format("User with ID=%s not found.", model.getId())));

        if (StringUtils.isEmpty(model.getPassword())) {
            model.setPassword(dbUser.getPassword());
        } else {
            model.setPassword(passwordEncoder.encode(model.getPassword()));
        }

        if (!Objects.equals(dbUser.getEmail(), model.getEmail())) {
            throw new ModelBusinessValidationException("Email update is not allowed.");
        }

        model.setEnabled(dbUser.isEnabled());
        enrich(model);
        validate(model);
        dbUser = queryService.save(model);
        log.debug("User updated {}", dbUser);

        return dbUser;
    }

    public User enable(String id, Boolean enabled) throws ModelValidationException {
        log.debug("Setting user enabled flag to {}. User ID={}", enabled, id);
        User dbUser = findById(id).orElseThrow(() -> new ModelBusinessValidationException("User not found."));

        dbUser.setEnabled(enabled);
        dbUser.setStatus(EnUserStatus.Active);
        enrich(dbUser);
        validate(dbUser);

        return queryService.save(dbUser);
    }

    public User resetPassword(String id, String rawPassword) throws ModelValidationException {
        log.debug("Resetting user password for. User ID={}", id);
        User dbUser = findById(id).orElseThrow(() -> new ModelBusinessValidationException("User not found."));
        dbUser.setPassword(rawPassword);
        update(dbUser);
        return dbUser;
    }

    public Optional<User> findByEmail(@NotNull @NotBlank @Email String email) {
        log.info("Searching user by email = {}", email);
        User user = queryService.findOneBy(User.Fields.email, email, User.class);
        return Optional.ofNullable(user);
    }

    public Optional<User> findById(@NotNull @NotBlank String id) {
        log.info("Searching user by id = {} ", id);
        User user = queryService.findOneBy(Fields.id, id, User.class);
        return Optional.ofNullable(user);
    }

}
