/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Date;

/**
 *
 * @author abiaps
 */
public class Restock {
    private Date restockDate;
    private String itemStocked;
    private Long itemQuantity;
    private String manufacturer;
    private Double wholesalePrice;

    /**
     * @return the restockDate
     */
    public Date getRestockDate() {
        return restockDate;
    }

    /**
     * @param restockDate the restockDate to set
     */
    public void setRestockDate(Date restockDate) {
        this.restockDate = restockDate;
    }

    /**
     * @return the itemStocked
     */
    public String getItemStocked() {
        return itemStocked;
    }

    /**
     * @param itemStocked the itemStocked to set
     */
    public void setItemStocked(String itemStocked) {
        this.itemStocked = itemStocked;
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
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return the wholesalePrice
     */
    public Double getWholesalePrice() {
        return wholesalePrice;
    }

    /**
     * @param wholesalePrice the wholesalePrice to set
     */
    public void setWholesalePrice(Double wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }
    
    
}
