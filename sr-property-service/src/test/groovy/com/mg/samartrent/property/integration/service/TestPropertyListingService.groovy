package com.mg.samartrent.property.integration.service

import com.mg.persistence.service.QueryService
import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.domain.validation.ModelBusinessValidationException
import com.mg.smartrent.domain.validation.ModelValidationException
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.config.RestServicesConfig
import com.mg.smartrent.property.service.PropertyListingService
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.UserService
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

import javax.validation.ConstraintViolationException

import static com.mg.samartrent.property.TestUtils.generateProperty
import static com.mg.samartrent.property.TestUtils.generatePropertyListing
import static com.mg.samartrent.property.TestUtils.generateUser
import static org.mockito.Mockito.when
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

    @Autowired
    private PropertyService propertyService

    @MockBean
    private UserService userService
    @Autowired
    @InjectMocks
    private PropertyListingService listingService


    static boolean testsSetupExecuted
    static Property dbProperty = generateProperty()

    def setup() {
        if (!testsSetupExecuted) {
            purgeCollection(PropertyListing.class)

            MockitoAnnotations.initMocks(this)
            when(userService.userExists(dbProperty.getUserTID())).thenReturn(true)//mock external service call

            dbProperty = propertyService.save(dbProperty)
            testsSetupExecuted = true
        }

    }

    def "test: save null listing"() {
        when:
        listingService.save(null)

        then: "exception is thrown"
        ConstraintViolationException e = thrown()
        e.getMessage() == "save.listing: must not be null"
    }

    def "test: save listing for in-existent Property"() {

        when: "saving listing with invalid property id"
        listingService.save(generatePropertyListing())

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Listing could not be saved. Property not found."
    }

    def "test: save listing for in-existent User"() {
        setup: "mocking user"
        def listing = generatePropertyListing()
        listing.setPropertyTID(dbProperty.getTrackingId())
        when(userService.userExists(listing.getUserTID())).thenReturn(false)//mock external service call to user not found

        when: "saving listing"
        listingService.save(listing)

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Listing could not be saved. User not found, UserTID = ${listing.userTID}"
    }

    def "test: save listing with past checkin/checkout date"() {
        setup: "mock user exists"
        def listing = generatePropertyListing()
        listing.setPropertyTID(dbProperty.getTrackingId())
        when(userService.userExists(listing.getUserTID())).thenReturn(true)//mock external service call

        when: "saving with past dates"
        listing.setCheckInDate(new Date(System.currentTimeMillis() - 100000))
        listing.setCheckOutDate(new Date(System.currentTimeMillis() - 100000))
        listingService.save(listing)

        then: "exception is thrown"
        ModelValidationException e = thrown()
        e.getMessage().startsWith('[PropertyListing] validation failed [[Invalid future date for [CheckIn Date] =')
        e.getMessage().contains('Invalid future date for [CheckOut Date] =')
    }

    //todo: fix the test by creating an anotation to validate the date interval: date1.before(date2)
    def "test: save listing for with checkin after checkout date"() {
        setup:
        def listing = generatePropertyListing()
        listing.setPropertyTID(dbProperty.getTrackingId())
        when(userService.userExists(listing.getUserTID())).thenReturn(true)//mock external service call

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
        when(userService.userExists(listing.getUserTID())).thenReturn(true)//mock external service call

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

    def "test: findByPropertyTID then findByUserTID"() {
        setup:
        def property = generateProperty()
        when(userService.userExists(property.getUserTID())).thenReturn(true)//mock external service call

        def listing = generatePropertyListing()
        listing.setPropertyTID(propertyService.save(property).getTrackingId())
        when(userService.userExists(listing.getUserTID())).thenReturn(true)//mock external service call

        listing = listingService.save(listing)

        when:
        def listings = listingService.findByPropertyTID(listing.getPropertyTID())

        then: 'only one instance found'
        listings.size() == 1

        when:
        listings = listingService.findByPropertyTID(listing.getPropertyTID())

        then: 'only one instance found'
        listings.size() == 1
    }


}
