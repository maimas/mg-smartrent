package com.mg.samartrent.user.integration.resource

import com.fasterxml.jackson.core.type.TypeReference
import com.mg.samartrent.user.TestUtils
import com.mg.samartrent.user.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnRentalApplicationStatus
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.RentalApplication
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.resource.RentalApplicationRestController
import com.mg.smartrent.property.service.ExternalUserService
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.RentalApplicationService
import org.apache.commons.lang.RandomStringUtils
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static com.mg.smartrent.domain.enums.EnRentalApplicationStatus.PendingOwnerReview
import static org.mockito.Mockito.when

@SpringBootTest(
        classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestRentalApplicationsRestController extends IntegrationTestsSetup {

    @LocalServerPort
    private int port

    @Autowired
    private PropertyService propertyService

    @Autowired
    private RentalApplicationRestController restController

    @MockBean
    private ExternalUserService userService
    @Autowired
    @InjectMocks
    private RentalApplicationService rentalApplicationService

    static boolean initialized
    static String endpointURL;

    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        if (!initialized) {
            endpointURL = "http://localhost:$port/rest/rentalapplications"
            purgeCollection(User.class)
            purgeCollection(Property.class)
            purgeCollection(RentalApplication.class)
            mockMvc = MockMvcBuilders.standaloneSetup(restController).build()
            initialized = true
        }
    }

    def "test: save rental application"() {

        setup: "set mocked user id"
        Property property = createProperty()

        when: "create rental application"
        def response = createRentalApplication(property)

        then: "created"
        response.status == HttpStatus.OK.value()
        response.getContentAsString() == ""
    }

    def "test: get by propertyTID"() {
        setup:
        Property property = createProperty()
        createRentalApplication(property)

        when:
        def url = "$endpointURL?propertyTID=${property.getTrackingId()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def listOfApplications = (List<RentalApplication>) mvcResultToModels(result, new TypeReference<List<RentalApplication>>() {
        })
        then:
        listOfApplications.size() == 1

        when:
        def dbRentalApplication = listOfApplications.get(0)

        then:
        dbRentalApplication.getTrackingId() != null
        dbRentalApplication.getPropertyTID() == property.getTrackingId()
        dbRentalApplication.getRenterUserTID() == property.getUserTID()
    }

    def "test: get by renterUserTID"() {
        setup:
        purgeCollection(RentalApplication.class)
        Property property = createProperty()
        createRentalApplication(property)

        when:
        def url = "$endpointURL?renterUserTID=${property.getUserTID()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def listOfApplications = (List<RentalApplication>) mvcResultToModels(result, new TypeReference<List<RentalApplication>>() {
        })
        then:
        listOfApplications.size() == 1

        when:
        def application = listOfApplications.get(0)

        then:
        application.getTrackingId() != null
        application.getPropertyTID() == property.getTrackingId()
        application.getRenterUserTID() == property.getUserTID()
    }


    def "test: get by in-existent propertyTID"() {

        when:
        def url = "$endpointURL?propertyTID=inExistent"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == "[]"
    }

    def "test: get by trackingId"() {

        setup: "model"
        Property property = createProperty()
        String rentApplicTrackingID = RandomStringUtils.randomAlphabetic(10)
        createRentalApplication(property, rentApplicTrackingID)

        when:
        def url = "$endpointURL/${rentApplicTrackingID}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def dbRentalApplication = (RentalApplication) mvcResultToModel(result, RentalApplication.class)

        then:
        dbRentalApplication != null
    }


//--------------------Private Methods-----------------------------

    private createProperty() {
        def userTID = "mockUserID"

        MockitoAnnotations.initMocks(this)
        when(userService.userExists(userTID)).thenReturn(true)//mock external service call

        Property p = TestUtils.generateProperty()
        p.setUserTID(userTID)
        p = propertyService.save(p)

        return p
    }

    private MockHttpServletResponse createRentalApplication(Property property) {
        return createRentalApplication(property, null)
    }

    private MockHttpServletResponse createRentalApplication(Property property, String rentalApplTrackingId) {
        RentalApplication rentalApplication = TestUtils.generateRentalApplication()
        rentalApplication.setTrackingId(rentalApplTrackingId)
        rentalApplication.setStatus(PendingOwnerReview.name())
        rentalApplication.setPropertyTID(property.getTrackingId())
        rentalApplication.setRenterUserTID(property.getUserTID())

        def response = doPost(mockMvc, endpointURL, rentalApplication).getResponse()

        return response
    }
}


