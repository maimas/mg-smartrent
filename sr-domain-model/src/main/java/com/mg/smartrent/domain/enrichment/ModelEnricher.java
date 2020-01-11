package com.mg.smartrent.domain.enrichment;

import com.mg.smartrent.domain.models.BizItem;

import java.util.Date;

public class ModelEnricher {

    public static void enrich(BizItem model) {
        if (model.getCreatedDate() == null) {
            model.setCreatedDate(new Date(System.currentTimeMillis()));
        }

        if (model.getTrackingId() == null) {
            model.setTrackingId(TrackingIdGenerator.generateUnique());
        }

        if (model.getTrackingId() != null) {
            model.setTrackingId(model.getTrackingId().trim());
        }

        model.setModifiedDate(new Date(System.currentTimeMillis()));
    }
}
