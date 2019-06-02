/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import model.Inventory;
import model.Order;
import model.Restock;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author abiaps
 */
public class Refrostly {

    private List<Order> ordersList = new ArrayList<>();
    private List<Restock> restocksList = new ArrayList<>();
    
    /**
     * @return the ordersList
     */
    public List<Order> getOrdersList() {
        return ordersList;
    }

    /**
     * @param ordersList the ordersList to set
     */
    public void setOrdersList(List<Order> ordersList) {
        this.ordersList = ordersList;
    }

    /**
     * @return the restocksList
     */
    public List<Restock> getRestocksList() {
        return restocksList;
    }

    /**
     * @param restocksList the restocksList to set
     */
    public void setRestocksList(List<Restock> restocksList) {
        this.restocksList = restocksList;
    }
        
    public static void main(String[] args) throws IOException, ParseException, Exception
    {        
        // read orders.json
        Refrostly refrostly = new Refrostly();
        JSONParser jsonParser = new JSONParser();        
        File ordersFile = new File("orders.json");
        File restocksFile = new File("restocks.json");
        try (FileReader reader = new FileReader(ordersFile.getCanonicalPath()))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray orderList = (JSONArray) obj;
             
            for(int i = 0; i < orderList.size(); i++)
            {
                refrostly.parseOrdersObject((JSONObject) orderList.get(i));
            }
           
            // sort these orders w.r.t orderdate
            refrostly.sortOrdersList();            
            
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        } catch (ParseException e) {
            throw new Exception(e);
        }
        
        try (FileReader reader = new FileReader(restocksFile.getCanonicalPath()))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray restockList = (JSONArray) obj;          
            for(int j = 0; j < restockList.size(); j++)
            {
                refrostly.parseRestockObject((JSONObject) restockList.get(j));
            }
            // sort these restocks w.r.t restockdate
            refrostly.sortRestocksList();
        } 
        catch (FileNotFoundException e) 
        {
            throw new FileNotFoundException("main: " + e);
        } 
        catch (IOException e) 
        {
            throw new IOException("main: " + e);
        } 
        catch (ParseException e) 
        {
            throw new Exception(e);
        }
        
        refrostly.traverseOrderRestockList(); 
    }
    
    /**
     * sort Orders objects with respect to their order dates
     */
    private void sortOrdersList()
    {
        Collections.sort(getOrdersList(), new Comparator<Order>(){
                @Override
                public int compare(Order a, Order b) {                    
                    return a.getOrderDate().compareTo(b.getOrderDate());                    
                }
            });
    }
    
    /**
     * sort Restock objects with respect to their restock dates
     */
    private void sortRestocksList()
    {
        Collections.sort(getRestocksList(), new Comparator<Restock>(){
                @Override
                public int compare(Restock a, Restock b) {
                    return a.getRestockDate().compareTo(b.getRestockDate());                    
                }
            });
    }
    
    /**
     * gets the status of inventory by traversing through ordersList and restocksList in chronological order
     */
    private void traverseOrderRestockList() throws Exception
    {
        try
        {
            Inventory inventory = new Inventory();
            int i = 0, j = 0;
            while(i < getOrdersList().size() && j < getRestocksList().size())
            {
                int compare = getOrdersList().get(i).getOrderDate().compareTo(getRestocksList().get(j).getRestockDate());

                if( compare < 0)        // order date is before restock date
                {
                    if(performOrder(getOrdersList().get(i), inventory))                    
                        i++;
                    else
                        return;
                }
                else if(compare > 0)        // order date is after restock date
                {
                    performRestock(getRestocksList().get(j), inventory);                 
                    j++;
                }
                else if(compare == 0)       // order date is same as restock date
                {
                    // perform restock first and satisfy the order
                    performRestock(getRestocksList().get(j), inventory);
                    j++;
                    if(performOrder(getOrdersList().get(i), inventory))                    
                        i++;
                    else
                        return;
                }
            }
            while(i < getOrdersList().size())
            {
                if(performOrder(getOrdersList().get(i), inventory))                    
                    i++;
                else
                    return; 
            }
            while(j < getRestocksList().size())
            {
                performRestock(getRestocksList().get(j), inventory);  
                j++;
            }            
            showInventoryStatus(inventory);            
        }
        catch(Exception ex)
        {
            throw new Exception("traverseOrderRestockList: " + ex);
        }        
    }
    
    /**
     * update inventory with items ordered (subtract from inventory)
     * @param order
     * @param inventory
     * @return 
     */
    private boolean performOrder(Order order, Inventory inventory)
    {
        switch (order.getItemOrdered()) {
            case "sled":
                if(inventory.getSled() >= order.getItemQuantity())
                    inventory.setSled(inventory.getSled()-order.getItemQuantity());
                else{
                    System.out.println("OUT OF STOCK! Item ordered: sleds, Item quantity:  " + order.getItemQuantity() + " , Order date: " + order.getOrderDate() + ", Quantity in inventory: " + inventory.getSled());
                    return false;
                }                                
                break;
            case "snowblower":
                if(inventory.getSnowblowers() >= order.getItemQuantity())
                    inventory.setSnowblowers(inventory.getSnowblowers()-order.getItemQuantity());
                else{
                    System.out.println("OUT OF STOCK! Item ordered: snowblowers, Item quantity:  " + order.getItemQuantity() + " , Order date: " + order.getOrderDate() + ", Quantity in inventory: " + inventory.getSnowblowers());
                    return false;
                }                                
                break;
            case "tires":
                if(inventory.getTires() >= order.getItemQuantity())
                    inventory.setTires(inventory.getTires()-order.getItemQuantity());
                else{
                    System.out.println("OUT OF STOCK! Item ordered: tires, Item quantity:  " + order.getItemQuantity() + " , Order date: " + order.getOrderDate() + ", Quantity in inventory: " + inventory.getTires());
                    return false;
                }                                
                break;
            case "shovel":
                if(inventory.getShovels() >= order.getItemQuantity())
                    inventory.setShovels(inventory.getShovels()-order.getItemQuantity());
                else{
                    System.out.println("OUT OF STOCK! Item ordered: shovels, Item quantity:  " + order.getItemQuantity() + " , Order date: " + order.getOrderDate() + ", Quantity in inventory: " + inventory.getShovels());
                    return false; 
                }                                
                break;
            case "skis":
                if(inventory.getSkis() >= order.getItemQuantity())
                    inventory.setSkis(inventory.getSkis()-order.getItemQuantity());
                else{
                    System.out.println("OUT OF STOCK! Item ordered: skis, Item quantity:  " + order.getItemQuantity() + " , Order date: " + order.getOrderDate() + ", Quantity in inventory: " + inventory.getSkis());
                    return false; 
                }                                
                break;
            default:
                break;
        }
        return true;
    }
    
    /**
     * update inventory with items restocked(add to inventory)
     * @param restock
     * @param inventory 
     */
    private void performRestock(Restock restock, Inventory inventory)
    {
        switch (restock.getItemStocked()) {
            case "sled":
                inventory.setSled(inventory.getSled()+restock.getItemQuantity());
                break;
            case "snowblower":
                inventory.setSnowblowers(inventory.getSnowblowers()+restock.getItemQuantity());
                break;
            case "tires":
                inventory.setTires(inventory.getTires()+restock.getItemQuantity());
                break;
            case "shovel":
                inventory.setShovels(inventory.getShovels()+restock.getItemQuantity());
                break;
            case "skis":
                inventory.setSkis(inventory.getShovels()+restock.getItemQuantity());
                break;
            default:
                break;
        }
    }
  
    /**
     * parses the json orders and adds it ordersList
     * @param order: a JSONObject in orders.json file
     */
    private void parseOrdersObject(JSONObject order) throws Exception
    { 
        try
        {
            Date d = null;
            Long itemQuantity = null;
            String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";            
            String custId = (String) order.get("customer_id");
            String orderDate = (String) order.get("order_date");
            if(orderDate != null && !orderDate.isEmpty())
            {
                try{
                    d = new SimpleDateFormat(dateFormat).parse(orderDate);
                }
                catch(Exception ex)
                {
                    return;
                }
            }
            String itemOrdered = (String) order.get("item_ordered");
            if(order.get("item_quantity") != null && !order.get("item_quantity").toString().isEmpty())
               itemQuantity = Long.valueOf(order.get("item_quantity").toString()) ;
            String itemPrice = (String) order.get("item_price"); 
            if(itemOrdered != null && !itemOrdered.isEmpty() && itemQuantity != null && d != null){
                Order curOrder;
                curOrder = new Order();
                curOrder.setCustomerId(custId);
                curOrder.setItemOrdered(itemOrdered);
                curOrder.setItemPrice(itemPrice);
                curOrder.setItemQuantity(itemQuantity);
                curOrder.setOrderDate(d);
                getOrdersList().add(curOrder);
            }            
        }        
        catch(Exception ex)
        {
            throw new Exception("parseOrdersObject: " + ex);
        }
    }
    
    /**
     * parses the restock json and add it to restockList
     * @param restock: a JSONObject in the restocks.json file
     */
    private void parseRestockObject(JSONObject restock) throws ParseException, Exception
    { 
        try
        {
            Date d = null;            
            String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
            String restockDate = (String) restock.get("restock_date");            
            if(restockDate != null && !restockDate.isEmpty()){
                try
                {
                    d = new SimpleDateFormat(dateFormat).parse(restockDate);
                }
                catch(Exception ex)
                {
                    return;
                }
            }
                
            String itemStocked = (String) restock.get("item_stocked");             
            Long itemQuantity = (Long) restock.get("item_quantity");                        
            String manufacturer = (String) restock.get("manufacturer"); 
            Double wholesalePrice = (Double) restock.get("wholesale_price");
            if(d != null && itemQuantity != null && itemStocked != null && !itemStocked.isEmpty())
            {
                Restock curRestock;
                curRestock = new Restock();
                curRestock.setItemQuantity(itemQuantity);
                curRestock.setItemStocked(itemStocked);
                curRestock.setManufacturer(manufacturer);
                curRestock.setRestockDate(d);
                curRestock.setWholesalePrice(wholesalePrice);
                getRestocksList().add(curRestock);
            }            
        }  
        
        catch(Exception ex)
        {
            throw new Exception("parseRestockObject: " + ex);
        }
    }
    
    private void showInventoryStatus(Inventory inventory)
    {
        System.out.println("SUCCESS! Remaining items in inventory: \n shovels: " + inventory.getShovels()+ ", \n" + "sleds: " + inventory.getSled()+ ", \n" + "snowblowers: " +  inventory.getSnowblowers() + ", \n" + "tires: " +inventory.getTires() + ", \n" + "skis: " + inventory.getSkis());
    }
    
}
