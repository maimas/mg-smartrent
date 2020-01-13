package com.mg.smartrent.property.resource;


import com.mg.smartrent.domain.models.PropertyListing;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.property.service.PropertyListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/propertylistings")
public class PropertyListingRestController {

    private final PropertyListingService listingService;

    public PropertyListingRestController(PropertyListingService listingService) {
        this.listingService = listingService;
    }


    @PostMapping
    public ResponseEntity saveListing(@RequestBody PropertyListing listing) throws ModelValidationException {
        listingService.save(listing);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/{trackingId}", params = "publish")
    public ResponseEntity publishListing(@PathVariable String trackingId, @RequestParam boolean publish) throws ModelValidationException {
        listingService.publish(trackingId, publish);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<PropertyListing> getListingByTrackingId(@PathVariable String trackingId) {
        return new ResponseEntity<>(listingService.findByTrackingId(trackingId), HttpStatus.OK);
    }

    @GetMapping(params = "propertyTID")
    public ResponseEntity<List<PropertyListing>> getListingsByPropertyTID(@RequestParam String propertyTID) {
        return new ResponseEntity<>(listingService.findByPropertyTID(propertyTID), HttpStatus.OK);
    }

}
