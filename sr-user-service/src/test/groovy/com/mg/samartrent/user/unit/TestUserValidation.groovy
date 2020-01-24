package com.mg.samartrent.user.unit

import com.mg.persistence.service.QueryService
import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.service.UserService
import org.junit.Assert
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.mg.samartrent.user.ModelBuilder.generateUser
import static java.lang.System.currentTimeMillis
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic
import static org.mockito.Mockito.when

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestUserValidation extends Specification {

    @Mock
    private QueryService<User> queryService
    @Autowired
    @InjectMocks
    private UserService userService

    @Shared
    def longString = randomAlphabetic(1000)

    @Unroll
    def "test: user constraints for #field = #testValue"() {

        setup: "mock db call and user exists call"
        MockitoAnnotations.initMocks(this)
        when(queryService.save(model)).thenReturn(model)//mock db call

        when: "saving model with a new test value"
        BeanWrapperImpl beanUtilsWrapper = new BeanWrapperImpl(model)
        beanUtilsWrapper.setPropertyValue(field, testValue)

        then: "expectations are meet"
        try {
            User dbModel = userService.save(model)
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
        model          | field         | testValue                   | expectedValue               | expectException | checkContains | errorMsg
        generateUser() | 'firstName'   | null                        | null                        | true            | false         | "Field [firstName], value [null], reason [must not be null]"
        generateUser() | 'firstName'   | ""                          | null                        | true            | false         | "Field [firstName], value [], reason [size must be between 1 and 100]"
        generateUser() | 'firstName'   | longString                  | null                        | true            | true          | "[size must be between 1 and 100]"
        generateUser() | 'firstName'   | "FName"                     | "FName"                     | false           | false         | null

        generateUser() | 'lastName'    | null                        | null                        | true            | false         | "Field [lastName], value [null], reason [must not be null]"
        generateUser() | 'lastName'    | ""                          | null                        | true            | false         | "Field [lastName], value [], reason [size must be between 1 and 100]"
        generateUser() | 'lastName'    | longString                  | null                        | true            | true          | "[size must be between 1 and 100]"
        generateUser() | 'lastName'    | "LName"                     | "LName"                     | false           | false         | null

        generateUser() | 'email'       | null                        | null                        | true            | false         | "Field [email], value [null], reason [must not be null]"
        generateUser() | 'email'       | ""                          | null                        | true            | false         | "Field [email], value [], reason [size must be between 1 and 100]"
        generateUser() | 'email'       | "@test.com"                 | null                        | true            | false         | "Field [email], value [@test.com], reason [must be a well-formed email address]"
        generateUser() | 'email'       | longString                  | null                        | true            | true          | "reason [size must be between 1 and 100]"
        generateUser() | 'email'       | "test.test@test.com"        | "test.test@test.com"        | false           | false         | null

        generateUser() | 'dateOfBirth' | new Date(10000000001)       | new Date(10000000001)       | false           | false         | null
        generateUser() | 'dateOfBirth' | null                        | null                        | true            | false         | "Field [dateOfBirth], value [null], reason [must not be null]"

        generateUser() | 'gender'      | null                        | null                        | true            | false         | "Field [gender], value [null], reason [must not be null]"
        generateUser() | 'gender'      | ""                          | null                        | true            | false         | "Field [gender], value [], reason [must be any of enum class com.mg.smartrent.domain.enums.EnGender]"
        generateUser() | 'gender'      | "notValid"                  | null                        | true            | false         | "Field [gender], value [notValid], reason [must be any of enum class com.mg.smartrent.domain.enums.EnGender]"
        generateUser() | 'gender'      | EnGender.Male.name()        | EnGender.Male.name()        | false           | false         | null
        generateUser() | 'gender'      | EnGender.Female.name()      | EnGender.Female.name()      | false           | false         | null
        generateUser() | 'gender'      | EnGender.Unknown.name()     | EnGender.Unknown.name()     | false           | false         | null


        generateUser() | 'password'    | null                        | null                        | true            | false         | "User could not be saved. Password not specified."
        generateUser() | 'password'    | ""                          | null                        | true            | false         | "User could not be saved. Password not specified."
//        generateUser() | 'password'  | longString                  | null                        | true            | false         | ""
        //covered in integration suite
//        generateUser() | 'password'  | "@%^^#%@!@sdasda"                         | false           | false         | null
//        generateUser() | 'password'  | "12341234"                                | false           | false         | null

        generateUser() | 'status'      | EnUserStatus.Active.name()  | EnUserStatus.Pending.name() | false           | false         | null
        generateUser() | 'status'      | EnUserStatus.Pending.name() | EnUserStatus.Pending.name() | false           | false         | null
        generateUser() | 'status'      | null                        | EnUserStatus.Pending.name() | false           | false         | null
        generateUser() | 'status'      | ""                          | EnUserStatus.Pending.name() | false           | false         | null
        generateUser() | 'status'      | "Invalid"                   | EnUserStatus.Pending.name() | false           | false         | null


        generateUser() | 'enabled'     | true                        | true                        | false           | false         | null
        generateUser() | 'enabled'     | false                       | false                       | false           | false         | null
    }

}
