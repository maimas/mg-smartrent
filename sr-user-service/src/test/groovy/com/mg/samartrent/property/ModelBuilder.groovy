package com.mg.samartrent.property

import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.User


class ModelBuilder {

    static User generateUser() {

        User user = new User()

        user.setFirstName("FName")
        user.setLastName("LName")
        user.setEmail("test.user@domain.com")
        user.setPassword("12341234")
        user.setEnabled(true)
        user.setStatus(EnUserStatus.Active.name())

        return user
    }

}
