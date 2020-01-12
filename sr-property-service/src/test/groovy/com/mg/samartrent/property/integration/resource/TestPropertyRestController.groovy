package com.mg.samartrent.property.integration.resource

import com.fasterxml.jackson.core.type.TypeReference
import com.mg.samartrent.property.TestUtils
import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.resource.PropertyRestController
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.UserService
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Stepwise

import static org.mockito.Mockito.when

@SpringBootTest(
        classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
@Stepwise
class TestPropertyRestController extends IntegrationTestsSetup {

    @LocalServerPort
    private int port;
    @Autowired
    private PropertyRestController restController

    @MockBean
    private UserService userService
    @Autowired
    @InjectMocks
    private PropertyService propertyService

    static boolean initialized

    static def userTID = "mockUserID"
    static def dbProperty = TestUtils.generateProperty()

    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        if (!initialized) {
            purgeCollection(User.class)
            MockitoAnnotations.initMocks(this)
            when(userService.userExists(userTID)).thenReturn(true)//mock external service call


            mockMvc = MockMvcBuilders.standaloneSetup(restController).build()
            initialized = true
        }
    }

    def "test: save property"() {
        setup: "set mocked user id"
        dbProperty.setUserTID(userTID)

        when:
        def url = "http://localhost:$port/rest/properties"
        def response = doPost(mockMvc, url, dbProperty).getResponse()

        then:
        response.status == HttpStatus.OK.value()
        response.getContentAsString() == ""
    }

    def "test: get by userTID"() {

        when:
        def url = "http://localhost:$port/rest/properties?userTID=${dbProperty.getUserTID()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def dbProperties = (List<Property>) mvcResultToModels(result, new TypeReference<List<Property>>() {})
        then:
        dbProperties.size() == 1

        when:
        dbProperty = dbProperties.get(0)

        then:
        dbProperty.getTrackingId() != null
        dbProperty.getUserTID() != null
    }

    def "test: get by in-existent userTID"() {

        when:
        def url = "http://localhost:$port/rest/properties?userTID=inExistent"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == "[]"
    }

    def "test: get property by trackingId"() {
        when:
        def url = "http://localhost:$port/rest/properties?trackingId=${dbProperty.getTrackingId()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def user = (Property) mvcResultToModel(result, Property.class)
        then:
        user.getTrackingId() == dbProperty.getTrackingId()
    }

    def "test: get by trackingId for in-existent property"() {

        when:
        def url = "http://localhost:$port/rest/properties?trackingId=testInvId"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == ""
    }

}


