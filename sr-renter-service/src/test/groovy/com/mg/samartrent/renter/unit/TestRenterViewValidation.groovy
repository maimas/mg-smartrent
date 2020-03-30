package com.mg.samartrent.renter.unit


import com.mg.smartrent.domain.models.RenterView
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.services.RenterViewsService
import org.junit.Assert
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import static com.mg.samartrent.renter.TestUtils.generateRenterView
import static org.apache.commons.lang.RandomStringUtils.random

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
        generateRenterView() | 'userId'   | null         | "unknown"     | false           | false         | null
        generateRenterView() | 'userId'   | ""           | null          | true            | false         | 'Field "userId" has an invalid value value "". [size must be between 1 and 100]'
        generateRenterView() | 'userId'   | random(1000) | null          | true            | true          | "[size must be between 1 and 100]"
        generateRenterView() | 'userId'   | "TEST"       | "TEST"        | false           | false         | null

        generateRenterView() | 'renterId' | null         | null          | true            | false         | 'Field "renterId" has an invalid value value "null". [must not be null]'
        generateRenterView() | 'renterId' | ""           | null          | true            | false         | 'Field "renterId" has an invalid value value "". [size must be between 1 and 100]'
        generateRenterView() | 'renterId' | random(1000) | null          | true            | true          | "[size must be between 1 and 100]"
        generateRenterView() | 'renterId' | "LName"      | "LName"       | false           | false         | null

    }

}
