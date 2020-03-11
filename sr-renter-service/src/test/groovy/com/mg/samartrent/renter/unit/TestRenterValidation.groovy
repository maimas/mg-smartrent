package com.mg.samartrent.renter.unit

import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.service.RenterService
import org.junit.Assert
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.mg.samartrent.renter.TestUtils.generateRenter
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestRenterValidation extends Specification {

    @Autowired
    private RenterService renterService

    @Shared
    def longString = randomAlphabetic(1000)

    @Unroll
    def "test: renter constraint for #field = #value"() {

        when: "saving model with a new test value"
        BeanWrapperImpl beanUtilsWrapper = new BeanWrapperImpl(model)
        beanUtilsWrapper.setPropertyValue(field, value)

        then: "expectations are meet"
        try {
            Renter dbModel = renterService.save(model)
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
        model            | field         | value                   | expectedValue           | expectException | checkContains | errorMsg
        generateRenter() | 'firstName'   | null                    | null                    | true            | false         | "Field [firstName], value [null], reason [must not be null]"
        generateRenter() | 'firstName'   | ""                      | null                    | true            | false         | "Field [firstName], value [], reason [size must be between 1 and 100]"
        generateRenter() | 'firstName'   | longString              | null                    | true            | true          | "[size must be between 1 and 100]"
        generateRenter() | 'firstName'   | "FName"                 | "FName"                 | false           | false         | null

        generateRenter() | 'lastName'    | null                    | null                    | true            | false         | "Field [lastName], value [null], reason [must not be null]"
        generateRenter() | 'lastName'    | ""                      | null                    | true            | false         | "Field [lastName], value [], reason [size must be between 1 and 100]"
        generateRenter() | 'lastName'    | longString              | null                    | true            | true          | "[size must be between 1 and 100]"
        generateRenter() | 'lastName'    | "LName"                 | "LName"                 | false           | false         | null

        generateRenter() | 'dateOfBirth' | new Date(10000000010)   | new Date(10000000010)   | false           | false         | null
        generateRenter() | 'dateOfBirth' | null                    | null                    | false           | false         | null

        generateRenter() | 'phoneNumber' | null                    | null                    | false           | false         | null
        generateRenter() | 'phoneNumber' | ""                      | null                    | true            | true          | "Field [phoneNumber], value [], reason [must be a valid phone number]"
        generateRenter() | 'phoneNumber' | "dasdasds"              | null                    | true            | false         | "Field [phoneNumber], value [dasdasds], reason [must be a valid phone number]"
        generateRenter() | 'phoneNumber' | "3252402021"            | "3252402021"            | false           | false         | null

        generateRenter() | 'gender'      | null                    | null                    | true            | false         | "Field [gender], value [null], reason [must not be null]"
        generateRenter() | 'gender'      | ""                      | null                    | true            | false         | "Field [gender], value [], reason [must be any of enum class com.mg.smartrent.domain.enums.EnGender]"
        generateRenter() | 'gender'      | "notValid"              | null                    | true            | false         | "Field [gender], value [notValid], reason [must be any of enum class com.mg.smartrent.domain.enums.EnGender]"
        generateRenter() | 'gender'      | EnGender.Male.name()    | EnGender.Male.name()    | false           | false         | null
        generateRenter() | 'gender'      | EnGender.Female.name()  | EnGender.Female.name()  | false           | false         | null
        generateRenter() | 'gender'      | EnGender.Unknown.name() | EnGender.Unknown.name() | false           | false         | null

        generateRenter() | 'email'       | null                    | null                    | true            | false         | "Field [email], value [null], reason [must not be null]"
        generateRenter() | 'email'       | ""                      | null                    | true            | false         | "Field [email], value [], reason [size must be between 1 and 100]"
        generateRenter() | 'email'       | "@test.com"             | null                    | true            | false         | "Field [email], value [@test.com], reason [must be a well-formed email address]"
        generateRenter() | 'email'       | longString              | null                    | true            | true          | "reason [must be a well-formed email address]"
        generateRenter() | 'email'       | "test.test@test.com"    | "test.test@test.com"    | false           | false         | null

    }

}
