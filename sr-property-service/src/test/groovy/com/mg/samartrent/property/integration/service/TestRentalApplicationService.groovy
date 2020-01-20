package com.mg.samartrent.property.integration.service

import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnCurrency
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.domain.models.RentalApplication
import com.mg.smartrent.domain.validation.ModelBusinessValidationException
import com.mg.smartrent.domain.validation.ModelValidationException
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.service.ExternalUserService
import com.mg.smartrent.property.service.PropertyListingService
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.RentalApplicationService
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

import javax.validation.ConstraintViolationException

import static com.mg.samartrent.property.TestUtils.generateProperty
import static com.mg.samartrent.property.TestUtils.generatePropertyListing
import static com.mg.samartrent.property.TestUtils.generateRentalApplication
import static org.mockito.Mockito.when

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(
        classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"])
class TestRentalApplicationService extends IntegrationTestsSetup {

    @Autowired
    private PropertyService propertyService

    @MockBean
    private ExternalUserService userService
    @Autowired
    @InjectMocks
    private RentalApplicationService rentalApplicationService


    static boolean testsSetupExecuted
    static Property dbProperty = generateProperty()

    def setup() {
        if (!testsSetupExecuted) {
            purgeCollection(RentalApplication.class)

            MockitoAnnotations.initMocks(this)
            when(userService.userExists(dbProperty.getUserTID())).thenReturn(true)//mock external service call

            dbProperty = propertyService.save(dbProperty)
            testsSetupExecuted = true
        }

    }

    def "test: save null rental application"() {
        when:
        rentalApplicationService.save(null)

        then: "exception is thrown"
        ConstraintViolationException e = thrown()
        e.getMessage() == "save.model: must not be null"
    }

    def "test: save rental application for in-existent Property"() {

        when: "saving listing with invalid property id"
        rentalApplicationService.save(generateRentalApplication())

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Rental Application could not be saved. User not found, UserTID = mockedUserId"
    }

    def "test: save listing for in-existent User"() {
        setup: "mocking user"
        def application = generateRentalApplication()
        application.setPropertyTID(dbProperty.getTrackingId())
        when(userService.userExists(application.getRenterUserTID())).thenReturn(false)//mock external service call to user not found

        when: "saving listing"
        rentalApplicationService.save(application)

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage() == "Rental Application could not be saved. User not found, UserTID = mockedUserId"
    }

    def "test: save rental application with past checkin/checkout date"() {
        setup: "mock user exists"
        def application = generateRentalApplication()
        application.setPropertyTID(dbProperty.getTrackingId())
        when(userService.userExists(application.getRenterUserTID())).thenReturn(true)//mock external service call

        when: "saving with past dates"
        application.setCheckInDate(new Date(System.currentTimeMillis() - 100000))
        application.setCheckOutDate(new Date(System.currentTimeMillis() - 100000))
        rentalApplicationService.save(application)

        then: "exception is thrown"
        ModelValidationException e = thrown()
        e.getMessage().startsWith('[PropertyListing] validation failed [[Invalid future date for [CheckIn Date] =')
        e.getMessage().contains('Invalid future date for [CheckOut Date] =')
    }

    //todo: fix the test by creating an anotation to validate the date interval: date1.before(date2)
    def "test: save rental application with checkin date after checkout date"() {
        setup:
        def application = generateRentalApplication()
        application.setPropertyTID(dbProperty.getTrackingId())
        when(userService.userExists(application.getRenterUserTID())).thenReturn(true)//mock external service call

        when: "checkin after checkout"
        application.setCheckInDate(new Date(System.currentTimeMillis() + 100000))
        application.setCheckOutDate(new Date(System.currentTimeMillis() - 100000))
        rentalApplicationService.save(application)

        then: "exception is thrown"
        ModelBusinessValidationException e = thrown()
        e.getMessage().startsWith("[PropertyListing] validation failed [[Invalid future date for [CheckOut Date] =")
    }

    def "test: save listing for existent User and Property"() {
        setup:
        def application = generateRentalApplication()
        application.setPropertyTID(dbProperty.getTrackingId())
        when(userService.userExists(application.getRenterUserTID())).thenReturn(true)//mock external service call

        when: "saving listing"
        application = rentalApplicationService.save(application)

        then: "successfully saved"
        application.getTrackingId() != null
        application.getCreatedDate() != null
        application.getModifiedDate() != null
        application.getRenterUserTID() == "mockedUserId"
        application.getPropertyTID() == dbProperty.trackingId
        application.getPrice() == 100
        application.getCurrency() == EnCurrency.USD.name()
        application.getCheckInDate().before(new Date())
        application.getCheckOutDate().after(new Date())
    }

    def "test: findByTracking then findByPropertyTID then findByRenterUserTID"() {
        setup:
        def property = generateProperty()
        when(userService.userExists(property.getUserTID())).thenReturn(true)//mock external service call
        property = propertyService.save(property)

        def application = generateRentalApplication()
        application.setPropertyTID(property.getTrackingId())
        application.setRenterUserTID('mockedRenterUserIdentifier')
        when(userService.userExists('mockedRenterUserIdentifier')).thenReturn(true)//mock external service call


        when:
        application = rentalApplicationService.save(application)
        def dbListing = rentalApplicationService.findByTrackingId(application.getTrackingId())

        then: 'found'
        dbListing != null


        when:
        def applications = rentalApplicationService.findByPropertyTID(application.getPropertyTID())

        then: 'only one instance found'
        applications.size() == 1

        when:
        applications = rentalApplicationService.findByRenterUserTID(application.getRenterUserTID())

        then: 'only one instance found'
        applications.size() == 1
    }


}
