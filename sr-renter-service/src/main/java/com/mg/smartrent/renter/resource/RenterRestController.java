package com.mg.smartrent.renter.resource;


import com.mg.smartrent.domain.models.Renter;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.renter.service.RenterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest/renters")
public class RenterRestController {

    private final RenterService renterService;

    public RenterRestController(RenterService renterService) {
        this.renterService = renterService;
    }


//    @PostMapping
//    public ResponseEntity saveProperty(@RequestBody Renter renter) throws ModelValidationException {
//        renterService.save(renter);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @GetMapping(params = "email")
    public ResponseEntity<Renter> getRenterByEmail(@RequestParam String email) throws ModelValidationException {
        return new ResponseEntity<>(renterService.findByEmail(email, true), HttpStatus.OK);
    }

}
