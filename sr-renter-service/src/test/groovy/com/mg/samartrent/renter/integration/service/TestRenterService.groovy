package com.mg.samartrent.renter.integration.service

import com.mg.samartrent.renter.TestUtils
import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.service.RenterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestRenterService extends IntegrationTestsSetup {

    @Autowired
    private RenterService renterService

    static boolean initialized

    def setup() {
        if (!initialized) {
            purgeCollection(Renter.class)
            initialized = true
        }
    }


    def "test: create renter"() {

        setup:
        Renter renter = TestUtils.generateRenter()

        when: "saving "

        def dbRenter = renterService.save(renter)

        then: "successfully saved"
        dbRenter.getTrackingId() != null
        dbRenter.getCreatedDate() != null
        dbRenter.getModifiedDate() != null
        dbRenter.getEmail() == renter.getEmail()
        dbRenter.getDateOfBirth() == renter.getDateOfBirth()
        dbRenter.getGender() == renter.getGender()
        dbRenter.getPhoneNumber() == renter.getPhoneNumber()
        dbRenter.getFirstName() == renter.getFirstName()
        dbRenter.getLastName() == renter.getLastName()
    }

    def "test: find renter by trackingId"() {
        setup:
        Renter renter = renterService.save(TestUtils.generateRenter())

        when:
        def dbRenter = renterService.findByEmail(renter.getEmail())

        then:
        dbRenter != null
        dbRenter.getTrackingId() == renter.getTrackingId()
        dbRenter.getEmail() == renter.getEmail()
    }

}
