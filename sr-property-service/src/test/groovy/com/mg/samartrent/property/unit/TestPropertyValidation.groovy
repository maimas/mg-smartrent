package com.mg.samartrent.property.unit

import com.mg.persistence.service.QueryService
import com.mg.smartrent.domain.enums.EnBuildingType
import com.mg.smartrent.domain.enums.EnPropertyCondition
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.UserService
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
import static org.mockito.Mockito.when

/**
 * This tests suite is designed to ensure correctness of the model validation constraints.
 */

@SpringBootTest(classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestPropertyValidation extends Specification {

    @MockBean
    private UserService userService
    @Mock
    private QueryService<Property> queryService
    @Autowired
    @InjectMocks
    private PropertyService propertyService


    @Unroll
    def "test: property constraint for #field = #value"() {

        setup: "mock db call and user exists call"
        MockitoAnnotations.initMocks(this)
        when(userService.userExists(model.getUserTID())).thenReturn(true)//mock external service call
        when(queryService.save(model)).thenReturn(model)//mock db call

        when: "saving property with a new test value"
        BeanWrapperImpl beanUtilsWrapper = new BeanWrapperImpl(model)
        beanUtilsWrapper.setPropertyValue(field, value)

        then: "expectations are meet"
        try {
            Property dbModel = propertyService.save(model)
            beanUtilsWrapper = new BeanWrapperImpl(dbModel)

            Assert.assertEquals(value, beanUtilsWrapper.getPropertyValue(field))

            Assert.assertEquals(expectException, false)
            Assert.assertEquals(13, beanUtilsWrapper.getProperties().size())//13 properties

        } catch (Exception e) {
            Assert.assertEquals(errorMsg, e.getMessage().trim())
        }

        where:
        model              | field              | value                                         | expectException | errorMsg
        generateProperty() | 'userTID'          | null                                          | true            | "User with trackingId=null not found."
        generateProperty() | 'userTID'          | ""                                            | true            | "User with trackingId= not found."
        generateProperty() | 'userTID'          | "inValidUserID"                               | true            | "User with trackingId=inValidUserID not found."
        generateProperty() | 'userTID'          | "userId1234"                                  | false           | null

        generateProperty() | 'buildingType'     | EnBuildingType.Apartment.name()               | false           | null
        generateProperty() | 'buildingType'     | EnBuildingType.Condo.name()                   | false           | null
        generateProperty() | 'buildingType'     | EnBuildingType.House.name()                   | false           | null
        generateProperty() | 'buildingType'     | null                                          | true            | "Field [buildingType], value [null], reason [must not be null]"
        generateProperty() | 'buildingType'     | ""                                            | true            | "Field [buildingType], value [], reason [must be any of enum class com.mg.smartrent.domain.enums.EnBuildingType]"
        generateProperty() | 'buildingType'     | "invalid"                                     | true            | "Field [buildingType], value [invalid], reason [must be any of enum class com.mg.smartrent.domain.enums.EnBuildingType]"

        generateProperty() | 'condition'        | EnPropertyCondition.Normal.name()             | false           | null
        generateProperty() | 'condition'        | EnPropertyCondition.RequiresReparation.name() | false           | null
        generateProperty() | 'condition'        | EnPropertyCondition.AfterReparation.name()    | false           | null
        generateProperty() | 'condition'        | "invalid"                                     | true            | "Field [condition], value [invalid], reason [must be any of enum class com.mg.smartrent.domain.enums.EnPropertyCondition]"
        generateProperty() | 'condition'        | ""                                            | true            | "Field [condition], value [], reason [must be any of enum class com.mg.smartrent.domain.enums.EnPropertyCondition]"
        generateProperty() | 'condition'        | null                                          | true            | "Field [condition], value [null], reason [must not be null]"

        generateProperty() | 'totalRooms'       | -1                                            | true            | "Field [totalRooms], value [-1], reason [must be greater than or equal to 0]"
        generateProperty() | 'totalRooms'       | 1                                             | false           | null
        generateProperty() | 'totalRooms'       | 10000                                         | false           | null

        generateProperty() | 'totalBathRooms'   | -1                                            | true            | "Field [totalBathRooms], value [-1], reason [must be greater than or equal to 0]"
        generateProperty() | 'totalBathRooms'   | 1                                             | false           | null
        generateProperty() | 'totalBathRooms'   | 10000                                         | false           | null

        generateProperty() | 'totalBalconies'   | -1                                            | true            | "Field [totalBalconies], value [-1], reason [must be greater than or equal to 0]"
        generateProperty() | 'totalBalconies'   | 1                                             | false           | null
        generateProperty() | 'totalBalconies'   | 10000                                         | false           | null

//        generateProperty() | 'thumbnail'        | null                                          | false           | null
        generateProperty() | 'thumbnail'        | new byte[1]                                   | false           | null

        generateProperty() | 'parkingAvailable' | true                                          | false           | null
        generateProperty() | 'parkingAvailable' | false                                         | false           | null
    }

}
