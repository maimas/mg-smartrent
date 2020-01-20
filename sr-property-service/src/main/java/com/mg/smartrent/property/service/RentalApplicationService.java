package com.mg.smartrent.property.service;


import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.models.RentalApplication;
import com.mg.smartrent.domain.validation.ModelBusinessValidationException;
import com.mg.smartrent.domain.validation.ModelValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.enrich;
import static com.mg.smartrent.domain.validation.ModelValidator.validate;

@Service
@Validated
public class RentalApplicationService {

    private static final Logger log = LogManager.getLogger(RentalApplicationService.class);

    private ExternalUserService userService;
    private QueryService<RentalApplication> queryService;
    private PropertyService propertyService;


    public RentalApplicationService(QueryService<RentalApplication> queryService, ExternalUserService userService, PropertyService propertyService) {
        this.queryService = queryService;
        this.userService = userService;
        this.propertyService = propertyService;
    }


    public RentalApplication save(@NotNull RentalApplication model) throws ModelValidationException {
        if (!userService.userExists(model.getRenterUserTID())) {
            throw new ModelBusinessValidationException(String.format("Rental Application could not be saved. User not found, UserTID = %s", model.getRenterUserTID()));
        }
        if (propertyService.findByTrackingId(model.getPropertyTID()) == null) {
            throw new ModelBusinessValidationException(String.format("Rental Application could not be saved. Property not found, TrackingId = %s", model.getPropertyTID()));
        }
        enrich(model);
        validate(model);
        RentalApplication dbModel = queryService.save(model);
        log.info(String.format("Rental Request created, %s. From user %s to property %s", dbModel.getTrackingId(), dbModel.getRenterUserTID(), dbModel.getPropertyTID()));

        return dbModel;
    }


    public RentalApplication findByTrackingId(@NotNull @NotBlank String trackingId) {
        List<RentalApplication> propertyList = queryService.findAllBy("trackingId", trackingId, RentalApplication.class);
        return (propertyList == null || propertyList.isEmpty()) ? null : propertyList.get(0);
    }

    public List<RentalApplication> findByRenterUserTID(@NotNull @NotBlank String renterUserTID) {
        return queryService.findAllBy("renterUserTID", renterUserTID, RentalApplication.class);
    }

    public List<RentalApplication> findByPropertyTID(@NotNull @NotBlank String propertyTID) {
        return queryService.findAllBy("propertyTID", propertyTID, RentalApplication.class);
    }


}
