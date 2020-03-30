package com.mg.smartrent.renter.services;


import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.models.RenterReview;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.domain.validation.ModelValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.enrich;

@Service
@Validated
public class RenterReviewService {

    private static final Logger log = LogManager.getLogger(RenterReviewService.class);

    private ExternalUserService userService;
    private RenterService renterService;
    private QueryService<RenterReview> queryService;


    public RenterReviewService(ExternalUserService userService, RenterService renterService, QueryService<RenterReview> queryService) {
        this.queryService = queryService;
        this.renterService = renterService;
        this.userService = userService;
    }


    public RenterReview save(@NotNull RenterReview model) throws ModelValidationException {
        enrich(model);
        validate(model);
        RenterReview review = queryService.save(model);
        log.info("Renter review created. Id = {}", review.getId());
        return review;
    }

    public List<RenterReview> findByRenterId(@NotNull String renterId) {
        return queryService.findAllBy(RenterReview.Fields.renterId, renterId, RenterReview.class);
    }

    //----------------Private Methods------------------------------
    private void validate(RenterReview renterReview) throws ModelValidationException {
        ModelValidator.validate(renterReview);

        if (!userService.userExists(renterReview.getUserId())) {
            throw new RuntimeException(String.format("Renter Review could not be saved. User with Id %s not found.", renterReview.getUserId()));
        }

        if (renterService.findById(renterReview.getRenterId()) == null) {
            throw new RuntimeException(String.format("Renter Review could not be saved. Renter with Id %s not found.", renterReview.getRenterId()));
        }
    }
}
