/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author abiaps
 */
import java.util.Date;

public class Order {
    private String customerId;
    private Date orderDate;
    private String itemOrdered;
    private Long itemQuantity;
    private String itemPrice;

    /**
     * @return the customerId
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the orderDate
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * @param orderDate the orderDate to set
     */
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * @return the itemOrdered
     */
    public String getItemOrdered() {
        return itemOrdered;
    }

    /**
     * @param itemOrdered the itemOrdered to set
     */
    public void setItemOrdered(String itemOrdered) {
        this.itemOrdered = itemOrdered;
    }

    /**
     * @return the itemQuantity
     */
    public Long getItemQuantity() {
        return itemQuantity;
    }

    /**
     * @param itemQuantity the itemQuantity to set
     */
    public void setItemQuantity(Long itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    /**
     * @return the itemPrice
     */
    public String getItemPrice() {
        return itemPrice;
    }

    /**
     * @param itemPrice the itemPrice to set
     */
    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }    
    
}
