package com.mg.samartrent.property.integration.service

import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.service.PropertyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import static com.mg.samartrent.property.ModelBuilder.generateProperty

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestPropertyService extends IntegrationTestsSetup {

    @Autowired
    private PropertyService propertyService

    static boolean initialized

    def setup() {
        if (!initialized) {
            purgeCollection(Property.class)
            initialized = true
        }
    }


    static Property dbProperty

    def "test: create property"() {

        when: "saving a new property"
        dbProperty = propertyService.save(generateProperty())

        then: "successfully saved"
        dbProperty.getTrackingId() != null
        dbProperty.getCreatedDate() != null
        dbProperty.getModifiedDate() != null
        dbProperty.getUserTID() == "userId1234"
        dbProperty.getBuildingType() == "apartment"
        dbProperty.getCondition() == "requiresReparation"
        dbProperty.getTotalRooms() == 10
        dbProperty.getTotalBathRooms() == 5
        dbProperty.getTotalBalconies() == 1
        dbProperty.getThumbnail() == null
        dbProperty.isParkingAvailable()
    }

    def "test: find renter by trackingId"() {
        when:
        def property = propertyService.findByTrackingId(dbProperty.getTrackingId())

        then:
        property != null
    }

    def "test: find renter by userTID"() {
        when:
        def property = propertyService.findByUserTID(dbProperty.getUserTID())

        then:
        property.size() == 1
    }


}
