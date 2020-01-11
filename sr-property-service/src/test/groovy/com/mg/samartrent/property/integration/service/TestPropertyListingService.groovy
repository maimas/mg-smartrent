package com.mg.samartrent.property.integration.service

import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.domain.validation.ModelBusinessValidationException
import com.mg.smartrent.domain.validation.ModelValidationException
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.config.RestServicesConfig
import com.mg.smartrent.property.service.PropertyListingService
import com.mg.smartrent.property.service.PropertyService
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

import static com.mg.samartrent.property.ModelBuilder.generateProperty
import static com.mg.samartrent.property.ModelBuilder.generatePropertyListing
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(
        classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"])
class TestPropertyListingService extends IntegrationTestsSetup {


    @Mock
    RestTemplate restTemplateMock

    @Autowired
    @InjectMocks
    private PropertyListingService listingService

    @Autowired
    private PropertyService propertyService
    @Autowired
    private RestServicesConfig restServicesConfig;

    static boolean testsSetupExecuted
    static Property dbProperty

    def setup() {
        if (!testsSetupExecuted) {
            purgeCollection(PropertyListing.class)
            dbProperty = propertyService.save(generateProperty())
            testsSetupExecuted = true
        }
    }

    def "test: save null listing"() {

        when:
        listingService.save(null)

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Listing could not be saved. Invalid Listing."
    }

    def "test: save listing for in-existent Property"() {

        when: "saving listing with invalid property id"
        listingService.save(generatePropertyListing())

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Listing could not be saved. Property not found."
    }

    def "test: save listing for in-existent User"() {
        setup:
        def listing = generatePropertyListing()
        listing.setPropertyTID(dbProperty.getTrackingId())
        mockUserRestCall(listing.userTID, new ResponseEntity("Mocking: User not found", NOT_FOUND))

        when: "saving listing"
        listingService.save(listing)

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Listing could not be saved. User not found, UserTID = ${listing.userTID}"
    }

    def "test: save listing with past checkin/checkout date"() {
        setup:
        def listing = generatePropertyListing()
        listing.setPropertyTID(dbProperty.getTrackingId())
        mockUserRestCall(listing.userTID, new ResponseEntity("Mocked response", OK));

        when: "saving with past dates"
        listing.setCheckInDate(new Date(System.currentTimeMillis() - 100000))
        listing.setCheckOutDate(new Date(System.currentTimeMillis() - 100000))
        listingService.save(listing)

        then: "exception is thrown"
        ModelValidationException e = thrown()
        e.getMessage().startsWith('[PropertyListing] validation failed [[Invalid future date for [CheckIn Date] =')
        e.getMessage().contains('Invalid future date for [CheckOut Date] =')
    }

    def "test: save listing for with checkin after checkout date"() {
        setup:
        def listing = generatePropertyListing()
        listing.setPropertyTID(dbProperty.getTrackingId())
        mockUserRestCall(listing.userTID, new ResponseEntity("Mocked response", OK))

        when: "checkin after checkout"
        listing.setCheckInDate(new Date(System.currentTimeMillis() + 100000))
        listing.setCheckOutDate(new Date(System.currentTimeMillis() - 100000))
        listingService.save(listing)

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage().startsWith("[PropertyListing] validation failed [[Invalid future date for [CheckOut Date] =")
    }

    def "test: save listing for existent User and Property"() {
        setup:
        def listing = generatePropertyListing()
        listing.setPropertyTID(dbProperty.getTrackingId())
        mockUserRestCall(listing.userTID, new ResponseEntity("Mocked response", OK))

        when: "saving listing"
        listing = listingService.save(listing)

        then: "successfully saved"
        listing.getTrackingId() != null
        listing.getCreatedDate() != null
        listing.getModifiedDate() != null
        listing.getUserTID() == "mockedUserId"
        listing.getPropertyTID() == dbProperty.trackingId
        listing.getPrice() == 100
        listing.getTotalViews() == 3
        listing.getCheckInDate().after(new Date())
        listing.getCheckOutDate().after(new Date())

    }

    def "test: find by propertyTID"() {
        setup:
        def listing = generatePropertyListing()
        listing.setPropertyTID(propertyService.save(generateProperty()).getTrackingId())

        mockUserRestCall(listing.userTID, new ResponseEntity("Mocked response", OK));
        listing = listingService.save(listing)

        when: 'checking number of saved instances'
        def listings = listingService.findByPropertyTID(listing.getPropertyTID())

        then: 'only one instance found'
        listings.size() == 1
    }

    def "test: find by userTID"() {
        setup:
        def listing = generatePropertyListing()
        listing.setPropertyTID(dbProperty.getTrackingId())
        listing.setUserTID("invalidMockedUserID")

        mockUserRestCall(listing.userTID, new ResponseEntity("Mocked response", OK));
        listing = listingService.save(listing)

        when: 'checking number of saved instances'
        def listings = listingService.findByUserTID(listing.getUserTID())

        then: 'only one instance is found'
        listings.size() == 1

    }


    def mockUserRestCall(String userTID, ResponseEntity responseEntity) {
        MockitoAnnotations.initMocks(this)
        URI uri = URI.create(restServicesConfig.getUsersServiceURI() + "/rest/users/exists/trackingId=" + userTID)
        Mockito.when(restTemplateMock.getForEntity(uri, ResponseEntity.class)).thenReturn(responseEntity)
    }
}
