package com.mg.samartrent.property.integration.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.mg.samartrent.property.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.resource.UsersRestController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.client.RestTemplate
import spock.lang.Stepwise

import static com.mg.samartrent.property.ModelBuilder.generateUser

@SpringBootTest(
        classes = UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
@Stepwise
class TestUserRestController extends IntegrationTestsSetup {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private UsersRestController restController
    static boolean initialized

    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        if (!initialized) {
            purgeCollection(User.class)
            mockMvc = MockMvcBuilders.standaloneSetup(restController).build()
            initialized = true
        }
    }

    static def dbUser = generateUser()

    def "test: save user"() {
        when:
        def url = "http://localhost:$port/rest/users"
        def response = doPost(mockMvc, url, dbUser).getResponse()

        then:
        response.status == HttpStatus.OK.value()
        response.getContentAsString() == ""
    }

    def "test: get by email"() {

        when:
        def url = "http://localhost:$port/rest/users?email=${dbUser.getEmail()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        dbUser = (User) mvcResultToModel(result, User.class)
        then:
        dbUser.getTrackingId() != null
        dbUser.getEmail() != null
    }

    def "test: get by email in-existent user"() {

        when:
        def url = "http://localhost:$port/rest/users?email=test@test@gmail.com"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == ""
    }

    def "test: get existing user by trackingId"() {
        when:
        def url = "http://localhost:$port/rest/users?trackingId=${dbUser.getTrackingId()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def user = (User) mvcResultToModel(result, User.class)
        then:
        user.getTrackingId() == dbUser.getTrackingId()
        user.getEmail() == dbUser.getEmail()
    }

    def "test: get existing user by trackingId for in-existent user"() {

        when:
        def url = "http://localhost:$port/rest/users?trackingId=testInvId"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == ""
    }

    def "test: user exists"() {
        when:
        def url = "http://localhost:$port/rest/users/exists/${dbUser.getTrackingId()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().getContentAsString() == ""
    }

    def "test: user exists for in-existent user"() {
        when:
        def url = "http://localhost:$port/rest/users/exists/123123"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.NOT_FOUND.value()
        result.getResponse().getContentAsString() == "No such user."
    }

}


