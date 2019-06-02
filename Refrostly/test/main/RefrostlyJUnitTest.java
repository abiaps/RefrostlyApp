/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import main.Refrostly;
import model.Inventory;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author abiaps
 */
public class RefrostlyJUnitTest {
    
    public RefrostlyJUnitTest() {
    }
        
    @Test
     public void refrostlytest() throws Exception
     {
         try
         {
            Refrostly refrostly = new Refrostly();
            JSONParser jsonParser = new JSONParser(); 
            refrostly.parseOrdersJson(jsonParser);
            refrostly.parseRestocksJson(jsonParser);
            Inventory expectedInventory = new Inventory();
            expectedInventory.setShovels(4l);
            expectedInventory.setSkis(2l);
            expectedInventory.setSled(1l);
            expectedInventory.setSnowblowers(4l);
            expectedInventory.setTires(2l);
            Inventory output = refrostly.evaluateRestockingAlgorithm();
            assertEquals(expectedInventory, output); 
         }
         catch(Exception e)
         {
             throw new Exception(e);
         }
     }
}
