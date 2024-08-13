import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InventoryManagement {

    private static final String TRANSACTIONS_FILE = "transactions.txt";
    
    private Map<String, Integer> inventory = new HashMap<>();
    private Suppliers suppliers;
    private Hospitals hospitals;

    public InventoryManagement(Suppliers suppliers, Hospitals hospitals) {
        this.suppliers = suppliers;
        this.hospitals = hospitals;
        loadInventory();
    }

    // Load inventory from the PPE file
    private void loadInventory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PPE.PPE_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] itemData = line.split(",");
                if (itemData.length == 4) {
                    String code = itemData[0];
                    int quantity = Integer.parseInt(itemData[3]);
                    inventory.put(code, quantity);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }

    // Record a transaction
    private void recordTransaction(String itemCode, String entityCode, int quantity, boolean isReceived) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            String type = isReceived ? "Received" : "Distributed";
            String entry = String.format("%s,%s,%d,%s,%s", itemCode, entityCode, quantity, type, getCurrentDateTime());
            bw.write(entry);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
        }
    }

    // Get current date and time as a formatted string
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    // Add items received from suppliers
    public void addItemFromSupplier(String itemCode, String supplierID, int quantity) {
        if (!suppliers.isValidSupplierID(supplierID)) {
            System.out.println("Invalid supplier ID.");
            return;
        }

        inventory.put(itemCode, inventory.getOrDefault(itemCode, 0) + quantity);
        recordTransaction(itemCode, supplierID, quantity, true);
        System.out.println("Items added successfully.");
    }

    // Distribute items to hospitals
    public void distributeItemToHospital(String itemCode, String hospitalID, int quantity) {
        if (!hospitals.isHospitalIDInUse(hospitalID)) {
            System.out.println("Invalid hospital ID.");
            return;
        }

        int currentQuantity = inventory.getOrDefault(itemCode, 0);
        if (currentQuantity < quantity) {
            System.out.printf("Insufficient stock. Current stock: %d\n", currentQuantity);
            return;
        }

        inventory.put(itemCode, currentQuantity - quantity);
        recordTransaction(itemCode, hospitalID, quantity, false);
        System.out.println("Items distributed successfully.");
    }

    public static void main(String[] args) {
        Suppliers suppliers = new Suppliers();
        Hospitals hospitals = new Hospitals();
        InventoryManagement im = new InventoryManagement(suppliers, hospitals);
        
        // Create PPE file if it doesn't exist
        PPE.createPPEFile();
        
        // Testing updates
        im.addItemFromSupplier("HC", "1", 50); // Add Head Covers from supplier with ID 1
        im.distributeItemToHospital("HC", "1", 20); // Distribute Head Covers to hospital with ID 1
        
    }
}
