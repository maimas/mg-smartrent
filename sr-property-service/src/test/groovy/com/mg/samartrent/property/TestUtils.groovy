package com.mg.samartrent.property

import com.mg.smartrent.domain.enums.EnBuildingType
import com.mg.smartrent.domain.enums.EnCurrency
import com.mg.smartrent.domain.enums.EnPropertyCondition
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.domain.models.RentalApplication
import com.mg.smartrent.domain.models.User

class TestUtils {

    static Property generateProperty() {

        Property property = new Property()

        property.setUserTID("userId1234")
        property.setBuildingType(EnBuildingType.Condo.name())
        property.setCondition(EnPropertyCondition.Normal.name())
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
        listing.setPropertyTID("mockedPropertyId")
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

    static RentalApplication generateRentalApplication() {

        RentalApplication model = new RentalApplication()
        model.setRenterUserTID("mockedUserId")
        model.setPropertyTID("mockedPropertyId")
        model.setCheckInDate(new Date(System.currentTimeMillis() - 1000000000))
        model.setCheckOutDate(new Date(System.currentTimeMillis() + 1000000000))
        model.setPrice(100)
        model.setCurrency(EnCurrency.USD.name())
        return model
    }

}
