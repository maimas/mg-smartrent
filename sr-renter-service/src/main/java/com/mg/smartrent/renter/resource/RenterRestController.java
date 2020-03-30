package com.mg.smartrent.renter.resource;


import com.mg.smartrent.domain.models.BizItem;
import com.mg.smartrent.domain.models.Renter;
import com.mg.smartrent.domain.models.RenterReview;
import com.mg.smartrent.domain.models.RenterView;
import com.mg.smartrent.domain.validation.ModelValidationException;
import com.mg.smartrent.renter.services.RenterReviewService;
import com.mg.smartrent.renter.services.RenterService;
import com.mg.smartrent.renter.services.RenterViewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<HashMap<String, Object>> saveRenter(@RequestBody Renter renter) throws ModelValidationException {
        renter = renterService.save(renter);
        HashMap<String, Object> map = new HashMap<>();
        map.put(BizItem.Fields.id, renter.getId());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping(params = "email")
    public ResponseEntity<Renter> getRenterByEmail(@RequestParam String email) {
        return new ResponseEntity<>(renterService.findByEmail(email), HttpStatus.OK);
    }

    @PostMapping("/{renterId}/reviews")
    public ResponseEntity<HashMap<String, Object>> saveRenterReview(@PathVariable String renterId, @RequestBody RenterReview renterReview) throws ModelValidationException {
        renterReview = reviewService.save(renterReview);
        HashMap<String, Object> map = new HashMap<>();
        map.put(BizItem.Fields.id, renterReview.getId());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/{renterId}/reviews")
    public ResponseEntity<List<RenterReview>> getRenterReviews(@PathVariable String renterId) {
        return new ResponseEntity<>(reviewService.findByRenterId(renterId), HttpStatus.OK);
    }

    @PostMapping("/{renterId}/views")
    public ResponseEntity<HashMap<String, Object>> saveRenterView(@PathVariable String renterId, @RequestBody RenterView renterView) throws ModelValidationException {
        renterView = viewsService.save(renterView);
        HashMap<String, Object> map = new HashMap<>();
        map.put(BizItem.Fields.id, renterView.getId());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/{renterId}/views/count")
    public ResponseEntity<Long> getRenterViewsCount(@PathVariable String renterId) {
        return new ResponseEntity<>(viewsService.count(renterId), HttpStatus.OK);
    }

}
