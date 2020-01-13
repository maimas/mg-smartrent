package com.mg.smartrent.renter.service;


import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.models.Renter;
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

    private QueryService<Renter> queryService;


    public RenterService(QueryService<Renter> queryService) {
        this.queryService = queryService;
    }


    public Renter save(@NotNull Renter model) throws ModelValidationException {
        enrich(model);
        validate(model);
        Renter renter = queryService.save(model);
        log.info("Renter created. TrackingId = " + renter.getTrackingId());
        return renter;
    }

    public Renter findByEmail(@NotNull @Email String email) {
        List<Renter> renters = queryService.findAllBy("email", email, Renter.class);
        return (renters != null && !renters.isEmpty()) ? renters.get(0) : null;
    }
}
