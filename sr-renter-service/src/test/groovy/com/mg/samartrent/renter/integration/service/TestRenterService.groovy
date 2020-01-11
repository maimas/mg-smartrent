package com.mg.samartrent.renter.integration.service


import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.domain.Renter
import com.mg.smartrent.renter.service.RenterService
import com.mg.smartrent.renter.service.SchemaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = RenterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestRenterService extends IntegrationTestsSetup {


    @Autowired
    private SchemaService schemaService
    @Autowired
    private RenterService renterService

    @Value('${domain.schemas.renter}')
    private String schemasDir

    static boolean schemaInitialized

    def setup() {
        if (!schemaInitialized) {
            schemaService.initSchema()
            schemaInitialized = true
        }
    }

    static Renter dbRenter

    def "test: create renter"() {

        when: "saving a new renter"
        dbRenter = renterService.save(generateRenter())

        then: "successfully saved"
        dbRenter.getTrackingId() != null
        dbRenter.getItemType() == "Renter"
        dbRenter.getCreatedDate() != null
        dbRenter.getModifiedDate() != null
        dbRenter.getFirstName() == "FName"
        dbRenter.getLastName() == "LName"
        dbRenter.getEmail() == "test.test@domain.com"
        dbRenter.getGender() == "male"
    }

    def "test: find renter by trackingId"() {
        when:
        def renter = renterService.findByTrackingId(dbRenter.getTrackingId())

        then:
        renter != null
    }


    //------------------------------------------------------------
    //---------------------Private methods (helpers)--------------
    //------------------------------------------------------------

    private static Renter generateRenter() {

        Renter renter = new Renter()

        renter.setFirstName("FName")
        renter.setLastName("LName")
        renter.setDateOfBirth(new Date())
        renter.setEmail("test.test@domain.com")
        renter.setGender("male")
        renter.setPhoneNumber("452222221")

        return renter
    }

}
