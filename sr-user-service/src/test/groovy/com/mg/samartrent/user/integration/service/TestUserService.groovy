package com.mg.samartrent.user.integration.service


import com.mg.samartrent.user.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.domain.validation.ModelBusinessValidationException
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.service.UserService
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

import static com.mg.samartrent.user.ModelBuilder.generateUser
import static org.apache.commons.lang.RandomStringUtils.randomNumeric

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


    def "test: create user"() {

        when: "saving a new user"
        def user = generateUser()
        def dbUser = userService.create(user.clone() as User)

        then: "successfully saved"
        dbUser.getCreatedDate() != null
        dbUser.getModifiedDate() != null
        dbUser.getFirstName() == user.firstName
        dbUser.getLastName() == user.lastName
        dbUser.getEmail() == user.email
        dbUser.getStatus() == EnUserStatus.Pending
        dbUser.getPassword() != user.password
        dbUser.getGender() == EnGender.Male
        dbUser.getDateOfBirth() == user.dateOfBirth
        passwordEncoder.matches(user.password, dbUser.getPassword())

    }

    def "test: update user"() {

        setup: "saving a new user"
        def user = generateUser()
        def dbUser = userService.create(user.clone() as User)
        Date newDOB = new Date(System.currentTimeMillis() - 1000000000000);

        when:
        dbUser.setFirstName("FNameUpdated")
        dbUser.setLastName("LNameUpdated")
//        dbUser.setEmail("test.test@gmail.com")

        dbUser.setStatus(EnUserStatus.Active)
        dbUser.setDateOfBirth(newDOB)
        dbUser.setGender(EnGender.Female)
        dbUser.setEnabled(true)
        dbUser.setPassword("abc1234")
        userService.update(dbUser)

        then: "successfully updated"
        dbUser.getCreatedDate() != null
        dbUser.getModifiedDate() != null
        dbUser.getFirstName() == "FNameUpdated"
        dbUser.getLastName() == "LNameUpdated"
        dbUser.getEmail() == user.email // email is same as it is not allowed to be changed in update operation
        dbUser.getStatus() == EnUserStatus.Active
        passwordEncoder.matches("abc1234", dbUser.getPassword())
        dbUser.getGender() == EnGender.Female
        dbUser.getDateOfBirth() == newDOB
    }

    def "test: update user with a new ID"() {
        setup: "saving a new user"
        def oldUser = userService.create(generateUser());
        def newUser = userService.create(generateUser())

        when:
        newUser.setId(oldUser.id)
        newUser.setEmail(oldUser.email) //email update is not allowed with update operation
        userService.update(newUser)

        then:
        userService.findById(newUser.id).isPresent()
    }

    def "test: update user with a new email"() {
        setup: "saving a new user"
        def oldUser = userService.create(generateUser());
        def newUser = userService.create(generateUser())

        when:
        newUser.setEmail(oldUser.email)
        userService.update(newUser)

        then:
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Email update is not allowed."
    }

    def "test: update user with null password"() {
        setup: "saving a new user"
        def dbUser = userService.create(generateUser())

        when:
        dbUser.setPassword(null)
        def updatedUser = userService.update(dbUser)

        then:
        dbUser.getPassword() == updatedUser.getPassword()
    }

    def "test: update user that does not exists"() {
        when:
        def user = generateUser()
        userService.update(user)

        then:
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "User with ID=null not found."
    }

    def "test: find user by ID"() {
        setup:
        def dbUser = userService.create(generateUser())

        when:
        def user = userService.findById(dbUser.getId())

        then:
        user.isPresent()


        when:
        user = userService.findById("not_existent")

        then:
        !user.isPresent()
    }


    def "test: find user by email"() {
        setup:
        def dbUser = userService.create(generateUser())

        when:
        def user = userService.findByEmail(dbUser.getEmail())

        then:
        user.isPresent()

        when:
        user = userService.findByEmail("inexistent.email@te.com")

        then:
        !user.isPresent()
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
