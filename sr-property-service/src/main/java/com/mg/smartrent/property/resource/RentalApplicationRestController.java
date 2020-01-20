package com.mg.smartrent.property.resource;


import com.mg.smartrent.domain.models.RentalApplication;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.property.service.RentalApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/rentalapplications")
public class RentalApplicationRestController {

    private final RentalApplicationService rentalApplicationService;

    public RentalApplicationRestController(RentalApplicationService rentalApplicationService) {
        this.rentalApplicationService = rentalApplicationService;
    }


    @PostMapping
    public ResponseEntity saveApplication(@RequestBody RentalApplication rentalApplication) throws ModelValidationException {
        rentalApplicationService.save(rentalApplication);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(params = "renterUserTID")
    public ResponseEntity<List<RentalApplication>> getApplicationsByRenterUserTID(@RequestParam String renterUserTID) {
        return new ResponseEntity<>(rentalApplicationService.findByRenterUserTID(renterUserTID), HttpStatus.OK);
    }

    @GetMapping(params = "propertyTID")
    public ResponseEntity<List<RentalApplication>> getApplicationsByPropertyUserTID(@RequestParam String propertyTID) {
        return new ResponseEntity<>(rentalApplicationService.findByPropertyTID(propertyTID), HttpStatus.OK);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<RentalApplication> getRentalApplicationByTrackingId(@PathVariable String trackingId) {
        return new ResponseEntity<>(rentalApplicationService.findByTrackingId(trackingId), HttpStatus.OK);
    }

}
