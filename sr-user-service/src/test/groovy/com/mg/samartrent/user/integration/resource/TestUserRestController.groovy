package com.mg.samartrent.user.integration.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.mg.samartrent.user.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.resource.UsersRestController
import com.mg.smartrent.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static com.mg.samartrent.user.ModelBuilder.generateUser
import static org.apache.commons.lang.RandomStringUtils.randomNumeric

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
    private PasswordEncoder passwordEncoder
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
            rootURL = "http://localhost:$port/rest/users"
            initialized = true
        }
    }


    def "test: create user"() {
        setup:
        def user = generateUser()
        def userJson = new ObjectMapper().writeValueAsString(user).replace("}", ",\"password\":\"12341234\"}") // add password as it is ignored on serialization

        when: "creating a new user"
        def response = doPost(mockMvc, rootURL, userJson).getResponse()
        then:
        response.status == HttpStatus.CREATED.value()

        when: "checking if id returned"
        def dbUser = userService.findByEmail(user.email).get()
        then: "id present"
        response.contentAsString == "{\"id\":\"$dbUser.id\"}"
        passwordEncoder.matches("12341234", dbUser.password)

    }

    def "test: update user"() {
        setup:
        def user = userService.create(generateUser())
        user.setFirstName("UpdatedName")

        when:
        def result = doPut(mockMvc, "$rootURL/${user.id}", user)
        then:
        result.getResponse().status == HttpStatus.OK.value()

        when:
        def responseUser = (User) mvcResultToModel(result, User.class)
        then:
        responseUser.id == user.id
        responseUser.getFirstName() == 'UpdatedName'
        responseUser.getPassword() == null
    }


    def "test: update in-existent user"() {
        setup:
        def user = generateUser()
        user.setId(randomNumeric(10))

        when:
        def url = "$rootURL/${user.id}"
        def result = doPut(mockMvc, url, user).getResponse()
        then:
        result.status == HttpStatus.NOT_FOUND.value()
        result.getContentAsString() == ""
    }


    def "test: get by email"() {
        setup:
        def dbUser = userService.create(generateUser())

        when:
        def url = "$rootURL?email=${dbUser.getEmail()}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        dbUser = (User) mvcResultToModel(result, User.class)
        then:
        dbUser.getEmail() != null
        dbUser.getPassword() == null
    }

    def "test: get by email in-existent user"() {
        when:
        def url = "$rootURL?email=test.test@gmail.com"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == "null"
    }

    def "test: get existing user by id"() {
        setup:
        def dbUser = userService.create(generateUser())

        when:
        def url = "$rootURL/${dbUser.id}"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        def user = (User) mvcResultToModel(result, User.class)
        then:
        user.id == dbUser.id
        user.getEmail() == dbUser.getEmail()
        user.getPassword() == null
    }

    def "test: get user by in-existent id"() {

        when:
        def url = "$rootURL/12341234"
        MvcResult result = doGet(mockMvc, url)

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()
        result.getResponse().contentAsString == "null"
    }


}


