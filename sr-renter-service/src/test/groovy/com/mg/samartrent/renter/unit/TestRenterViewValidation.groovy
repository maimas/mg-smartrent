package com.mg.samartrent.renter.unit

import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.domain.models.RenterReview
import com.mg.smartrent.domain.models.RenterView
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.service.ExternalUserService
import com.mg.smartrent.renter.service.RenterReviewService
import com.mg.smartrent.renter.service.RenterService
import com.mg.smartrent.renter.service.RenterViewsService
import org.junit.Assert
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Specification
import spock.lang.Unroll

import static com.mg.samartrent.renter.TestUtils.generateRenterReview
import static com.mg.samartrent.renter.TestUtils.generateRenterView
import static org.apache.commons.lang.RandomStringUtils.random
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic
import static org.mockito.Mockito.when

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestRenterViewValidation extends Specification {

    @Autowired
    private RenterViewsService renterViewsService

    @Unroll
    def "test: renter view constraint for #field = #value"() {

        when: "saving model with a new test value"
        BeanWrapperImpl beanUtilsWrapper = new BeanWrapperImpl(model)
        beanUtilsWrapper.setPropertyValue(field, value)


        then: "expectations are meet"
        try {
            RenterView dbModel = renterViewsService.save(model)
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
        model                | field       | value        | expectedValue | expectException | checkContains | errorMsg
        generateRenterView() | 'userTID'   | null         | "unknown"     | false           | false         | null
        generateRenterView() | 'userTID'   | ""           | null          | true            | false         | "Field [userTID], value [], reason [size must be between 1 and 100]"
        generateRenterView() | 'userTID'   | random(1000) | null          | true            | true          | "[size must be between 1 and 100]"
        generateRenterView() | 'userTID'   | "TEST"       | "TEST"        | false           | false         | null

        generateRenterView() | 'renterTID' | null         | null          | true            | false         | "Field [renterTID], value [null], reason [must not be null]"
        generateRenterView() | 'renterTID' | ""           | null          | true            | false         | "Field [renterTID], value [], reason [size must be between 1 and 100]"
        generateRenterView() | 'renterTID' | random(1000) | null          | true            | true          | "[size must be between 1 and 100]"
        generateRenterView() | 'renterTID' | "LName"      | "LName"       | false           | false         | null

    }

}
