package com.mg.smartrent.property.services;


import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.enrichment.ModelEnricher;
import com.mg.smartrent.domain.enums.EnRentalApplicationStatus;
import com.mg.smartrent.domain.models.BizItem;
import com.mg.smartrent.domain.models.RentalApplication;
import com.mg.smartrent.domain.validation.ModelBusinessValidationException;
import com.mg.smartrent.domain.validation.ModelValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.valid4j.Validation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
        Validation.validate(userService.userExists(model.getRenterUserId()), new ModelBusinessValidationException(String.format("Rental Application could not be saved. User not found, UserId = %s", model.getRenterUserId())));
        Validation.validate(propertyService.findById(model.getPropertyId()) != null, new ModelBusinessValidationException(String.format("Rental Application could not be saved. Property not found, Id = %s", model.getPropertyId())));

        enrich(model);
        validate(model);
        RentalApplication dbModel = queryService.save(model);
        log.debug("Rental Request created: {}", dbModel);

        return dbModel;
    }


    public RentalApplication findById(@NotNull @NotBlank String id) {
        List<RentalApplication> propertyList = queryService.findAllBy(BizItem.Fields.id, id, RentalApplication.class);
        return (propertyList == null || propertyList.isEmpty()) ? null : propertyList.get(0);
    }

    public List<RentalApplication> findByRenterUserId(@NotNull @NotBlank String renterUserId) {
        return queryService.findAllBy(RentalApplication.Fields.renterUserId, renterUserId, RentalApplication.class);
    }

    public List<RentalApplication> findByPropertyId(@NotNull @NotBlank String propertyId) {
        return queryService.findAllBy(RentalApplication.Fields.propertyId, propertyId, RentalApplication.class);
    }


    //-----------------Private Methods-------------------

    public void enrich(RentalApplication model) {

        if (model.getId() == null) {
            model.setStatus(EnRentalApplicationStatus.PendingOwnerReview);
        }
        ModelEnricher.enrich(model);
    }

}
