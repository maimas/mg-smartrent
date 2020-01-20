package com.mg.samartrent.property.integration.resource

import com.fasterxml.jackson.core.type.TypeReference
import com.mg.samartrent.property.TestUtils
import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.property.PropertyApplication
import com.mg.smartrent.property.resource.PropertyListingRestController
import com.mg.smartrent.property.service.PropertyService
import com.mg.smartrent.property.service.ExternalUserService
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
import spock.lang.Stepwise

import static org.mockito.Mockito.when

@SpringBootTest(
        classes = PropertyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
@Stepwise
class TestPropertyListingRestController extends IntegrationTestsSetup {

    @LocalServerPort
    private int port
    @Autowired
    private PropertyListingRestController restController

    @MockBean
    private ExternalUserService userService
    @Autowired
    @InjectMocks
    private PropertyService propertyService

    static boolean initialized


    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        if (!initialized) {
            purgeCollection(User.class)
            purgeCollection(Property.class)
            purgeCollection(PropertyListing.class)
            mockMvc = MockMvcBuilders.standaloneSetup(restController).build()
            initialized = true
        }
    }

    def "test: save property listing"() {

        setup: "set mocked user id"
        Property property = createProperty()

        when: "create listing"
        def response = createListing(property)

        then: "created"
        response.status == HttpStatus.OK.value()
        response.getContentAsString() == ""
    }

    def "test: get by propertyTID"() {
        setup:
        Property property = createProperty()
        createListing(property)

        when:
        def url = "http://localhost:$port/rest/propertylistings?propertyTID=${property.getTrackingId()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def dbListings = (List<PropertyListing>) mvcResultToModels(result, new TypeReference<List<PropertyListing>>() {
        })
        then:
        dbListings.size() == 1

        when:
        def dbPropertyListing = dbListings.get(0)

        then:
        dbPropertyListing.getTrackingId() != null
        dbPropertyListing.getPropertyTID() == property.getTrackingId()
        dbPropertyListing.getUserTID() == property.getUserTID()
    }

    def "test: get by in-existent propertyTID"() {

        when:
        def url = "http://localhost:$port/rest/propertylistings?propertyTID=inExistent"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == "[]"
    }

    def "test: get by trackingId"() {

        setup: "listing"
        Property property = createProperty()
        String listingTrackingID = RandomStringUtils.randomAlphabetic(10)
        createListing(property, listingTrackingID)

        when:
        def url = "http://localhost:$port/rest/propertylistings/${listingTrackingID}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def dbListing = (PropertyListing) mvcResultToModel(result, PropertyListing.class)

        then:
        dbListing != null
    }


    def "test: property listing - set listed=false"() {
        setup: "listing"
        Property property = createProperty()
        String listingTrackingID = RandomStringUtils.randomAlphabetic(10)
        createListing(property, listingTrackingID)


        when: "set published false"
        def url = "http://localhost:$port/rest/propertylistings/${listingTrackingID}?publish=false"
        def response = doPost(mockMvc, url).getResponse()

        then:
        response.status == HttpStatus.OK.value()
        response.contentAsString == ""

        when: "getting listing from db"
        url = "http://localhost:$port/rest/propertylistings/${listingTrackingID}"
        def result = doGet(mockMvc, url)
        def dbListing = (PropertyListing) mvcResultToModel(result, PropertyListing.class)

        then: "it is listing = false"
        !dbListing.isListed()
    }

    def "test: property listing - set listed=true"() {

        setup: "listing"
        Property property = createProperty()
        String listingTrackingID = RandomStringUtils.randomAlphabetic(10)
        createListing(property, listingTrackingID)


        when: "set published false"
        def url = "http://localhost:$port/rest/propertylistings/${listingTrackingID}?publish=true"
        def response = doPost(mockMvc, url).getResponse()

        then:
        response.status == HttpStatus.OK.value()
        response.contentAsString == ""

        when: "getting listing from db"
        url = "http://localhost:$port/rest/propertylistings/${listingTrackingID}"
        def result = doGet(mockMvc, url)
        def dbListing = (PropertyListing) mvcResultToModel(result, PropertyListing.class)

        then: "it is listing = false"
        dbListing.isListed()
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

    private MockHttpServletResponse createListing(Property property) {
        return createListing(property, null)
    }

    private MockHttpServletResponse createListing(Property property, String listingTrackingId) {
        PropertyListing listing = TestUtils.generatePropertyListing()
        listing.setTrackingId(listingTrackingId)
        listing.setUserTID(property.getUserTID())
        listing.setPropertyTID(property.getTrackingId())

        def url = "http://localhost:$port/rest/propertylistings"
        def response = doPost(mockMvc, url, listing).getResponse()

        return response
    }
}


