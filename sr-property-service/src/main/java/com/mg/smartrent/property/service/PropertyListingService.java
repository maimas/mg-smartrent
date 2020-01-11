package com.mg.smartrent.property.service;

import com.mg.persistence.service.nosql.MongoQueryService;
import com.mg.smartrent.domain.enrichment.ModelEnricher;
import com.mg.smartrent.domain.models.PropertyListing;
import com.mg.smartrent.domain.validation.ModelBusinessValidationException;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.domain.validation.ModelValidator;
import com.mg.smartrent.property.config.RestServicesConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.enrich;
import static com.mg.smartrent.domain.validation.ModelValidator.validate;

@Service
public class PropertyListingService {

    private RestTemplate restTemplate;
    private PropertyService propertyService;
    private RestServicesConfig restServicesConfig;
    private MongoQueryService<PropertyListing> queryService;

    public PropertyListingService(MongoQueryService<PropertyListing> queryService,
                                  PropertyService propertyService,
                                  RestTemplate restTemplate,
                                  RestServicesConfig restServicesConfig) {
        this.propertyService = propertyService;
        this.queryService = queryService;
        this.restTemplate = restTemplate;
        this.restServicesConfig = restServicesConfig;
    }


    public PropertyListing save(PropertyListing listing) throws ModelBusinessValidationException, ModelValidationException {

        if (listing == null) {
            throw new ModelBusinessValidationException("Listing could not be saved. Invalid Listing.");
        }

        if (propertyService.findByTrackingId(listing.getPropertyTID()) == null) {
            throw new ModelBusinessValidationException("Listing could not be saved. Property not found.");
        }

        if (!userExists(listing.getUserTID())) {
            throw new ModelBusinessValidationException("Listing could not be saved. User not found, UserTID = " + listing.getUserTID());
        }

        enrich(listing);
        validate(listing);
        return queryService.save(listing);
    }


    public List<PropertyListing> findByPropertyTID(String propertyTID) {
        return queryService.findAllBy("propertyTID", propertyTID, PropertyListing.class);
    }

    public List<PropertyListing> findByUserTID(String userTID) {
        return queryService.findAllBy("userTID", userTID, PropertyListing.class);
    }


    private boolean userExists(String userTID) {
        URI uri = URI.create(restServicesConfig.getUsersServiceURI() + "/rest/users/exists/trackingId=" + userTID);
        ResponseEntity response = restTemplate.getForEntity(uri, ResponseEntity.class);

        return response.getStatusCode() == HttpStatus.OK;
    }
}
