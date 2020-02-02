package com.mg.samartrent.user.unit

import com.mg.persistence.service.QueryService
import com.mg.smartrent.domain.enums.EnCurrency
import com.mg.smartrent.domain.enums.EnRentalApplicationStatus
import com.mg.smartrent.domain.models.RentalApplication
import com.mg.smartrent.domain.validation.ModelValidationException
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.service.ExternalUserService
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.RentalApplicationService
import org.junit.Assert
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Specification
import spock.lang.Unroll

import static com.mg.samartrent.user.TestUtils.generateProperty
import static com.mg.samartrent.user.TestUtils.generateRentalApplication
import static java.lang.System.currentTimeMillis
import static org.mockito.Mockito.when

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestRentalApplicationValidation extends Specification {

    @Mock
    private QueryService<RentalApplication> queryService
    @MockBean
    private ExternalUserService userService
    @MockBean
    private PropertyService propertyService


    @Autowired
    @InjectMocks
    private RentalApplicationService rentalApplicationService


    @Unroll
    def "test: property rental application validation for #field = #value"() {

        setup: "mock db call and user exists call"
        MockitoAnnotations.initMocks(this)
        when(userService.userExists(model.getRenterUserTID())).thenReturn(true)//mock external service call
        when(propertyService.findByTrackingId(model.getPropertyTID())).thenReturn(generateProperty())//mock property as it already exists
        when(queryService.save(model)).thenReturn(model)//mock db call

        when: "saving listing with a new test value"
        BeanWrapperImpl beanUtilsWrapper = new BeanWrapperImpl(model)
        beanUtilsWrapper.setPropertyValue(field, value)


        then: "expectations are meet"

        try {
            RentalApplication dbModel = rentalApplicationService.save(model)
            beanUtilsWrapper = new BeanWrapperImpl(dbModel)

            Assert.assertEquals(value, beanUtilsWrapper.getPropertyValue(field))
            Assert.assertEquals(expectException, false)
            Assert.assertEquals(13, beanUtilsWrapper.getProperties().size())//13 properties

        } catch (Exception e) {
            Assert.assertEquals(errorMsg, e.getMessage().trim())
        }
        where:
        model                       | field           | value                                  | expectException | errorMsg
        generateRentalApplication() | 'renterUserTID' | null                                   | true            | "Rental Application could not be saved. User not found, UserTID = null"
        generateRentalApplication() | 'renterUserTID' | ""                                     | true            | "Rental Application could not be saved. User not found, UserTID ="
        generateRentalApplication() | 'renterUserTID' | "inValidUserID"                        | true            | "Rental Application could not be saved. User not found, UserTID = inValidUserID"
        generateRentalApplication() | 'renterUserTID' | "mockedUserId"                         | false           | null

        generateRentalApplication() | 'propertyTID'   | null                                   | true            | "Rental Application could not be saved. Property not found, TrackingId = null"
        generateRentalApplication() | 'propertyTID'   | ""                                     | true            | "Rental Application could not be saved. Property not found, TrackingId ="
        generateRentalApplication() | 'propertyTID'   | "inValidPropertyID"                    | true            | "Rental Application could not be saved. Property not found, TrackingId = inValidPropertyID"
        generateRentalApplication() | 'propertyTID'   | "mockedPropertyId"                     | false           | null

        generateRentalApplication() | 'price'         | -1                                     | true            | "Field [price], value [-1], reason [must be greater than 0]"
        generateRentalApplication() | 'price'         | 0                                      | true            | "Field [price], value [0], reason [must be greater than 0]"
        generateRentalApplication() | 'price'         | 1                                      | false           | null

        generateRentalApplication() | 'currency'      | EnCurrency.USD.name()                  | false           | null
        generateRentalApplication() | 'currency'      | EnCurrency.EUR.name()                  | false           | null
        generateRentalApplication() | 'currency'      | 'invalid'                              | true            | "Field [currency], value [invalid], reason [must be any of enum class com.mg.smartrent.domain.enums.EnCurrency]"
        generateRentalApplication() | 'currency'      | ''                                     | true            | "Field [currency], value [], reason [must be any of enum class com.mg.smartrent.domain.enums.EnCurrency]"
        generateRentalApplication() | 'currency'      | null                                   | true            | "Field [currency], value [null], reason [must not be null]"

        generateRentalApplication() | "checkInDate"   | new Date(currentTimeMillis())          | false           | null
        generateRentalApplication() | "checkInDate"   | new Date(currentTimeMillis() - 100000) | false           | null
        generateRentalApplication() | "checkInDate"   | new Date(currentTimeMillis() + 100000) | false           | null

        generateRentalApplication() | "checkOutDate"  | new Date(currentTimeMillis())          | false           | null
        generateRentalApplication() | "checkOutDate"  | new Date(currentTimeMillis() - 100000) | false           | null
        generateRentalApplication() | "checkOutDate"  | new Date(currentTimeMillis() + 100000) | false           | null
    }


    def "test: property rental application status"() {

        setup: "mock db call and user exists call"
        RentalApplication application = generateRentalApplication();

        MockitoAnnotations.initMocks(this)
        when(userService.userExists(application.getRenterUserTID())).thenReturn(true)//mock external service call
        when(propertyService.findByTrackingId(application.getPropertyTID())).thenReturn(generateProperty())//mock property as it already exists
        when(queryService.save(application)).thenReturn(application)//mock db call

        when: "saving new application with no status"
        application.setStatus(null)
        def dbModel = rentalApplicationService.save(application)

        then: 'default value is set'
        dbModel.getStatus() == EnRentalApplicationStatus.PendingOwnerReview.name()


        when: "updating application status"
        dbModel.setStatus(EnRentalApplicationStatus.Accepted.name())
        dbModel = rentalApplicationService.save(application)

        then: 'new status is set'
        dbModel.getStatus() == EnRentalApplicationStatus.Accepted.name()


        when: "updating application status"
        dbModel.setStatus(EnRentalApplicationStatus.Rejected.name())
        dbModel = rentalApplicationService.save(application)

        then: 'new status is set'
        dbModel.getStatus() == EnRentalApplicationStatus.Rejected.name()


        when: "updating application status"
        dbModel.setStatus(EnRentalApplicationStatus.PendingRenterReview.name())
        dbModel = rentalApplicationService.save(application)

        then: 'new status is set'
        dbModel.getStatus() == EnRentalApplicationStatus.PendingRenterReview.name()


        when: "updating application status with and invalid status"
        dbModel.setStatus('invalid')
        rentalApplicationService.save(application)

        then: 'exception is thrown'
        thrown(ModelValidationException)

        when: "updating application status with and invalid status"
        dbModel.setStatus(null)
        rentalApplicationService.save(application)

        then: 'exception is thrown'
        thrown(ModelValidationException)
    }
}