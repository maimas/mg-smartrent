package com.mg.samartrent.renter

import com.mg.smartrent.domain.enums.EnBuildingType
import com.mg.smartrent.domain.enums.EnGender
import com.mg.smartrent.domain.enums.EnPropertyCondition
import com.mg.smartrent.domain.enums.EnUserStatus
import com.mg.smartrent.domain.models.Property
import com.mg.smartrent.domain.models.PropertyListing
import com.mg.smartrent.domain.models.Renter
import com.mg.smartrent.domain.models.RenterReview
import com.mg.smartrent.domain.models.RenterView
import com.mg.smartrent.domain.models.User
import com.mg.smartrent.renter.services.RenterService
import org.apache.commons.lang.RandomStringUtils

import static org.apache.commons.lang.RandomStringUtils.*

class TestUtils {

    static Property generateProperty() {

        Property property = new Property()

        property.setUserId("userId1234")
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
        listing.setUserId("mockedUserId")
        listing.setPropertyId("mockedPropertyId")
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
        user.setGender(EnGender.Male)
        user.setStatus(EnUserStatus.Pending)

        return user
    }


    static Renter generateRenter() {
        Renter renter = new Renter()
        renter.setFirstName("FName")
        renter.setLastName("LName")
        renter.setGender(EnGender.Male.name())
        renter.setPhoneNumber("4252402021")
        renter.setDateOfBirth(new Date(System.currentTimeMillis() - 1000000000))
        renter.setEmail(randomAlphabetic(5) + ".test@domain.com")

        return renter
    }

    static RenterReview generateRenterReview() {
        RenterReview review = new RenterReview()
        review.setUserId(randomAlphabetic(30))
        review.setRenterId(randomAlphabetic(30))
        review.setRating(1)
        review.setReview("A very good renter!!!")

        return review
    }

    static RenterView generateRenterView() {
        RenterView view = new RenterView()
        view.setUserId(randomAlphabetic(30))
        view.setRenterId(randomAlphabetic(30))
        return view
    }

}
