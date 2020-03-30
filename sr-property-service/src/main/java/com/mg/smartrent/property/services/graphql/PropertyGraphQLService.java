package com.mg.smartrent.property.services.graphql;


import com.mg.smartrent.domain.models.Property;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.property.services.PropertyService;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@GraphQLApi
public class PropertyGraphQLService {

    private static final Logger log = LogManager.getLogger(PropertyGraphQLService.class);

    private PropertyService propertyService;

    public PropertyGraphQLService(PropertyService propertyService) {
        this.propertyService = propertyService;
    }


    @GraphQLMutation
    public Property create(@NotNull Property model) throws ModelValidationException {
        return propertyService.save(model);
    }


    @GraphQLQuery
    public Property findById(@NotNull @NotBlank String id) {
        return propertyService.findById(id);
    }

    @GraphQLQuery
    public List<Property> findByUserId(@NotNull @NotBlank String userId) {
        return propertyService.findByUserId(userId);
    }


}
