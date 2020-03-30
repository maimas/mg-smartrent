package com.mg.samartrent.user.integration.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.util.RawValue
import com.graphql.spring.boot.test.GraphQLResponse
import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.mg.samartrent.user.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.BizItem
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.user.UserApplication
import com.mg.smartrent.user.resource.UsersRestController
import com.mg.smartrent.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static com.mg.samartrent.user.ModelBuilder.generateUser
import static com.mg.samartrent.user.ModelBuilder.generateUserInput
import static com.mg.smartrent.domain.models.BizItem.Fields.*
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest(
        classes = UserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestUserRestControllerGraph extends IntegrationTestsSetup {

    @LocalServerPort
    private int port;
    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;
    @Autowired
    private UserService userService
    static String restURL

    def setup() {
        purgeCollection(User.class)
        restURL = "http://localhost:$port/rest/users"
    }


    def "test: get user by ID"() {
        setup:
        def user = userService.create(generateUser())

        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put(id, user.getId());

        when:
        GraphQLResponse response = graphQLTestTemplate.perform("graphql/user_query_findById.graphql", variables);
        String jPath = '$.data.findById'

        then:
        response != null
        response.isOk()
        def context = response.context()
        context.read("${jPath}.$id") != null
        context.read("${jPath}.$createdDate") != null
        context.read("${jPath}.$modifiedDate") != null
        context.read("${jPath}.$User.Fields.dateOfBirth") != null
        context.read("${jPath}.$User.Fields.firstName") == user.firstName
        context.read("${jPath}.$User.Fields.lastName") == user.lastName
        context.read("${jPath}.$User.Fields.status") == user.status.name()
        context.read("${jPath}.$User.Fields.gender") == user.gender.name()
        context.read("${jPath}.$User.Fields.email") == user.email
        context.read("${jPath}.$User.Fields.enabled") == user.enabled
        context.read("${jPath}.[?(@.$User.Fields.password)]") == []
        context.read("${jPath}.length()") == 10
    }

    def "test: get user by email"() {
        setup:
        def user = userService.create(generateUser())

        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put(User.Fields.email, user.email);

        when:
        GraphQLResponse response = graphQLTestTemplate.perform("graphql/user_query_findByEmail.graphql", variables);
        String jPath = '$.data.findByEmail'

        then:
        response != null
        response.isOk()
        def context = response.context()
        context.read("${jPath}.$id") != null
        context.read("${jPath}.$createdDate") != null
        context.read("${jPath}.$modifiedDate") != null
        context.read("${jPath}.$User.Fields.firstName") == user.firstName
        context.read("${jPath}.$User.Fields.lastName") == user.lastName
        context.read("${jPath}.$User.Fields.status") == EnUserStatus.Pending.name()
        context.read("${jPath}.$User.Fields.dateOfBirth") != null
        context.read("${jPath}.$User.Fields.gender") == user.gender.name()
        context.read("${jPath}.$User.Fields.email") == user.email
        context.read("${jPath}.$User.Fields.enabled") == user.enabled
        context.read("${jPath}.[?(@.$User.Fields.password)]") == []
        context.read("${jPath}.length()") == 10
    }

    def "test: create user"() {
        setup:
        def userInput = generateUserInput(null)
        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.putPOJO("user", userInput)

        when:
        GraphQLResponse response = graphQLTestTemplate.perform("graphql/user_mutation_create.graphql", variables);
        String jPath = '$.data.create'

        then:
        response != null

        response.isOk()
        def context = response.context()
        context.read("${jPath}.$id") != null
        context.read("${jPath}.$createdDate") != null
        context.read("${jPath}.$modifiedDate") != null
        context.read("${jPath}.$User.Fields.dateOfBirth") != null
        context.read("${jPath}.$User.Fields.firstName") == userInput.firstName
        context.read("${jPath}.$User.Fields.lastName") == userInput.lastName
        context.read("${jPath}.$User.Fields.status") == EnUserStatus.Pending.name()
        context.read("${jPath}.$User.Fields.gender") == userInput.gender
        context.read("${jPath}.$User.Fields.email") == userInput.email
        context.read("${jPath}.$User.Fields.enabled") == false
        context.read("${jPath}.[?(@.$User.Fields.password)]") == []
        context.read("${jPath}.length()") == 10
    }

    def "test: update user"() {
        setup:
        def dbUser = userService.create(generateUser())
        def userInput = generateUserInput(dbUser)
        userInput.firstName = "UpdatedFromGraphQLTest"

        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.putPOJO("user", userInput)

        when:
        GraphQLResponse response = graphQLTestTemplate.perform("graphql/user_mutation_update.graphql", variables);
        String jPath = '$.data.update'

        then:
        response != null
        response.isOk()
        def context = response.context()
        context.read("${jPath}.$User.Fields.firstName") == "UpdatedFromGraphQLTest"
        context.read("${jPath}.length()") == 10
    }

    def "test: enable user account"() {
        setup:
        def dbUser = userService.create(generateUser())

        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put(id, dbUser.getId());
        variables.put(User.Fields.enabled, true);

        when:
        GraphQLResponse response = graphQLTestTemplate.perform("graphql/user_mutation_enable.graphql", variables);
        String jPath = '$.data.enable'

        then:
        response != null
        response.isOk()
        def context = response.context()
        context.read("${jPath}.$User.Fields.enabled") == true
        context.read("${jPath}.length()") == 10
    }

    def "test: reset user password"() {
        setup:
        def user = userService.create(generateUser())

        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put(id, user.id);
        variables.put("rawPassword", "abcabc1234");

        when:
        GraphQLResponse response = graphQLTestTemplate.perform("graphql/user_mutation_resetPassword.graphql", variables);
        String jPath = '$.data.resetPassword'

        then:
        response != null
        response.isOk()
        def context = response.context()
        context.read("${jPath}") == true
    }


}


