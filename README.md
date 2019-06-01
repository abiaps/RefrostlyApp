# RefrostlyApp
RefrostlyApp tracks the inventory of Refrostly store with order management status as either Success or Out of stock.

Refrostly store sells the following products online:

- Skis
- Shovels
- Sleds
- Snowblowers
- Winter tires

This application evaluates the restocking algorithm against Refrostly's actual order history from the past year. 

Input:

There are two json files orders.json and restocks.json to test with,

1. Refrostly’s 2018 customer order events (orders.json), with the following data:
    ○ Customer ID
    ○ Order ID
    ○ Date and time of the order
    ○ Item ordered
    ○ Quantity of item
    ○ Price that Refrostly charged per item
    
2. A list of inventory restocking events generated by the Restocking Algorithm
(restocks.json)
    ○ Date and time of the restock
    ○ Item stocked
    ○ Quantity of item
    ○ Manufacturer name
    ○ Price that Refrostly paid per item
    
 Output:
 
 SUCCESS! with remaining items and its quantites in the inventory (or)
 
 OUT OF STOCK! which item ran out of and when?
 
 For example, with the two sample files provided, the application should explain that the restocking algorithm was a SUCCESS , with the following extra inventory in the warehouse:
  ● 4 shovels
  ● 4 snowblowers
  ● 2 tires
  ● 1 sled
  
  Prerequisites:
  
java installed on your machine

Inorder to run this application locally,

clone or download this project

Go the project directory in command line

Go to dist

run the following command

java -jar Refrostly.jar

This application is written with

-Netbeans IDE 8.2
-JDK 1.8




