package com.mg.smartrent.renter.resource;


import com.mg.smartrent.domain.models.Renter;
import com.mg.smartrent.domain.models.RenterReview;
import com.mg.smartrent.domain.models.RenterView;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.renter.service.RenterReviewService;
import com.mg.smartrent.renter.service.RenterService;
import com.mg.smartrent.renter.service.RenterViewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/renters")
public class RenterRestController {

    private final RenterService renterService;
    private final RenterReviewService reviewService;
    private final RenterViewsService viewsService;

    public RenterRestController(RenterService renterService, RenterReviewService reviewService, RenterViewsService viewsService) {
        this.renterService = renterService;
        this.reviewService = reviewService;
        this.viewsService = viewsService;
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

    @PostMapping("/{renterTID}/views")
    public ResponseEntity saveRenterView(@PathVariable String renterTID, @RequestBody RenterView renterView) throws ModelValidationException {
        viewsService.save(renterView);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{renterTID}/views")
    public ResponseEntity<Long> getRenterViewsCount(@PathVariable String renterTID) {
        return new ResponseEntity<>(viewsService.count(renterTID), HttpStatus.OK);
    }

}
