package com.mg.smartrent.user.service;

import com.mg.persistence.service.nosql.MongoQueryService;
import com.mg.smartrent.domain.enums.EnUserStatus;
import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.domain.validation.ModelValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.*;
import static com.mg.smartrent.domain.validation.ModelValidator.*;

@Service
public class UserService {

    private MongoQueryService<User> queryService;
    private PasswordEncoder passwordEncoder;


    public UserService(MongoQueryService<User> queryService, PasswordEncoder passwordEncoder) {
        this.queryService = queryService;
        this.passwordEncoder = passwordEncoder;
    }


    public User save(User model) throws ModelValidationException {


        if (model.getId() == null) {//new user
            model.setPassword(passwordEncoder.encode(model.getPassword()));
            model.setStatus(EnUserStatus.Active.name());

        } else {//reset password
            User dbUser = findByEmail(model.getEmail());
            if (dbUser != null && passwordEncoder.matches(model.getPassword(), dbUser.getPassword())) {
                model.setPassword(passwordEncoder.encode(model.getPassword()));
            }
        }

        enrich(model);
        validate(model);
        return queryService.save(model);
    }

    public User findByTrackingId(String trackingId) {
        return queryService.findOneBy("trackingId", trackingId, User.class);
    }

    public User findByEmail(String email) {
        return queryService.findOneBy("email", email, User.class);
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}