package com.mg.samartrent.user.integration.service

import com.mg.samartrent.user.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnBuildingType
import com.mg.smartrent.domain.enums.EnPropertyCondition
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.ExternalUserService
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

import static com.mg.samartrent.user.TestUtils.generateProperty
import static org.mockito.Mockito.when

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestPropertyService extends IntegrationTestsSetup {

    @MockBean
    private ExternalUserService userService
    @Autowired
    @InjectMocks
    private PropertyService propertyService

    static boolean initialized

    def setup() {
        if (!initialized) {
            purgeCollection(Property.class)
            MockitoAnnotations.initMocks(this)
            initialized = true
        }
    }


    static Property dbProperty

    def "test: create property"() {

        setup: "mock external REST call"
        dbProperty = generateProperty()
        when(userService.userExists(dbProperty.getUserTID())).thenReturn(true)//mock external service call

        when: "saving a new property"

        dbProperty = propertyService.save(dbProperty)

        then: "successfully saved"
        dbProperty.getTrackingId() != null
        dbProperty.getCreatedDate() != null
        dbProperty.getModifiedDate() != null
        dbProperty.getUserTID() == "userId1234"
        dbProperty.getBuildingType() == EnBuildingType.Condo.name()
        dbProperty.getCondition() == EnPropertyCondition.Normal.name()
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
        property.getTrackingId() == dbProperty.getTrackingId()
    }

    def "test: find renter by userTID"() {
        when:
        def properties = propertyService.findByUserTID(dbProperty.getUserTID())

        then:
        properties.size() == 1
        properties.get(0).getUserTID() == dbProperty.getUserTID()
    }

    def "test: edit property"() {
        setup:
        def dbProperty = generateProperty()
        when(userService.userExists(dbProperty.getUserTID())).thenReturn(true)//mock external service call
        propertyService.save(dbProperty)

        when:
        def editedProperty = propertyService.findByTrackingId(dbProperty.trackingId)
        editedProperty.setTotalRooms(10)

        then:
        propertyService.save(editedProperty).getTotalRooms() == 10
    }

}
