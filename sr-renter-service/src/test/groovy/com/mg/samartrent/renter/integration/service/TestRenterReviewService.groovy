package com.mg.samartrent.renter.integration.service


import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.domain.models.RenterReview
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.service.ExternalUserService
import com.mg.smartrent.renter.service.RenterReviewService
import com.mg.smartrent.renter.service.RenterService
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

import static com.mg.samartrent.renter.TestUtils.generateRenterReview

@SpringBootTest(classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestRenterReviewService extends IntegrationTestsSetup {

    @MockBean
    private ExternalUserService userService
    @MockBean
    private RenterService renterService

    @Autowired
    @InjectMocks
    private RenterReviewService renterReviewService

    static boolean initialized

    def setup() {
        if (!initialized) {
            purgeCollection(RenterReview.class)
            initialized = true
        }
    }


    def "test: create renter review"() {

        setup:
        RenterReview review = generateRenterReview()
        mockServicesFor(review)

        when: "saving"
        def dbReview = renterReviewService.save(review)

        then: "successfully saved"
        dbReview.getTrackingId() != null
        dbReview.getCreatedDate() != null
        dbReview.getModifiedDate() != null
        dbReview.getUserTID() == review.getUserTID()
        dbReview.getRenterTID() == review.getRenterTID()
        dbReview.getRating() == review.getRating()
        dbReview.getReview() == review.getReview()
    }

    def "test: find all by renter TID"() {
        setup:
        RenterReview review = generateRenterReview()
        mockServicesFor(review)
        renterReviewService.save(review)

        when:
        List<RenterReview> reviews = renterReviewService.findByRenterTID(review.getRenterTID())

        then:
        reviews != null
        reviews.size() == 1
    }


    def "test: save review with in-existent userTID"() {
        setup:
        RenterReview review = generateRenterReview()
        MockitoAnnotations.initMocks(this)
        Mockito.when(renterService.findByTrackingId(review.getRenterTID())).thenReturn(new Renter())

        when: "saving"
        renterReviewService.save(review)

        then: "exception is thrown"
        RuntimeException e = thrown()
        e.getMessage() == "Renter Review could not be saved. User with TID ${review.userTID} not found."
    }

    def "test: save review with in-existent renterTID"() {
        setup:
        RenterReview review = generateRenterReview()
        MockitoAnnotations.initMocks(this)
        Mockito.when(userService.userExists(review.getUserTID())).thenReturn(true)

        when: "saving"
        renterReviewService.save(review)

        then: "exception is thrown"
        RuntimeException e = thrown()
        e.getMessage() == "Renter Review could not be saved. Renter with TID ${review.userTID} not found."
    }


    private mockServicesFor(RenterReview review) {
        MockitoAnnotations.initMocks(this)
        Mockito.when(userService.userExists(review.getUserTID())).thenReturn(true)
        Mockito.when(renterService.findByTrackingId(review.getRenterTID())).thenReturn(new Renter())
    }

}
