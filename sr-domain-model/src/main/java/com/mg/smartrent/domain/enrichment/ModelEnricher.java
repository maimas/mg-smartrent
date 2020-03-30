package com.mg.smartrent.domain.enrichment;

import com.mg.smartrent.domain.models.BizItem;

import java.util.Date;

public class ModelEnricher {

    public static void enrich(BizItem model) {
        if (model.getCreatedDate() == null) {
            model.setCreatedDate(new Date(System.currentTimeMillis()));
        }
        model.setModifiedDate(new Date(System.currentTimeMillis()));
    }
}
