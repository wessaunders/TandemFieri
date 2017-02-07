package com.gmail.dleemcewen.tandemfieri.Entities;

import android.widget.EditText;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;

import static com.gmail.dleemcewen.tandemfieri.R.id.firstName;

/**
 * Created by Nexusrex on 2/5/2017.
 */

public class User extends Entity {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phoneNumber;
    private String email;
    private String username;



    private String authUserID;

    public User(String firstName, String lastName, String address, String city, String state, String zip, String phoneNumber, String email, String username, String authUserID){
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.username = username;
        this.authUserID = authUserID;
    }


    public void setAuthUserID(String authuserID) {
        this.authUserID = authUserID;
    }

    public String getAuthUserID() {

        return authUserID;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }




}
