package com.mg.smartrent.renter.service;

import com.mg.persistence.domain.SystemFiled;
import com.mg.persistence.domain.bizitem.model.BizItemModel;
import com.mg.persistence.domain.bizitem.service.BizItemService;
import com.mg.persistence.exceptions.BizItemSchemaValidationException;
import com.mg.persistence.exceptions.ValidationSchemaNotFoundException;
import com.mg.smartrent.renter.domain.Renter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RenterService {

    private BizItemService itemService;

    public RenterService(BizItemService itemService) {
        this.itemService = itemService;
    }


    public Renter save(Renter renter) throws ValidationSchemaNotFoundException, BizItemSchemaValidationException {
        return (Renter) itemService.save(renter);
    }

    public Renter findByTrackingId(String trackingId) {
        List<BizItemModel> bizItemModels = itemService.find(SystemFiled.TrackingId, trackingId, Renter.class.getSimpleName());

        return (bizItemModels == null || bizItemModels.isEmpty()) ? null : (Renter) bizItemModels.get(0);
    }
}
