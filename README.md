# java-oop
oops
ahahahaHAHAHAHAH


things to do:

1. ~~create the initial program that creates the users.txt file: it should include userID, name, password and userType(admin/ staff). (should probably add date of registration).~~
       - ~~Admin should be able to add new users, modify them, search and delete them.~~
       - ~~creation should only be done once and only once during intial launch.~~
   
2. ~~create the initial program that creates the ppe.txt file that stores items in the form of item code, supplier code and quantity in stock (measured in boxes).~~
        ~~- All ppe items in boxes, received, recorded and distributed. initial creation must be at 100 units.~~
        ~~- each items is supplied by one supplier, one supplier can supply more than 1 type.~~
        ~~- minimum of 3 hospitals. with only 3 to 4 suppliers.~~

3.~~create a program that creates the suppliers.txt file to store and update supplier details.~~

4. ~~create the inventory update program.~~
        ~~ -update item quantities every time after receiving/distributing (increase/decrease).~~
       ~~  - before updating the items, program should check for availabble quantity, user should also be notified.~~
         ~~        -program should also state the quantity of items.~~
        ~~ -record the details of all the updates in a text file (transactions.txt):~~
          ~~       -this should include items received and distributed, items code, supplier code and hospital code, quantity received/distributed, and date/time.~~



2nd part:

1. create the inventory tracking program.
         -program should be able the track the amount of items and be able to print it to a .pdf file.
         -total availabbe items sorted in ascending order of item code
         -records of items <25 quantities
         -track available quatity for an item
         -track items received during specific time (start to end date)

2. create the search functionality.
         -be able to search through a filtered list (ie: before a specific date etc.)
         -search for details of distribution or received for an item.
         -search should be done using item code
         -the search should include supplier/hospital code with received/distributed date with quantity.
         -if items has been received and distributed bby te same supplier/hospital more than once, the quantities should be summed (?? idk either)

3. create the menu where all process should be accessed from.

SIMPLIFIED LIST:
1. ~~Program that creates users.txt and ppe.txt during start up.~~
2. ~~Program that creates the admin account on start up and have that admin be able to create more users with data.~~
3. ~~Program that creates the suppliers.txt file~~ and store details/update.
4. ~~Program that updates the inventory of items.~~
5. ~~Program that records the distribution/receiving of items in transactions.txt~~

6. ~~Program that tracks the items and prints it into a .pdf file~~
7. ~~Program that can search with a filter~~







IDEAS (By Oguess):

When Adding/Modifying Supplier IDs, if it is limited to 4 ids (1,2,3 or 4), have the program display available/taken IDs and have the error message correnspond to this as well
