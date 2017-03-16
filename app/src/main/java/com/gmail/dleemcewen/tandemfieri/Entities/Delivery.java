package com.gmail.dleemcewen.tandemfieri.Entities;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Delivery identifies a delivery for a driver
 */

public class Delivery extends Entity implements Serializable {
    private List<Order> orders;
    private String currentOrderId;

    /**
     * Default constructor
     */
    public Delivery() {
    }

    /**
     * getOrders returns the orders associated with the delivery
     * @return orders associated with the delivery
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * setOrders sets the orders associated with the delivery
     * @param orders indicates the orders associated with the delivery
     */
    public void setOrder(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * getCurrentOrderId returns the current order id
     * @return the id of the driver's current order
     */
    public String getCurrentOrderId() {
        return currentOrderId;
    }

    /**
     * setCurrentOrderId sets the current order id for a driver to be able to identify which order
     * is the driver's current order
     * @param currentOrderId indicates the current order id
     */
    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

}
