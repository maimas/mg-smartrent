package com.mg.samartrent.user.integration.resource


import com.mg.samartrent.user.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.resource.UsersRestController
import com.mg.smartrent.user.service.UserService
import com.netflix.discovery.converters.Auto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Stepwise

import static com.mg.samartrent.user.ModelBuilder.generateUser

@SpringBootTest(
        classes = UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestUserRestController extends IntegrationTestsSetup {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate
    @Autowired
    private UsersRestController restController
    @Autowired
    private UserService userService
    static boolean initialized
    static String rootURL = ""

    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        if (!initialized) {
            purgeCollection(User.class)
            mockMvc = MockMvcBuilders.standaloneSetup(restController).build()
            rootURL = "http://localhost:$port"
            initialized = true
        }
    }


    def "test: save user"() {
        setup:
        def user = generateUser()

        when:
        def url = "$rootURL/rest/users"
        def response = doPost(mockMvc, url, user).getResponse()

        then:
        response.status == HttpStatus.CREATED.value()
        response.getContentAsString() != ""

        when:
        def dbUser = userService.findByEmail(user.getEmail());
        then:
        dbUser != null
    }

    def "test: update user"() {
        setup:
        def user = userService.save(generateUser())
        user.setFirstName("UpdatedName")

        when:
        def url = "$rootURL/rest/users/${user.trackingId}"
        def result = doPut(mockMvc, url, user)
        then:
        result.getResponse().status == HttpStatus.OK.value()

        when:
        def responseUser = (User) mvcResultToModel(result, User.class)
        then:
        responseUser.trackingId == user.trackingId
        responseUser.getFirstName() == 'UpdatedName'
    }

    def "test: update user with mismatch in trackingId param"() {
        setup:
        def user = userService.save(generateUser())

        when:
        def url = "$rootURL/rest/users/invalidUsertTrackingId"
        def result = doPut(mockMvc, url, user).getResponse()
        then:
        result.status == HttpStatus.BAD_REQUEST.value()
        result.getContentAsString() == ""
    }

    def "test: update in-existent user"() {
        setup:
        def user = generateUser()
        user.setTrackingId("mock_tid")

        when:
        def url = "$rootURL/rest/users/${user.trackingId}"
        def result = doPut(mockMvc, url, user).getResponse()
        then:
        result.status == HttpStatus.NOT_FOUND.value()
        result.getContentAsString() == ""
    }


    def "test: get by email"() {
        setup:
        def dbUser = userService.save(generateUser())

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
        def url = "http://localhost:$port/rest/users?email=test.test@gmail.com"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().getContentAsString() == ""
    }

    def "test: get existing user by trackingId"() {
        setup:
        def dbUser = userService.save(generateUser())

        when:
        def url = "http://localhost:$port/rest/users/${dbUser.getTrackingId()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def user = (User) mvcResultToModel(result, User.class)
        then:
        user.getTrackingId() == dbUser.getTrackingId()
        user.getEmail() == dbUser.getEmail()
    }

    def "test: get user by in-existent trackingId"() {

        when:
        def url = "http://localhost:$port/rest/users/1234notExists"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == ""
    }


}


