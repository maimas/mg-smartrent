package com.mg.smartrent.renter.resource;

import com.mg.persistence.services.SchemaInitializerService;
import com.mg.smartrent.renter.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/renters/schema")
public class SchemaRestController {

    @Autowired
    private SchemaService schemaService;


    @PostMapping
    public ResponseEntity initSchema() {
        schemaService.initSchema();

        return new ResponseEntity(HttpStatus.OK);
    }
}
