package com.mg.samartrent.renter.integration.resource

import com.fasterxml.jackson.core.type.TypeReference
import com.jayway.jsonpath.JsonPath
import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.domain.models.RenterReview
import com.mg.smartrent.domain.models.RenterView
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.resource.RenterRestController
import com.mg.smartrent.renter.services.ExternalUserService
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
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic


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
    static String restUrl

    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        if (!initialized) {
            purgeCollection(Renter.class)
            purgeCollection(RenterReview.class)
            mockMvc = MockMvcBuilders.standaloneSetup(restController).build()
            restUrl = "http://localhost:$port/rest/renters"
            initialized = true
        }
    }

    def "test: save renter"() {
        setup:
        def renter = generateRenter()
        when:
        def response = doPost(mockMvc, restUrl, renter).getResponse()

        then:
        response.status == HttpStatus.OK.value()
        JsonPath.parse(response.getContentAsString()).read('$.id') != null
        JsonPath.parse(response.getContentAsString()).read('$.length()') == 1

    }

    def "test: get by email"() {
        when: "create renter"
        def renter = generateRenter()
        def response = doPost(mockMvc, restUrl, renter).getResponse()
        then:
        response.status == HttpStatus.OK.value()

        when: "searching by email"
        MvcResult result = doGet(mockMvc, "$restUrl?email=${renter.getEmail()}")

        then:
        response.status == HttpStatus.OK.value()
        JsonPath.parse(result.response.getContentAsString()).read('$.id') != null
        JsonPath.parse(result.response.getContentAsString()).read('$.email') == renter.email

    }

    def "test: get by in-existent email"() {
        when:
        MvcResult result = doGet(mockMvc, "$restUrl?email=inExistent@email.com")

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == ""
    }

    def "test: save renter Review"() {
        setup: "create renter"
        def renter = generateRenter()
        renter.setId(randomAlphabetic(20))
        doPost(mockMvc, restUrl, renter).getResponse()

        when: "-------add review to renter------------"
        def review = generateRenterReview();
        review.setRenterId(renter.id)
        mockServicesFor(review)
        def response = doPost(mockMvc, "$restUrl/${renter.id}/reviews", review).getResponse()

        then: "-------review created----------"
        response.status == HttpStatus.OK.value()
        JsonPath.parse(response.getContentAsString()).read('$.id') != null


        when: "searching for renter reviews"
        MvcResult result = doGet(mockMvc, "$restUrl/${renter.id}/reviews")

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def dbReviews = (List<RenterReview>) mvcResultToModels(result, new TypeReference<List<RenterReview>>() {})

        then:
        dbReviews != null
        dbReviews.size() == 1
    }

    def "test: save renter View"() {
        setup:
        def view = generateRenterView();

        when: "save view"
        def response = doPost(mockMvc, "$restUrl/${view.renterId}/views", view).getResponse()
        then: "success"
        response.status == HttpStatus.OK.value()
        JsonPath.parse(response.getContentAsString()).read('$.id') != null

        when: "count renter views"
        MvcResult result = doGet(mockMvc, "$restUrl/${view.renterId}/views/count")
        then: "total views = 1"
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == "1"

    }


    private mockServicesFor(RenterReview review) {
        MockitoAnnotations.initMocks(this)
        Mockito.when(userService.userExists(review.getUserId())).thenReturn(true)
    }


}


