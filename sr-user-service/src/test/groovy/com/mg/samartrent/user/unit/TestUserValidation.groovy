package com.mg.samartrent.user.unit

import com.mg.persistence.service.QueryService
import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.service.UserService
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
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
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
    def "test: user constraints for #FIELD = #TEST_VALUE"() {

        setup: "mock db call and user exists call"
        MockitoAnnotations.initMocks(this)
        when(queryService.save(MODEL)).thenReturn(MODEL)//mock db call

        when: "saving model with a new test value"
        BeanWrapperImpl beanUtilsWrapper = new BeanWrapperImpl(MODEL)
        beanUtilsWrapper.setPropertyValue(FIELD, TEST_VALUE)

        then: "expectations are meet"
        try {
            User dbModel = userService.create(MODEL)
            beanUtilsWrapper = new BeanWrapperImpl(dbModel)
            assertEquals(EXPECTED_VALUE, beanUtilsWrapper.getPropertyValue(FIELD))
            assertEquals(EXPECT_EXCEPTION, false)
            assertEquals(13, beanUtilsWrapper.getProperties().size())//13 properties

        } catch (Exception e) {
            if (!CHECK_ERROR_CONTAINS) {
                assertEquals(ERROR_MSG, e.getMessage().trim())
            } else {
                def explanation = "ACTUAL: ${e.getMessage().trim()}\nShould countain\nEXPECTED: $ERROR_MSG"
                assertTrue(explanation, e.getMessage().trim().contains(ERROR_MSG))
            }
        }

        where:
        MODEL          | FIELD         | TEST_VALUE            | EXPECTED_VALUE        | EXPECT_EXCEPTION | CHECK_ERROR_CONTAINS | ERROR_MSG
        generateUser() | 'firstName'   | null                  | null                  | true             | false                | 'Field "firstName" has an invalid value value "null". [must not be null]'
        generateUser() | 'firstName'   | ""                    | null                  | true             | false                | 'Field "firstName" has an invalid value value "". [size must be between 1 and 100]'
        generateUser() | 'firstName'   | longString            | null                  | true             | true                 | '[size must be between 1 and 100]'
        generateUser() | 'firstName'   | "FName"               | "FName"               | false            | false                | null

        generateUser() | 'lastName'    | null                  | null                  | true             | false                | 'Field "lastName" has an invalid value value "null". [must not be null]'
        generateUser() | 'lastName'    | ""                    | null                  | true             | false                | 'Field "lastName" has an invalid value value "". [size must be between 1 and 100]'
        generateUser() | 'lastName'    | longString            | null                  | true             | true                 | '[size must be between 1 and 100]'
        generateUser() | 'lastName'    | "LName"               | "LName"               | false            | false                | null

        generateUser() | 'email'       | null                  | null                  | true             | false                | 'Field "email" has an invalid value value "null". [must not be null]'
        generateUser() | 'email'       | ""                    | null                  | true             | false                | 'Field "email" has an invalid value value "". [size must be between 1 and 100]'
        generateUser() | 'email'       | "@test.com"           | null                  | true             | false                | 'Field "email" has an invalid value value "@test.com". [must be a well-formed email address]'
        generateUser() | 'email'       | longString            | null                  | true             | true                 | '[size must be between 1 and 100]'
        generateUser() | 'email'       | "test.test@test.com"  | "test.test@test.com"  | false            | false                | null

        generateUser() | 'dateOfBirth' | new Date(10000000001) | new Date(10000000001) | false            | false                | null
        generateUser() | 'dateOfBirth' | null                  | null                  | true             | false                | 'Field "dateOfBirth" has an invalid value value "null". [must not be null]'

        generateUser() | 'gender'      | null                  | null                  | true             | false                | 'Field "gender" has an invalid value value "null". [must not be null]'
        generateUser() | 'gender'      | ""                    | null                  | true             | false                | 'Field "gender" has an invalid value value "null". [must not be null]'
//        generateUser() | 'gender'      | "notValid"            | null                  | true             | false                | 'Field "gender" has an invalid value value "notValid". [must be any of enum class com.mg.smartrent.domain.enums.EnGender]'
        generateUser() | 'gender'      | EnGender.Male         | EnGender.Male         | false            | false                | null
        generateUser() | 'gender'      | EnGender.Female       | EnGender.Female       | false            | false                | null
        generateUser() | 'gender'      | EnGender.Unknown      | EnGender.Unknown      | false            | false                | null

        generateUser() | 'password'    | null                  | null                  | true             | false                | 'Password not specified.'
        generateUser() | 'password'    | ""                    | null                  | true             | false                | 'Password not specified.'

        generateUser() | 'status'      | EnUserStatus.Active   | EnUserStatus.Pending  | false            | false                | null
        generateUser() | 'status'      | EnUserStatus.Pending  | EnUserStatus.Pending  | false            | false                | null
        generateUser() | 'status'      | null                  | EnUserStatus.Pending  | false            | false                | null
        generateUser() | 'status'      | ""                    | EnUserStatus.Pending  | false            | false                | null

        generateUser() | 'enabled'     | false                 | false                 | false            | false                | null
    }

}
