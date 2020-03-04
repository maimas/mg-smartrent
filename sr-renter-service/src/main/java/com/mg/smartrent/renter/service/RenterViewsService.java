package com.mg.smartrent.renter.service;


import com.mg.persistence.service.QueryService;
import com.mg.smartrent.domain.enrichment.ModelEnricher;
import com.mg.smartrent.domain.models.RenterView;
import com.mg.smartrent.domain.validation.ModelValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import static com.mg.smartrent.domain.validation.ModelValidator.validate;

@Service
@Validated
public class RenterViewsService {

    private static final Logger log = LogManager.getLogger(RenterViewsService.class);

    private QueryService<RenterView> queryService;


    public RenterViewsService(QueryService<RenterView> queryService) {
        this.queryService = queryService;
    }


    public RenterView save(@NotNull RenterView model) throws ModelValidationException {
        enrich(model);
        validate(model);
        RenterView view = queryService.save(model);
        log.info("Renter view created. TrackingId = " + view.getTrackingId());
        return view;
    }

    public long count(@NotNull String renterTID) {
        Query query = new Query(Criteria.where("renterTID").is(renterTID));
        return queryService.count(query, RenterView.class);
    }

    private void enrich(RenterView view) {
        ModelEnricher.enrich(view);
        if (view.getUserTID() == null) {
            view.setUserTID("unknown");
        }
    }

}
