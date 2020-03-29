package com.mg.samartrent.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.User
import spock.lang.Specification

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

class UserTest extends Specification {

    static ObjectMapper mapper = new ObjectMapper();

    def "test: password present on Deserialization"() {
        setup:
        String json = """{ "password":"12341234",
                           "id":null,
                           "createdDate":null,
                           "modifiedDate":null,
                           "firstName":"FName",
                           "lastName":"LName",
                           "dateOfBirth":100000000000,
                           "gender":"Male",
                           "email":"asda.user@domain.com",
                           "status":"Active",
                           "enabled":true 
                          }""";
        when:
        User model = mapper.readValue(json, User.class);
        then:
        "12341234" == model.getPassword()
    }

    def "test: password missing on Serialization"() {
        when:
        String jsonString = mapper.writeValueAsString(generateUser());

        then:
        !jsonString.contains("password")
        !jsonString.contains("\"password\":\"12341234\"");
    }


    private static User generateUser() {
        User user = new User()
        user.setFirstName("FName")
        user.setLastName("LName")
        user.setEmail(randomAlphabetic(9) + ".user@domain.com")
        user.setPassword("12341234")
        user.setGender(EnGender.Male)
        user.setDateOfBirth(new Date(10000000000))
        user.setEnabled(true)
        user.setStatus(EnUserStatus.Active)
        return user
    }

}
