package com.mg.samartrent.renter.integration.resource


import com.mg.persistence.domain.SystemCollection
import com.mg.persistence.domain.SystemFiled
import com.mg.persistence.services.QueryService
import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.resource.SchemaRestController
import com.mongodb.BasicDBObject
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Stepwise

@SpringBootTest(
        classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Stepwise
class TestSchemaRestController extends IntegrationTestsSetup {

    @Autowired
    private SchemaRestController schemaRestController
    @Autowired
    private QueryService queryService


    /**
     * Spring beans cannot be initialized in setupSpec : https://github.com/spockframework/spock/issues/76
     */
    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(schemaRestController).build()
    }

    def "test: schema initialization"() {
        when:
        MvcResult result = doPost(mockMvc, '/renters/schema')

        then:
        result.getResponse().getStatus() == HttpStatus.OK.value()

        when:
        String qString = new Query().addCriteria(Criteria.where(SystemFiled.ID).exists(true)).getQueryObject().toJson()
        BasicDBObject q = new BasicDBObject(Document.parse(qString))
        then:

        queryService.count(q, SystemCollection.BizItemSchemas) > 0
    }


}


