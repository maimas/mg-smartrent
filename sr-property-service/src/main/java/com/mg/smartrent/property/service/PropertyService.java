package com.mg.smartrent.property.service;


import com.mg.persistence.service.nosql.MongoQueryService;
import com.mg.smartrent.domain.models.Property;
import com.mg.smartrent.domain.validation.ModelValidationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mg.smartrent.domain.enrichment.ModelEnricher.enrich;
import static com.mg.smartrent.domain.validation.ModelValidator.validate;

@Service
public class PropertyService {

    private MongoQueryService<Property> queryService;


    public PropertyService(MongoQueryService<Property> queryService) {
        this.queryService = queryService;
    }


    public Property save(Property model) throws ModelValidationException {
        enrich(model);
        validate(model);
        return queryService.save(model);
    }


    public Property findByTrackingId(String trackingId) {
        List<Property> propertyList = queryService.findAllBy("trackingId", trackingId, Property.class);
        return (propertyList == null || propertyList.isEmpty()) ? null : propertyList.get(0);
    }

    public List<Property> findByUserTID(String userTID) {
        return queryService.findAllBy("userTID", userTID, Property.class);
    }
}
