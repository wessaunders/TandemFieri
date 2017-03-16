package com.gmail.dleemcewen.tandemfieri.Entities;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;
import com.gmail.dleemcewen.tandemfieri.Constants.OrderConstants;
import com.gmail.dleemcewen.tandemfieri.Enums.OrderEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

/**
 * Created by Derek on 3/1/2017.
 */

public class Order extends Entity implements Serializable {
    private List<OrderItem> items = new ArrayList<>();
    private OrderEnum status;
    private String restaurantId;
    private String customerId;
    private double subTotal;
    private double tax;
    private double total;
    private double deliveryCharge;
    private String restaurantName;
    private Date orderDate;

    public Order() {orderDate = new Date();}

    public Order(String key) {
        setKey(key);
        orderDate = new Date();
    }

    public double calculateSubTotal() {
        double subTotal = 0;
        for (OrderItem item : items) {
            subTotal += item.getBasePrice();
            for (OrderItemOptionGroup group : item.getOptionGroups()) {
                for (OrderItemOption selection : group.getOptions()) {
                    subTotal += selection.getAddedPrice();
                }
            }
        }
        subTotal += deliveryCharge;
        return subTotal;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        this.subTotal = calculateSubTotal();
        this.tax = calculateTax();
        this.total = calculateTotal();
        this.status = OrderEnum.CREATING;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double calculateTax() {
        return this.subTotal * OrderConstants.TAX_RATE;
    }

    public double calculateTotal() {
        return this.subTotal + this.tax;
    }

    public OrderEnum getStatus() {
        return status;
    }

    public void setStatus(OrderEnum status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {

        this.customerId = customerId;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String toString(){

        return dateToString() + " " + restaurantName + " $" + String.format(Locale.US, "%.2f", total);
    }

    public String dateToString(){
        SimpleDateFormat formatDateJava = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return formatDateJava.format(orderDate);
    }
}
