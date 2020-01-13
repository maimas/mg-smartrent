package com.mg.smartrent.property.service;

import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.models.PropertyListing;
import com.mg.smartrent.domain.validation.ModelBusinessValidationException;
import com.mg.smartrent.domain.validation.ModelValidationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.enrich;
import static com.mg.smartrent.domain.validation.ModelValidator.validate;

@Service
@Validated
public class PropertyListingService {


    private UserService userService;
    private PropertyService propertyService;
    private QueryService<PropertyListing> queryService;

    public PropertyListingService(QueryService<PropertyListing> queryService,
                                  PropertyService propertyService,
                                  UserService userService) {
        this.propertyService = propertyService;
        this.queryService = queryService;
        this.userService = userService;
    }


    public PropertyListing save(@NotNull PropertyListing listing) throws ModelValidationException {

        if (propertyService.findByTrackingId(listing.getPropertyTID()) == null) {
            throw new ModelBusinessValidationException("Listing could not be saved. Property not found.");
        }

        if (!userService.userExists(listing.getUserTID())) {
            throw new ModelBusinessValidationException("Listing could not be saved. User not found, UserTID = " + listing.getUserTID());
        }

        enrich(listing);
        validate(listing);
        return queryService.save(listing);
    }

    public PropertyListing publish(String trackingId, boolean listed) throws ModelValidationException {
        PropertyListing listing = findByTrackingId(trackingId);

        if (listing == null) {
            throw new ModelBusinessValidationException("Listing not found.");
        }
        listing.setListed(listed);
        save(listing);
        return listing;
    }


    public PropertyListing findByTrackingId(@NotNull @NotBlank String trackingId) {
        List<PropertyListing> listings = queryService.findAllBy("trackingId", trackingId, PropertyListing.class);

        return (listings != null && !listings.isEmpty()) ? listings.get(0) : null;
    }


    public List<PropertyListing> findByPropertyTID(@NotNull @NotBlank String propertyTID) {
        return queryService.findAllBy("propertyTID", propertyTID, PropertyListing.class);
    }

    public List<PropertyListing> findByUserTID(@NotNull @NotBlank String userTID) {
        return queryService.findAllBy("userTID", userTID, PropertyListing.class);
    }

}
