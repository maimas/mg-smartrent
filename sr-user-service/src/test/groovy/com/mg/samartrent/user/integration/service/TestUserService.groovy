package com.mg.samartrent.user.integration.service


import com.mg.samartrent.user.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.domain.validation.ModelBusinessValidationException
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

import static com.mg.samartrent.user.ModelBuilder.generateUser

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
        dbUser.getStatus() == EnUserStatus.Pending.name()
        dbUser.getPassword() != "12341234"
        dbUser.getGender() == EnGender.Male.name()
        dbUser.getDateOfBirth() != null
        passwordEncoder.matches("12341234", dbUser.getPassword())

    }

    def "test: update user"() {

        setup: "saving a new user"
        dbUser = userService.save(generateUser())
        Date newDOB = new Date(System.currentTimeMillis() - 1000000000000);

        when:
        dbUser.setFirstName("FNameUpdated")
        dbUser.setLastName("LNameUpdated")
        dbUser.setEmail("test.test@gmail.com")

        dbUser.setStatus(EnUserStatus.Active.name())
        dbUser.setDateOfBirth(newDOB)
        dbUser.setGender(EnGender.Female.name())
        dbUser.setEnabled(true)
        dbUser.setPassword("12341234")
        userService.update(dbUser)

        then: "successfully updated"
        dbUser.getTrackingId() != null
        dbUser.getCreatedDate() != null
        dbUser.getModifiedDate() != null
        dbUser.getFirstName() == "FNameUpdated"
        dbUser.getLastName() == "LNameUpdated"
        dbUser.getEmail() == "test.test@gmail.com"
        dbUser.getStatus() == EnUserStatus.Active.name()
        passwordEncoder.matches("12341234", dbUser.getPassword())
        dbUser.getGender() == EnGender.Female.name()
        dbUser.getDateOfBirth() == newDOB
    }

    def "test: update user with a new trackingId"() {
        setup: "saving a new user"
        def otherUser = userService.save(generateUser());
        dbUser = userService.save(generateUser())

        when:
        dbUser.setTrackingId(otherUser.trackingId)
        userService.update(dbUser)

        then:
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "User trackingId is not allowed to be updated."
    }

    def "test: update user with null password"() {
        setup: "saving a new user"
        dbUser = userService.save(generateUser())

        when:
        dbUser.setPassword(null)
        userService.update(dbUser)

        then:
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "User could not be updated. Password not specified."
    }

    def "test: update user that does not exists"() {
        when:
        dbUser = generateUser()
        userService.update(dbUser)

        then:
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Could not update. User does not exists."
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
    def "test: find user by invalid trackingId #value"() {
        when:
        userService.findByTrackingId(value)

        then:
        ConstraintViolationException e = thrown()
        e.getMessage().contains(errorMessage)

        where:
        value | errorMessage
        null  | "findByTrackingId.arg0: must not be null"
        ""    | "findByTrackingId.arg0: must not be blank"
    }

    def "test: find user by email"() {
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
    def "test: find user by invalid email #value"() {
        when:
        userService.findByEmail(value)

        then:
        ConstraintViolationException e = thrown()
        e.getMessage().contains(errorMessage)

        where:
        value               | errorMessage
        null                | "findByEmail.arg0: must not be null"
        ""                  | "findByEmail.arg0: must not be blank"
        "invalidEmail"      | "findByEmail.arg0: must be a well-formed email address"
        "inexistent.email@" | "findByEmail.arg0: must be a well-formed email address"

    }


}
