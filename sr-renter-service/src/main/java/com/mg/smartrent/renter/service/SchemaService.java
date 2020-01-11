package com.mg.smartrent.renter.service;

import com.mg.persistence.domain.bizitem.service.BizItemService;
import com.mg.persistence.exceptions.BizItemSchemaValidationException;
import com.mg.persistence.exceptions.ValidationSchemaNotFoundException;
import com.mg.persistence.services.SchemaInitializerService;
import com.mg.smartrent.renter.domain.Renter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SchemaService {

    @Value("${domain.schemas}")
    private String schemaDir;

    private SchemaInitializerService initializerService;

    public SchemaService(SchemaInitializerService initializerService) {
        this.initializerService = initializerService;
    }

    public void initSchema() {
        initializerService.initMetadata(schemaDir);
    }
}
