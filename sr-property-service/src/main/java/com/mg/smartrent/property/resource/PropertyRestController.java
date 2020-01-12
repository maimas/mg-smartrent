package com.mg.smartrent.property.resource;


import com.mg.smartrent.domain.models.Property;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.property.service.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/properties")
public class PropertyRestController {

    private final PropertyService propertyService;

    public PropertyRestController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }


    @PostMapping
    public ResponseEntity saveProperty(@RequestBody Property property) throws ModelValidationException {
        propertyService.save(property);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(params = "userTID")
    public ResponseEntity<List<Property>> getPropertyByUserTID(@RequestParam String userTID) {
        return new ResponseEntity<>(propertyService.findByUserTID(userTID), HttpStatus.OK);
    }

    @GetMapping(params = "trackingId")
    public ResponseEntity<Property> getPropertyByTrackingId(@RequestParam String trackingId) {
        return new ResponseEntity<>(propertyService.findByTrackingId(trackingId), HttpStatus.OK);
    }



}
