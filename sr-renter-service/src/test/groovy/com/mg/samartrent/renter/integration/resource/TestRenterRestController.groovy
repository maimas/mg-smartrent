package com.mg.samartrent.renter.integration.resource

import com.mg.samartrent.renter.TestUtils
import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.resource.RenterRestController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Stepwise


@SpringBootTest(
        classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
@Stepwise
class TestRenterRestController extends IntegrationTestsSetup {

    @LocalServerPort
    private int port;
    @Autowired
    private RenterRestController restController


    static boolean initialized

    static Renter dbRenter;

    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        if (!initialized) {
            purgeCollection(Renter.class)
            mockMvc = MockMvcBuilders.standaloneSetup(restController).build()
            initialized = true
        }
    }

    def "test: save renter"() {

        setup:
        dbRenter = TestUtils.generateRenter()

        when:
        def url = "http://localhost:$port/rest/renters"
        def response = doPost(mockMvc, url, dbRenter).getResponse()

        then:
        response.status == HttpStatus.OK.value()
        response.getContentAsString() == ""
    }

    def "test: get by email"() {

        when:
        def url = "http://localhost:$port/rest/renters?email=${dbRenter.getEmail()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def renter = (Renter) mvcResultToModel(result, Renter.class)

        then:
        renter != null
        renter.getTrackingId() != null
        renter.getEmail() == dbRenter.getEmail()
    }

    def "test: get by in-existent email"() {
        when:
        def url = "http://localhost:$port/rest/renters?email=inExistent@email.com"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == ""
    }

}


