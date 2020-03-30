package com.mg.samartrent.renter.unit

import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.domain.models.RenterReview
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.services.ExternalUserService
import com.mg.smartrent.renter.services.RenterReviewService
import com.mg.smartrent.renter.services.RenterService
import org.junit.Assert
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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
        when(userService.userExists(model.getUserId())).thenReturn(true)
        when(renterService.findById(model.getRenterId())).thenReturn(new Renter())


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
        model                  | field      | value                  | expectedValue     | expectException | checkContains | errorMsg
        generateRenterReview() | 'userId'   | null                   | null              | true            | false         | 'Field "userId" has an invalid value value "null". [must not be null]'
        generateRenterReview() | 'userId'   | ""                     | null              | true            | false         | 'Field "userId" has an invalid value value "". [size must be between 1 and 100]'
        generateRenterReview() | 'userId'   | longString             | null              | true            | true          | "[size must be between 1 and 100]"
        generateRenterReview() | 'userId'   | "TEST"                 | "TEST"            | false           | false         | null

        generateRenterReview() | 'renterId' | null                   | null              | true            | false         | 'Field "renterId" has an invalid value value "null". [must not be null]'
        generateRenterReview() | 'renterId' | ""                     | null              | true            | false         | 'Field "renterId" has an invalid value value "". [size must be between 1 and 100]'
        generateRenterReview() | 'renterId' | longString             | null              | true            | true          | "[size must be between 1 and 100]"
        generateRenterReview() | 'renterId' | "LName"                | "LName"           | false           | false         | null

        generateRenterReview() | 'review'   | null                   | null              | true            | false         | 'Field "review" has an invalid value value "null". [must not be null]'
        generateRenterReview() | 'review'   | ""                     | null              | true            | false         | 'Field "review" has an invalid value value "". [size must be between 1 and 1000000]'
        generateRenterReview() | 'review'   | reviewMaxCapacityPlus1 | null              | true            | true          | "[size must be between 1 and 1000000]"
        generateRenterReview() | 'review'   | reviewMaxCapacity      | reviewMaxCapacity | false           | false         | null
        generateRenterReview() | 'review'   | "Test review"          | "Test review"     | false           | false         | null

        generateRenterReview() | 'rating'   | 0                      | null              | true            | false         | 'Field "rating" has an invalid value value "0". [must be greater than or equal to 1]'
        generateRenterReview() | 'rating'   | -1                     | null              | true            | false         | 'Field "rating" has an invalid value value "-1". [must be greater than or equal to 1]'
        generateRenterReview() | 'rating'   | 6                      | null              | true            | false         | 'Field "rating" has an invalid value value "6". [must be less than or equal to 5]'
        generateRenterReview() | 'rating'   | 1                      | 1                 | false           | false         | null
        generateRenterReview() | 'rating'   | 2                      | 2                 | false           | false         | null
        generateRenterReview() | 'rating'   | 3                      | 3                 | false           | false         | null
        generateRenterReview() | 'rating'   | 4                      | 4                 | false           | false         | null
        generateRenterReview() | 'rating'   | 5                      | 5                 | false           | false         | null

    }

}
