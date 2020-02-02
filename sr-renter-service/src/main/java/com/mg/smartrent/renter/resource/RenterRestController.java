package com.mg.smartrent.renter.resource;


import com.mg.smartrent.domain.models.Renter;
import com.mg.smartrent.domain.models.RenterReview;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.renter.service.RenterReviewService;
import com.mg.smartrent.renter.service.RenterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/renters")
public class RenterRestController {

    private final RenterService renterService;
    private final RenterReviewService reviewService;

    public RenterRestController(RenterService renterService, RenterReviewService reviewService) {
        this.renterService = renterService;
        this.reviewService = reviewService;
    }


    @PostMapping
    public ResponseEntity saveRenter(@RequestBody Renter renter) throws ModelValidationException {
        renterService.save(renter);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(params = "email")
    public ResponseEntity<Renter> getRenterByEmail(@RequestParam String email) {
        return new ResponseEntity<>(renterService.findByEmail(email), HttpStatus.OK);
    }

    @PostMapping("/{renterTID}/reviews")
    public ResponseEntity saveRenterReview(@PathVariable String renterTID, @RequestBody RenterReview renterReview) throws ModelValidationException {
        reviewService.save(renterReview);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{renterTID}/reviews")
    public ResponseEntity<List<RenterReview>> getRenterReviews(@PathVariable String renterTID) {
        return new ResponseEntity<>(reviewService.findByRenterTID(renterTID), HttpStatus.OK);
    }

}
