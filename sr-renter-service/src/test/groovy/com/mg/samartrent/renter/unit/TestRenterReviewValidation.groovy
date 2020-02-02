package com.mg.samartrent.renter.unit

import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.domain.models.RenterReview
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.service.ExternalUserService
import com.mg.smartrent.renter.service.RenterReviewService
import com.mg.smartrent.renter.service.RenterService
import org.junit.Assert
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import static org.mockito.Mockito.when

import static com.mg.samartrent.renter.TestUtils.generateRenterReview
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestRenterReviewValidation extends Specification {

    @MockBean
    private ExternalUserService userService;

    @MockBean
    private RenterService renterService;

    @Autowired
    @InjectMocks
    private RenterReviewService renterReviewService

    static def longString = randomAlphabetic(10000)
    static def reviewMaxCapacity = randomAlphabetic(1000000)
    static def reviewMaxCapacityPlus1 = randomAlphabetic(1000001)

    @Unroll
    def "test: renter review constraint for #field = #value"() {

        when: "saving model with a new test value"
        BeanWrapperImpl beanUtilsWrapper = new BeanWrapperImpl(model)
        beanUtilsWrapper.setPropertyValue(field, value)
        //mock dependent services
        MockitoAnnotations.initMocks(this)
        when(userService.userExists(model.getUserTID())).thenReturn(true)
        when(renterService.findByTrackingId(model.getRenterTID())).thenReturn(new Renter())


        then: "expectations are meet"
        try {
            RenterReview dbModel = renterReviewService.save(model)
            beanUtilsWrapper = new BeanWrapperImpl(dbModel)
            Assert.assertEquals(expectedValue, beanUtilsWrapper.getPropertyValue(field))
            Assert.assertEquals(expectException, false)
            Assert.assertEquals(13, beanUtilsWrapper.getProperties().size())//13 properties

        } catch (Exception e) {
            if (!checkContains) {
                Assert.assertEquals(errorMsg, e.getMessage().trim())
            } else {
                def explanation = "ACTUAL: ${e.getMessage().trim()}\nShould countain\nEXPECTED: $errorMsg"
                Assert.assertTrue(explanation, e.getMessage().trim().contains(errorMsg))
            }
        }

        where:
        model                  | field       | value                  | expectedValue     | expectException | checkContains | errorMsg
        generateRenterReview() | 'userTID'   | null                   | null              | true            | false         | "Field [userTID], value [null], reason [must not be null]"
        generateRenterReview() | 'userTID'   | ""                     | null              | true            | false         | "Field [userTID], value [], reason [size must be between 1 and 100]"
        generateRenterReview() | 'userTID'   | longString             | null              | true            | true          | "[size must be between 1 and 100]"
        generateRenterReview() | 'userTID'   | "TEST"                 | "TEST"            | false           | false         | null

        generateRenterReview() | 'renterTID' | null                   | null              | true            | false         | "Field [renterTID], value [null], reason [must not be null]"
        generateRenterReview() | 'renterTID' | ""                     | null              | true            | false         | "Field [renterTID], value [], reason [size must be between 1 and 100]"
        generateRenterReview() | 'renterTID' | longString             | null              | true            | true          | "[size must be between 1 and 100]"
        generateRenterReview() | 'renterTID' | "LName"                | "LName"           | false           | false         | null

        generateRenterReview() | 'review'    | null                   | null              | true            | false         | "Field [review], value [null], reason [must not be null]"
        generateRenterReview() | 'review'    | ""                     | null              | true            | false         | "Field [review], value [], reason [size must be between 1 and 1000000]"
        generateRenterReview() | 'review'    | reviewMaxCapacityPlus1 | null              | true            | true          | "[size must be between 1 and 1000000]"
        generateRenterReview() | 'review'    | reviewMaxCapacity      | reviewMaxCapacity | false           | false         | null
        generateRenterReview() | 'review'    | "Test review"          | "Test review"     | false           | false         | null

        generateRenterReview() | 'rating'    | 0                      | null              | true            | false         | "Field [rating], value [0], reason [must be greater than or equal to 1]"
        generateRenterReview() | 'rating'    | -1                     | null              | true            | false         | "Field [rating], value [-1], reason [must be greater than or equal to 1]"
        generateRenterReview() | 'rating'    | 6                      | null              | true            | false         | "Field [rating], value [6], reason [must be less than or equal to 5]"
        generateRenterReview() | 'rating'    | 1                      | 1                 | false           | false         | null
        generateRenterReview() | 'rating'    | 2                      | 2                 | false           | false         | null
        generateRenterReview() | 'rating'    | 3                      | 3                 | false           | false         | null
        generateRenterReview() | 'rating'    | 4                      | 4                 | false           | false         | null
        generateRenterReview() | 'rating'    | 5                      | 5                 | false           | false         | null

    }

}
