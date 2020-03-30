package com.mg.samartrent.renter.integration.service


import com.mg.samartrent.renter.integration.IntegrationTestsSetup
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.renter.RenterApplication
import com.mg.smartrent.renter.services.ExternalUserService
import com.mg.smartrent.renter.services.RenterService
import org.apache.commons.lang.RandomStringUtils
import org.mockito.InjectMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

import static com.mg.samartrent.renter.TestUtils.generateRenter


@SpringBootTest(classes = RenterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["eureka.client.enabled:false"]
)
class TestRenterService extends IntegrationTestsSetup {

    @MockBean
    private ExternalUserService userService

    @Autowired
    @InjectMocks
    private RenterService renterService

    static boolean initialized

    def setup() {
        if (!initialized) {
            purgeCollection(Renter.class)
            initialized = true
        }
    }


    def "test: create renter"() {

        setup:
        Renter renter = generateRenter()

        when: "saving "

        def dbRenter = renterService.save(renter)

        then: "successfully saved"
        dbRenter.getId() != null
        dbRenter.getCreatedDate() != null
        dbRenter.getModifiedDate() != null
        dbRenter.getEmail() == renter.getEmail()
        dbRenter.getDateOfBirth() == renter.getDateOfBirth()
        dbRenter.getGender() == renter.getGender()
        dbRenter.getPhoneNumber() == renter.getPhoneNumber()
        dbRenter.getFirstName() == renter.getFirstName()
        dbRenter.getLastName() == renter.getLastName()
    }

    def "test: find renter by email"() {
        setup:
        Renter renter = generateRenter()

        when: "renter not present"
        def dbRenter = renterService.findByEmail(renter.getEmail())
        then:
        dbRenter == null

        when: "renter present"
        renterService.save(renter)
        dbRenter = renterService.findByEmail(renter.getEmail())

        then:
        dbRenter != null
        dbRenter.getId() == renter.getId()
        dbRenter.getEmail() == renter.getEmail()
    }


    def "test: find renter by Id"() {
        setup:
        Renter renter = generateRenter();
        renter.setId(RandomStringUtils.randomAlphabetic(30));

        when: "not present"
        def dbRenter = renterService.findById(renter.getId())
        then:
        dbRenter == null

        when: "present"
        renterService.save(renter)
        dbRenter = renterService.findById(renter.getId())

        then:
        dbRenter != null
        dbRenter.getId() == renter.getId()
    }

//    def "test: find renter by email when user exists"() {
//        setup: "create an user that has not renter profile yet"
//        User user = TestUtils.generateUser()
//
//        MockitoAnnotations.initMocks(this)
//        Mockito.when(userService.getUserByEmail(user.getEmail())).thenReturn(user)
//
//        when: "searching renter by user email and it does not exists then should be created from user"
//        def dbRenter = renterService.findByEmail(user.getEmail(), true)
//
//        then:
//        dbRenter != null
//        dbRenter.getId() != null
//        dbRenter.getFirstName() == user.getFirstName()
//        dbRenter.getLastName() == user.getLastName()
//        dbRenter.getDateOfBirth() == user.getDateOfBirth()
//        dbRenter.getGender() == user.getGender()
//        dbRenter.getEmail() == user.getEmail()
//        dbRenter.getPhoneNumber() == null
//    }


//    def "test: find renter by email when user does not exists"() {
//        setup:
//        def userEmail = "non.existentEmail@domain.com"
//        MockitoAnnotations.initMocks(this)
//        Mockito.when(userService.getUserByEmail(userEmail)).thenReturn(null)
//
//        when: "searching renter by user email and user does not exists "
//        renterService.findByEmail(userEmail, true)
//
//        then: "exception is thrown"
//        RuntimeException e = thrown()
//        e.getMessage() == "Renter could not be created. User with email $userEmail not found."
//    }

}
