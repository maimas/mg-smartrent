package com.mg.samartrent.property.integration.service


import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
        userService.getPasswordEncoder().matches("12341234", dbUser.getPassword())

    }

    def "test: find user by trackingId"() {
        when:
        def user = userService.findByTrackingId(dbUser.getTrackingId())

        then:
        user != null
    }

    def "test: find renter by email"() {
        when:
        def user = userService.findByEmail(dbUser.getEmail())

        then:
        user != null
    }


}
