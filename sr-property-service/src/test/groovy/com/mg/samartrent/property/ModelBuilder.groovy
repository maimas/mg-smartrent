package com.mg.samartrent.property

import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.domain.models.User


class ModelBuilder {

    static Property generateProperty() {

        Property property = new Property()

        property.setUserTID("userId1234")
        property.setBuildingType("apartment")
        property.setCondition("requiresReparation")
        property.setTotalRooms(10)
        property.setTotalBathRooms(5)
        property.setTotalBalconies(1)
        property.setThumbnail(null)
        property.setParkingAvailable(true)

        return property
    }

    static PropertyListing generatePropertyListing() {
        PropertyListing listing = new PropertyListing()
        listing.setUserTID("mockedUserId")
        listing.setPropertyTID("nonExistentPropertyId1234")
        listing.setListed(true)
        listing.setPrice(100)
        listing.setTotalViews(3)
        listing.setCheckInDate(new Date(System.currentTimeMillis() + 10000000000))
        listing.setCheckOutDate(new Date(System.currentTimeMillis() + 10000000000))
        return listing
    }

    static User generateUser() {

        User user = new User()
        user.setFirstName("FName")
        user.setLastName("LName")
        user.setEmail("test.user@domain.com")
        user.setPassword("12341234")
        user.setEnabled(true)
        user.setStatus("for new user is set by service")

        return user
    }

}
