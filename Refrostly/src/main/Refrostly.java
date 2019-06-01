/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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

    /**
     * @param args the command line arguments
     */
    static List<Order> ordersList = new ArrayList<>();
    static List<Restock> restocksList = new ArrayList<>();
    
    public static void main(String[] args) throws IOException, Exception
    {        
        // read orders.json
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader("resources/orders.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray orderList = (JSONArray) obj;
             
            for(int i = 0; i < orderList.size(); i++)
            {
                parseOrdersObject((JSONObject) orderList.get(i));
            }
            
            // sort these orders w.r.t orderdate
            Collections.sort(ordersList, new Comparator<Order>(){
                @Override
                public int compare(Order a, Order b) {                    
                    return a.getOrderDate().compareTo(b.getOrderDate());                    
                }
            });
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        try (FileReader reader = new FileReader("resources/restocks.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray restockList = (JSONArray) obj;          
            for(int j = 0; j < restockList.size(); j++)
            {
                parseRestockObject((JSONObject) restockList.get(j));
            }
            // sort these restocks w.r.t orderdate
            Collections.sort(restocksList, new Comparator<Restock>(){
                @Override
                public int compare(Restock a, Restock b) {
                    return a.getRestockDate().compareTo(b.getRestockDate());                    
                }
            });
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        traverseOrderRestockList();
    }
    
    /**
     * gets the status of inventory by traversing through ordersList and restocksList in chronological order
     */
    private static void traverseOrderRestockList(){
        Inventory inventory = new Inventory();
        int len = Math.min(ordersList.size(), restocksList.size());
        int i = 0, j = 0;
        while(i < ordersList.size() && j < restocksList.size())
        {
            int compare = ordersList.get(i).getOrderDate().compareTo(restocksList.get(j).getRestockDate());
            
            if( compare < 0)        // order date is before restock date
            {
                switch (ordersList.get(i).getItemOrdered()) {
                    case "sled":
                        inventory.setSled(inventory.getSled()-ordersList.get(i).getItemQuantity());
                        break;
                    case "snowblower":
                        inventory.setSnowblowers(inventory.getSnowblowers()-ordersList.get(i).getItemQuantity());
                        break;
                    case "tires":
                        inventory.setTires(inventory.getTires()-ordersList.get(i).getItemQuantity());
                        break;
                    case "shovel":
                        inventory.setShovels(inventory.getShovels()-ordersList.get(i).getItemQuantity());
                        break;
                    default:
                        break;
                }
                i++;
            }
            else if(compare > 0)
            {
                if(restocksList.get(j).getItemStocked().equals("sled"))
                {
                    inventory.setSled(inventory.getSled()+restocksList.get(j).getItemQuantity());
                }
                else if(restocksList.get(j).getItemStocked().equals("snowblower"))
                {
                    inventory.setSnowblowers(inventory.getSnowblowers()+restocksList.get(j).getItemQuantity());
                }
                else if(restocksList.get(j).getItemStocked().equals("tires"))
                {
                    inventory.setTires(inventory.getTires()+restocksList.get(j).getItemQuantity());
                }
                else if(restocksList.get(j).getItemStocked().equals("shovel"))
                {
                    inventory.setShovels(inventory.getShovels()+restocksList.get(j).getItemQuantity());
                }
                j++;
            }
            
        }
        while(i < ordersList.size())
        {
            switch (ordersList.get(i).getItemOrdered()) {
                case "sled":
                    inventory.setSled(inventory.getSled()-ordersList.get(i).getItemQuantity());
                    break;
                case "snowblower":
                    inventory.setSnowblowers(inventory.getSnowblowers()-ordersList.get(i).getItemQuantity());
                    break;
                case "tires":
                    inventory.setTires(inventory.getTires()-ordersList.get(i).getItemQuantity());
                    break;
                case "shovel":
                    inventory.setShovels(inventory.getShovels()-ordersList.get(i).getItemQuantity());
                    break;
                default:
                    break;
            }
            i++; 
        }
        while(j < restocksList.size())
        {
            if(restocksList.get(j).getItemStocked().equals("sled"))
            {
                inventory.setSled(inventory.getSled()+restocksList.get(j).getItemQuantity());
            }
            else if(restocksList.get(j).getItemStocked().equals("snowblower"))
            {
                inventory.setSnowblowers(inventory.getSnowblowers()+restocksList.get(j).getItemQuantity());
            }
            else if(restocksList.get(j).getItemStocked().equals("tires"))
            {
                inventory.setTires(inventory.getTires()+restocksList.get(j).getItemQuantity());
            }
            else if(restocksList.get(j).getItemStocked().equals("shovel"))
            {
                inventory.setTires(inventory.getShovels()+restocksList.get(j).getItemQuantity());
            }
            j++;
        }
        
        System.out.println("Remaining shovels in inventory: " + inventory.getShovels()+ ", \n" + "sleds: " + inventory.getSled()+ ", \n" + "snowblowers: " +  inventory.getSnowblowers() + ", \n" + "tires: " +inventory.getTires());
    }
  
    /**
     * parses the json orders and adds it ordersList
     * @param order: a JSONObject in orders.json file
     */
    private static void parseOrdersObject(JSONObject order) throws Exception
    { 
        try
        {
            String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
            
            String custId = (String) order.get("customer_id");
            String orderDate = (String) order.get("order_date");
            Date d = new SimpleDateFormat(dateFormat).parse(orderDate);
            String itemOrdered = (String) order.get("item_ordered"); 
            Long itemQuantity = Long.valueOf(order.get("item_quantity").toString()) ;
            String itemPrice = (String) order.get("item_price"); 
            Order curOrder;
            curOrder = new Order();
            curOrder.setCustomerId(custId);
            curOrder.setItemOrdered(itemOrdered);
            curOrder.setItemPrice(itemPrice);
            curOrder.setItemQuantity(itemQuantity);
            curOrder.setOrderDate(d);

            ordersList.add(curOrder);
        }
        
        catch(Exception ex)
        {
            throw new Exception(ex);
        }
    }
    
    /**
     * parses the restock json and add it to restockList
     * @param restock: a JSONObject in the restocks.json file
     */
    private static void parseRestockObject(JSONObject restock) throws Exception
    { 
        try
        {
            String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
            String restockDate = (String) restock.get("restock_date");
            Date d = new SimpleDateFormat(dateFormat).parse(restockDate);
            String itemStocked = (String) restock.get("item_stocked"); 
            Long itemQuantity = (Long) restock.get("item_quantity");
                        
            String manufacturer = (String) restock.get("manufacturer"); 
            Double wholesalePrice = (Double) restock.get("wholesale_price");
            Restock curRestock;
            curRestock = new Restock();

            curRestock.setItemQuantity(itemQuantity);
            curRestock.setItemStocked(itemStocked);
            curRestock.setManufacturer(manufacturer);
            curRestock.setRestockDate(d);
            curRestock.setWholesalePrice(wholesalePrice);

            restocksList.add(curRestock);
        }  
        catch(Exception ex)
        {
            throw new Exception(ex);
        }
    }
    
}
