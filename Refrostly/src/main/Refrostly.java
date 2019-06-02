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
        Refrostly refrostly = new Refrostly();
        JSONParser jsonParser = new JSONParser(); 
        refrostly.parseOrdersJson(jsonParser);
        refrostly.parseRestocksJson(jsonParser);
        Inventory inventory = refrostly.traverseOrderRestockList(); 
        if(inventory.getStatus()[0].equals("SUCCESS"))
            refrostly.showInventoryStatus(inventory);   // success and prints out the remaining inventories
        else
        {
            System.out.println(inventory.getStatus()[0] + " ran out of " + inventory.getStatus()[1] + " on " + inventory.getStatus()[2]);  // out of stock, which item and when?
        }
    }
    
    /**
     * parses the given orders.json file 
     * @param jsonParser
     * @throws IOException
     * @throws ParseException
     * @throws Exception 
     */
    private void parseOrdersJson(JSONParser jsonParser) throws IOException, ParseException, Exception
    {
        File ordersFile = new File("orders.json");
        try (FileReader reader = new FileReader(ordersFile.getCanonicalPath()))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray orderList = (JSONArray) obj;
             
            for(int i = 0; i < orderList.size(); i++)
            {
                parseOrdersObject((JSONObject) orderList.get(i));
            }
           
            // sort these orders w.r.t orderdate
            sortOrdersList();            
            
        } 
        catch (FileNotFoundException e) 
        {
            throw new Exception(e);
        } 
        catch (IOException e) 
        {
            throw new Exception(e);
        } 
        catch (ParseException e) 
        {
            throw new Exception(e);
        }
    }
    
    /**
     * parses the given restocks.json file
     * @param jsonParser
     * @throws IOException
     * @throws ParseException
     * @throws Exception 
     */
    private void parseRestocksJson(JSONParser jsonParser) throws IOException, ParseException, Exception
    {
        File restocksFile = new File("restocks.json");
        try (FileReader reader = new FileReader(restocksFile.getCanonicalPath()))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray restockList = (JSONArray) obj;          
            for(int j = 0; j < restockList.size(); j++)
            {
                parseRestockObject((JSONObject) restockList.get(j));
            }
            // sort these restocks w.r.t restockdate
            sortRestocksList();
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
    private Inventory traverseOrderRestockList() throws Exception
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
                        return inventory;
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
                        return inventory;
                }
            }
            while(i < getOrdersList().size())
            {
                if(performOrder(getOrdersList().get(i), inventory))                    
                    i++;
                else
                    return inventory;
            }
            while(j < getRestocksList().size())
            {
                performRestock(getRestocksList().get(j), inventory);  
                j++;
            } 
            inventory.setStatus(new String[]{"SUCCESS","",""});
            return inventory;
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
                    inventory.setStatus(new String[]{"OUT OF STOCK!", "sleds", order.getOrderDate().toString()});
                    return false;
                }                                
                break;
            case "snowblower":
                if(inventory.getSnowblowers() >= order.getItemQuantity())
                    inventory.setSnowblowers(inventory.getSnowblowers()-order.getItemQuantity());
                else{
                    inventory.setStatus(new String[]{"OUT OF STOCK!", "snowblowers", order.getOrderDate().toString()});
                    return false;
                }                                
                break;
            case "tires":
                if(inventory.getTires() >= order.getItemQuantity())
                    inventory.setTires(inventory.getTires()-order.getItemQuantity());
                else{
                    inventory.setStatus(new String[]{"OUT OF STOCK!", "tires", order.getOrderDate().toString()});
                    return false;
                }                                
                break;
            case "shovel":
                if(inventory.getShovels() >= order.getItemQuantity())
                    inventory.setShovels(inventory.getShovels()-order.getItemQuantity());
                else{
                    inventory.setStatus(new String[]{"OUT OF STOCK!", "shovels", order.getOrderDate().toString()});
                    return false; 
                }                                
                break;
            case "skis":
                if(inventory.getSkis() >= order.getItemQuantity())
                    inventory.setSkis(inventory.getSkis()-order.getItemQuantity());
                else{
                    inventory.setStatus(new String[]{"OUT OF STOCK!", "skis", order.getOrderDate().toString()});
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
                    return; // does not proceed with this order as this application cannot evaluate without the orderdate
                }
            }
            String itemOrdered = (String) order.get("item_ordered");
            if(order.get("item_quantity") != null && !order.get("item_quantity").toString().isEmpty())
               itemQuantity = Long.valueOf(order.get("item_quantity").toString()) ;
            if(itemOrdered != null && !itemOrdered.isEmpty() && itemQuantity != null && d != null){
                Order curOrder;
                curOrder = new Order();
                curOrder.setCustomerId(custId);
                curOrder.setItemOrdered(itemOrdered);
                curOrder.setItemPrice((String) order.get("item_price"));
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
                    return;// does not proceed with this restock as this application cannot evaluate without the restockdate
                }
            }
                
            String itemStocked = (String) restock.get("item_stocked");             
            Long itemQuantity = (Long) restock.get("item_quantity");                        
            String manufacturer = (String) restock.get("manufacturer"); 
            if(d != null && itemQuantity != null && itemStocked != null && !itemStocked.isEmpty())
            {
                Restock curRestock;
                curRestock = new Restock();
                curRestock.setItemQuantity(itemQuantity);
                curRestock.setItemStocked(itemStocked);
                curRestock.setManufacturer(manufacturer);
                curRestock.setRestockDate(d);
                curRestock.setWholesalePrice((Double) restock.get("wholesale_price"));
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
