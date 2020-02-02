package com.mg.samartrent.renter.integration.resource

import com.fasterxml.jackson.core.type.TypeReference
import com.mg.samartrent.renter.TestUtils
import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.domain.models.RenterReview
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.resource.RenterRestController
import com.mg.smartrent.renter.service.ExternalUserService
import com.mg.smartrent.renter.service.RenterReviewService
import com.mg.smartrent.renter.service.RenterService
import org.apache.commons.lang.RandomStringUtils
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Stepwise

import static com.mg.samartrent.renter.TestUtils.*
import static com.mg.samartrent.renter.TestUtils.generateRenter
import static org.apache.commons.lang.RandomStringUtils.*


@SpringBootTest(
        classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
@Stepwise
class TestRenterRestController extends IntegrationTestsSetup {

    @LocalServerPort
    private int port

    @MockBean
    private ExternalUserService userService

    @Autowired
    @InjectMocks
    private RenterRestController restController


    static boolean initialized


    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        if (!initialized) {
            purgeCollection(Renter.class)
            purgeCollection(RenterReview.class)
            mockMvc = MockMvcBuilders.standaloneSetup(restController).build()
            initialized = true
        }
    }

    def "test: save renter"() {
        setup:
        def renter = generateRenter()
        when:
        def url = "http://localhost:$port/rest/renters"
        def response = doPost(mockMvc, url, renter).getResponse()

        then:
        response.status == HttpStatus.OK.value()
        response.getContentAsString() == ""
    }

    def "test: get by email"() {
        when: "create renter"
        def renter = generateRenter()
        def url = "http://localhost:$port/rest/renters"
        def response = doPost(mockMvc, url, renter).getResponse()
        then:
        response.status == HttpStatus.OK.value()


        when: "searching by email"
        url = "http://localhost:$port/rest/renters?email=${renter.getEmail()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def dbRenter = (Renter) mvcResultToModel(result, Renter.class)

        then:
        dbRenter != null
        dbRenter.getTrackingId() != null
        dbRenter.getEmail() == renter.getEmail()
    }

    def "test: get by in-existent email"() {
        when:
        def url = "http://localhost:$port/rest/renters?email=inExistent@email.com"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == ""
    }

    def "test: save renter Review"() {
        setup: "create renter"
        def renter = generateRenter()
        renter.setTrackingId(randomAlphabetic(20))
        def url = "http://localhost:$port/rest/renters"
        doPost(mockMvc, url, renter).getResponse()

        when: "-------add review to renter------------"
        def review = generateRenterReview();
        review.setRenterTID(renter.trackingId)
        mockServicesFor(review)
        url = "http://localhost:$port/rest/renters/${renter.trackingId}/reviews"
        def response = doPost(mockMvc, url, review).getResponse()

        then: "-------review created----------"
        response.status == HttpStatus.OK.value()
        response.getContentAsString() == ""


        when: "searching for renter reviews"
        url = "http://localhost:$port/rest/renters/${renter.trackingId}/reviews"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def dbReviews = (List<RenterReview>) mvcResultToModels(result, new TypeReference<List<RenterReview>>() {})

        then:
        dbReviews != null
        dbReviews.size() == 1
    }


    private mockServicesFor(RenterReview review) {
        MockitoAnnotations.initMocks(this)
        Mockito.when(userService.userExists(review.getUserTID())).thenReturn(true)
    }


}


