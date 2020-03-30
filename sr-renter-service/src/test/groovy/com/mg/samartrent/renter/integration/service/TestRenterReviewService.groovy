package com.mg.samartrent.renter.integration.service


import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.domain.models.RenterReview
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.services.ExternalUserService
import com.mg.smartrent.renter.services.RenterReviewService
import com.mg.smartrent.renter.services.RenterService
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
        dbReview.getId() != null
        dbReview.getCreatedDate() != null
        dbReview.getModifiedDate() != null
        dbReview.getUserId() == review.getUserId()
        dbReview.getRenterId() == review.getRenterId()
        dbReview.getRating() == review.getRating()
        dbReview.getReview() == review.getReview()
    }

    def "test: find all by renter Id"() {
        setup:
        RenterReview review = generateRenterReview()
        mockServicesFor(review)
        renterReviewService.save(review)

        when:
        List<RenterReview> reviews = renterReviewService.findByRenterId(review.getRenterId())

        then:
        reviews != null
        reviews.size() == 1
    }


    def "test: save review with in-existent userId"() {
        setup:
        RenterReview review = generateRenterReview()
        MockitoAnnotations.initMocks(this)
        Mockito.when(renterService.findById(review.getRenterId())).thenReturn(new Renter())

        when: "saving"
        renterReviewService.save(review)

        then: "exception is thrown"
        RuntimeException e = thrown()
        e.getMessage() == "Renter Review could not be saved. User with Id ${review.userId} not found."
    }

    def "test: save review with in-existent renterId"() {
        setup:
        RenterReview review = generateRenterReview()
        MockitoAnnotations.initMocks(this)
        Mockito.when(userService.userExists(review.getUserId())).thenReturn(true)

        when: "saving"
        renterReviewService.save(review)

        then: "exception is thrown"
        RuntimeException e = thrown()
        e.getMessage() == "Renter Review could not be saved. Renter with Id ${review.renterId} not found."
    }


    private mockServicesFor(RenterReview review) {
        MockitoAnnotations.initMocks(this)
        Mockito.when(userService.userExists(review.getUserId())).thenReturn(true)
        Mockito.when(renterService.findById(review.getRenterId())).thenReturn(new Renter())
    }

}
