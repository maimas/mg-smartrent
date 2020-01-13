package com.mg.smartrent.renter.resource;


import com.mg.smartrent.domain.models.Renter;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.renter.service.RenterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/renters")
public class RenterRestController {

    private final RenterService renterService;

    public RenterRestController(RenterService renterService) {
        this.renterService = renterService;
    }


    @PostMapping
    public ResponseEntity saveProperty(@RequestBody Renter renter) throws ModelValidationException {
        renterService.save(renter);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(params = "email")
    public ResponseEntity getRenterByEmail(@RequestParam String email) {
        return new ResponseEntity<>(renterService.findByEmail(email), HttpStatus.OK);
    }




}
