package com.mg.samartrent.property.integration.service


import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

import static com.mg.samartrent.property.ModelBuilder.generateUser

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestUserService extends IntegrationTestsSetup {


    @Autowired
    private UserService userService

    @Autowired
    private PasswordEncoder passwordEncoder
    static boolean initialized

    def setup() {
        if (!initialized) {
            purgeCollection(User.class)
            initialized = true
        }
    }

    static User dbUser

    def "test: create user"() {

        when: "saving a new user"
        dbUser = userService.save(generateUser())

        then: "successfully saved"
        dbUser.getTrackingId() != null
        dbUser.getCreatedDate() != null
        dbUser.getModifiedDate() != null
        dbUser.getFirstName() == "FName"
        dbUser.getLastName() == "LName"
        dbUser.getEmail() == "test.user@domain.com"
        dbUser.getStatus() == EnUserStatus.Active.name()
        dbUser.getPassword() != "12341234"
        passwordEncoder.matches("12341234", dbUser.getPassword())

    }

    def "test: find user by trackingId"() {
        when:
        dbUser = userService.save(generateUser())
        def user = userService.findByTrackingId(dbUser.getTrackingId())

        then:
        user != null

        when:
        user = userService.findByTrackingId("in-existent")

        then:
        user == null
    }

    @Unroll
    def "test: find renter by invalid trackingId #value"() {
        when:
        userService.findByTrackingId(value)

        then:
        ConstraintViolationException e = thrown()
        e.getMessage() == errorMessage

        where:
        value | errorMessage
        null  | "findByTrackingId.arg0: must not be null, findByTrackingId.arg0: must not be blank"
        ""    | "findByTrackingId.arg0: must not be blank"
    }

    def "test: find renter by email"() {
        when:
        dbUser = userService.save(generateUser())
        def user = userService.findByEmail(dbUser.getEmail())

        then:
        user != null

        when:
        user = userService.findByEmail("inexistent.email@te.com")

        then:
        user == null
    }

    @Unroll
    def "test: find renter by invalid email #value"() {
        when:
        userService.findByEmail(value)

        then:
        ConstraintViolationException e = thrown()
        e.getMessage() == errorMessage

        where:
        value               | errorMessage
        null                | "findByEmail.arg0: must not be null, findByEmail.arg0: must not be blank"
        ""                  | "findByEmail.arg0: must not be blank"
        "invalidEmail"      | "findByEmail.arg0: must be a well-formed email address"
        "inexistent.email@" | "findByEmail.arg0: must be a well-formed email address"

    }


}
