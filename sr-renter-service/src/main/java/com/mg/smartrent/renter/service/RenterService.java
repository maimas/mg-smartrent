package com.mg.smartrent.renter.service;


import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.models.Renter;
import com.mg.smartrent.domain.models.RenterReview;
import com.mg.smartrent.domain.models.User;
import com.mg.smartrent.domain.validation.ModelValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.enrich;
import static com.mg.smartrent.domain.validation.ModelValidator.validate;

@Service
@Validated
public class RenterService {

    private static final Logger log = LogManager.getLogger(RenterService.class);

    private ExternalUserService userService;
    private QueryService<Renter> queryService;


    public RenterService(ExternalUserService userService, QueryService<Renter> queryService) {
        this.queryService = queryService;
        this.userService = userService;
    }


    public Renter save(@NotNull Renter model) throws ModelValidationException {
        enrich(model);
        validate(model);
        Renter renter = queryService.save(model);
        log.info("Renter created. TrackingId = " + renter.getTrackingId());
        return renter;
    }

    public Renter findByEmail(@NotNull @Email String email, boolean createOnMissing) throws ModelValidationException {
        List<Renter> renters = queryService.findAllBy("email", email, Renter.class);
        Renter renter = (renters != null && !renters.isEmpty()) ? renters.get(0) : null;

        if (renter == null && createOnMissing) {
            renter = save(buildRenterFromUser(email));
        }

        return renter;
    }

    public Renter findByTrackingId(@NotNull String trackingId) {
        List<Renter> renters = queryService.findAllBy("trackingId", trackingId, Renter.class);
        return (renters != null && !renters.isEmpty()) ? renters.get(0) : null;
    }

    //----------------Private Methods------------------------------
    private Renter buildRenterFromUser(String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException(String.format("Renter could not be created. User with email %s not found.", userEmail));
        }
        Renter renter = new Renter();
        renter.setDateOfBirth(user.dateOfBirth);
        renter.setFirstName(user.getFirstName());
        renter.setLastName(user.getLastName());
        renter.setEmail(user.getEmail());
        renter.setGender(user.getGender());

        return renter;
    }
}
