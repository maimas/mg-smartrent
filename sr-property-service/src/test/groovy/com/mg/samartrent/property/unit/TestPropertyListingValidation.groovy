package com.mg.samartrent.property.unit

import com.mg.persistence.service.QueryService
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.service.PropertyListingService
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.UserService
import org.apache.commons.beanutils.BeanUtils
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

import static com.mg.samartrent.property.TestUtils.generateProperty
import static com.mg.samartrent.property.TestUtils.generatePropertyListing
import static java.lang.System.*
import static org.apache.commons.beanutils.BeanUtils.setProperty
import static org.mockito.Mockito.when

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestPropertyListingValidation extends Specification {

    @Mock
    private QueryService<PropertyListing> queryService
    @MockBean
    private UserService userService
    @MockBean
    private PropertyService propertyService


    @Autowired
    @InjectMocks
    private PropertyListingService propertyListingService


    @Unroll
    def "test: property listing validation for #field = #value"() {

        setup: "mock db call and user exists call"
        MockitoAnnotations.initMocks(this)
        when(userService.userExists(model.getUserTID())).thenReturn(true)//mock external service call
        when(propertyService.findByTrackingId(model.getPropertyTID())).thenReturn(generateProperty())//mock property as it already exists
        when(queryService.save(model)).thenReturn(model)//mock db call

        when: "saving listing with a new test value"
        setProperty(model, field, value)

        then: "expectations are meet"

        try {
            PropertyListing dbModel = propertyListingService.save(model)
            BeanWrapperImpl beanUtilsWrapper = new BeanWrapperImpl(dbModel)

            Assert.assertEquals(value, beanUtilsWrapper.getPropertyValue(field))
            Assert.assertEquals(expectException, false)
            Assert.assertEquals(13, beanUtilsWrapper.getProperties().size())//13 properties

        } catch (Exception e) {
            Assert.assertEquals(errorMsg, e.getMessage().trim())
        }
        where:
        model                     | field          | value                                  | expectException | errorMsg
        generatePropertyListing() | 'userTID'      | null                                   | true            | "Listing could not be saved. User not found, UserTID = null"
        generatePropertyListing() | 'userTID'      | ""                                     | true            | "Listing could not be saved. User not found, UserTID ="
        generatePropertyListing() | 'userTID'      | "inValidUserID"                        | true            | "Listing could not be saved. User not found, UserTID = inValidUserID"
        generatePropertyListing() | 'userTID'      | "mockedUserId"                         | false           | null

        generatePropertyListing() | 'propertyTID'  | null                                   | true            | "Listing could not be saved. Property not found."
        generatePropertyListing() | 'propertyTID'  | ""                                     | true            | "Listing could not be saved. Property not found."
        generatePropertyListing() | 'propertyTID'  | "inValidPropertyID"                    | true            | "Listing could not be saved. Property not found."
        generatePropertyListing() | 'propertyTID'  | "mockedPropertyId"                     | false           | null

        generatePropertyListing() | 'price'        | -1                                     | true            | "Field [price], value [-1], reason [must be greater than or equal to 0]"
        generatePropertyListing() | 'price'        | 0                                      | false           | null
        generatePropertyListing() | 'price'        | 1                                      | false           | null

        generatePropertyListing() | 'totalViews'   | -1                                     | true            | "Field [totalViews], value [-1], reason [must be greater than or equal to 0]"
        generatePropertyListing() | 'totalViews'   | 0                                      | false           | null
        generatePropertyListing() | 'totalViews'   | 1                                      | false           | null

        generatePropertyListing() | "checkInDate"  | new Date(currentTimeMillis())          | false           | null
        generatePropertyListing() | "checkInDate"  | new Date(currentTimeMillis() - 100000) | false           | null
        generatePropertyListing() | "checkInDate"  | new Date(currentTimeMillis() + 100000) | false           | null

        generatePropertyListing() | "checkOutDate" | new Date(currentTimeMillis())          | false           | null
        generatePropertyListing() | "checkOutDate" | new Date(currentTimeMillis() - 100000) | false           | null
        generatePropertyListing() | "checkOutDate" | new Date(currentTimeMillis() + 100000) | false           | null

        generatePropertyListing() | "listed"       | true                                   | false           | null
        generatePropertyListing() | "listed"       | false                                  | false           | null

    }

}
