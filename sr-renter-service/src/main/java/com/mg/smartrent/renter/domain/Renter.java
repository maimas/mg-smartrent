package com.mg.smartrent.renter.domain;

import com.mg.persistence.domain.bizitem.model.BizItemModel;

import java.util.Date;

public class Renter extends BizItemModel {

    public Renter() {
        setItemType("Renter");
    }

    public void setFirstName(String firstName) {
        set("firstName", firstName);
    }

    public String getFirstName() {
        return String.valueOf(get("firstName"));
    }


    public void setLastName(String lastName) {
        set("lastName", lastName);
    }

    public String getLastName() {
        return String.valueOf(get("lastName"));
    }


    public Date getDateOfBirth() {
        return (Date) get("dateOfBirth");
    }

    public void setDateOfBirth(Date dateOfBirth) {
        set("dateOfBirth", dateOfBirth);
    }

    public String getEmail() {
        return String.valueOf(get("email"));
    }

    public void setEmail(String email) {
        set("email", email);
    }

    public String getPhoneNumber() {
        return String.valueOf(get("phoneNumber"));
    }

    public void setPhoneNumber(String phoneNumber) {
        set("phoneNumber", phoneNumber);
    }

    public String getGender() {
        return String.valueOf(get("gender"));
    }

    public void setGender(String gender) {
        set("gender", gender);
    }

}
