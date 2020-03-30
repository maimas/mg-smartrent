package com.mg.samartrent.user


import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.BizItem
import com.mg.smartrent.domain.models.User

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic

class ModelBuilder {

    static User generateUser() {
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

    /**
     * This method generates an UserInput required for the GraphQL mutation operations
     *
     * @return an instance of (@UserInput)
     */
    static UserInput generateUserInput(User user) {
        User u = Optional.ofNullable(user).orElse(generateUser());
        def input = new UserInput();
        input.id = u.id
        input.createdDate = u.createdDate
        input.modifiedDate = u.modifiedDate
        input.firstName = u.firstName
        input.lastName = u.lastName
        input.email = u.email
        input.password = u.password
        input.gender = u.gender
        input.dateOfBirth = u.dateOfBirth
        input.status = u.status
        input.enabled = u.enabled
        return input
    }

    static class UserInput extends BizItem {
        public String firstName, lastName, gender, email, password, status
        public Date dateOfBirth
        public Boolean enabled
    }


}
