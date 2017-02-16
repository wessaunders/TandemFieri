package com.gmail.dleemcewen.tandemfieri.Entities;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;
import com.gmail.dleemcewen.tandemfieri.MenuBuilder.MenuCatagory;

import java.io.Serializable;
import java.util.Map;

/**
 * Restaurant defines all the properties and behaviors for a Restaurant entity
 */
public class Restaurant extends Entity implements Serializable {
    private String name;
    private String street;
    private String city;
    private String state;
    private String zipcode;
    private Double charge;
    private String ownerId;
    private Map<String, String> drivers;

    private MenuCatagory menu;

    /**
     * Default constructor
     */
    public Restaurant() {
    }

    /**
     * Optional constructor
     * @param id uniquely identifies a restaurant
     */
    public Restaurant(String id) {
        setKey(id);
    }

    /**
     * return the restaurant id
     * @return String uniquely identifying the restaurant
     */
    public String getId() {
        return getKey();
    }

    /**
     * get the restaurant name
     * @return name of the restaurant
     */
    public String getName() {
        return name;
    }

    /**
     * set the restaurant name
     * @param name identifies the name of the restaurant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets the restaurant street address
     * @return street address of the restaurant
     */
    public String getStreet() {
        return street;
    }

    /**
     * sets the restaurant street address
     * @param street identifies the street address of the restaurant
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * gets the restaurant city
     * @return city the restaurant is in
     */
    public String getCity() {
        return city;
    }

    /**
     * sets the restaurant city
     * @param city identifies the city the restaurant is in
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * gets the restaurant state
     * @return state the restaurant is in
     */
    public String getState() {
        return state;
    }

    /**
     * sets the restaurant state
     * @param state identifies the state the restaurant is in
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * gets the restaurant zipcode
     * @return zipcode of the restaurant
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * sets the restaurant zipcode
     * @param zipcode identifies the zipcode of the restaurant
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * gets the restaurant delivery charge rate
     * @return restaurant delivery charge rate
     */
    public Double getCharge() {
        return charge;
    }

    /**
     * sets the restaurant delivery charge rate
     * @param charge identifies the restaurant delivery charge rate
     */
    public void setCharge(Double charge) {
        this.charge = charge;
    }

    /**
     * gets the restaurant owner id
     * @return restaurant owner id
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * sets the restaurant owner id
     * @param ownerId uniquely identifies the restaurant owner
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public MenuCatagory getMenu() {
        return menu;
    }

    public void setMenu(MenuCatagory menu) {
        this.menu = menu;
    }

    /**
     * get the drivers associated with the restaurant
     * @return return all of the driver ids and names associated with the restaurant
     */
    public Map<String, String> getDrivers() {
        return drivers;
    }

    /**
     * sets the drivers associated with the restaurant
     * @param drivers the id and name of each driver associated with the restaurant
     */
    public void setDrivers(Map<String, String> drivers) {
        this.drivers = drivers;
    }
}
