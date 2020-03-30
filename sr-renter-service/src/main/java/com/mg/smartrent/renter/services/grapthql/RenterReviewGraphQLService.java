package com.mg.smartrent.renter.services.grapthql;


import com.mg.smartrent.domain.models.RenterReview;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.renter.services.RenterReviewService;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
@GraphQLApi
public class RenterReviewGraphQLService {

    private static final Logger log = LogManager.getLogger(RenterReviewGraphQLService.class);

    private RenterReviewService renterReviewService;


    public RenterReviewGraphQLService(RenterReviewService renterReviewService) {
        this.renterReviewService = renterReviewService;
    }

    @GraphQLMutation
    public RenterReview create(@NotNull RenterReview model) throws ModelValidationException {
        return renterReviewService.save(model);
    }

}
