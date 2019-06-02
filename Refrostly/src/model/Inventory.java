/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Objects;

/**
 *
 * @author abiaps
 */
public class Inventory {
    private Long shovels = 0l;
    private Long snowblowers = 0l;
    private Long sled = 0l;
    private Long tires = 0l;
    private Long skis = 0l;
    private String[] status = new String[3];
    /**
     * @return the shovels
     */
    public Long getShovels() {
        return shovels;
    }

    /**
     * @param shovels the shovels to set
     */
    public void setShovels(Long shovels) {
        this.shovels = shovels;
    }

    /**
     * @return the snowblowers
     */
    public Long getSnowblowers() {
        return snowblowers;
    }

    /**
     * @param snowblowers the snowblowers to set
     */
    public void setSnowblowers(Long snowblowers) {
        this.snowblowers = snowblowers;
    }

    /**
     * @return the sled
     */
    public Long getSled() {
        return sled;
    }

    /**
     * @param sled the sled to set
     */
    public void setSled(Long sled) {
        this.sled = sled;
    }

    /**
     * @return the tires
     */
    public Long getTires() {
        return tires;
    }

    /**
     * @param tires the tires to set
     */
    public void setTires(Long tires) {
        this.tires = tires;
    }
    
    /**
     * @return the skis
     */
    public Long getSkis() {
        return skis;
    }

    /**
     * @param skis the skis to set
     */
    public void setSkis(Long skis) {
        this.skis = skis;
    }

    /**
     * @return the status
     */
    public String[] getStatus() {
        return status;
    }  
    
     /**
     * @param status the status to set
     */
    public void setStatus(String[] status) {
        this.status = status;
    }
    
    @Override
    public boolean equals(Object o) { 
   
        if (o == this) { 
            return true; 
        } 
  
        if (!(o instanceof Inventory)) { 
            return false; 
        } 
        
        Inventory c = (Inventory) o; 
         
        return Long.compare(c.getShovels(), getShovels()) == 0 &&
                Long.compare(c.getSkis(), getSkis()) == 0 &&
                Long.compare(c.getSled(), getSled()) == 0 &&
                Long.compare(c.getSnowblowers(), getSnowblowers()) == 0;
            
    } 

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.shovels);
        hash = 67 * hash + Objects.hashCode(this.snowblowers);
        hash = 67 * hash + Objects.hashCode(this.sled);
        hash = 67 * hash + Objects.hashCode(this.tires);
        hash = 67 * hash + Objects.hashCode(this.skis);
        return hash;
    }

}

