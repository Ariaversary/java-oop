import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InventoryManagement {

    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String PPE_FILE_NAME = "ppe.txt";
    
    private Map<String, Integer> inventory = new HashMap<>();
    private Suppliers suppliers;
    private Hospitals hospitals;

    public InventoryManagement(Suppliers suppliers, Hospitals hospitals) {
        this.suppliers = suppliers;
        this.hospitals = hospitals;
        Hospitals.initializeHospitalFile(); // Ensure the hospital file is initialized
        loadInventory();
    }

    // Load inventory from the PPE file
    private void loadInventory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PPE_FILE_NAME))) {
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

    // Update the PPE file with the current inventory
    private void updatePPEFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PPE_FILE_NAME))) {
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                String code = entry.getKey();
                int quantity = entry.getValue();
                // Assuming item description and supplier are fixed or retrieved from somewhere
                // This part should be adjusted if item description is required
                String description = "Description"; // Placeholder
                String supplier = "Supplier"; // Placeholder
                writer.write(String.format("%s,%s,%s,%d%n", code, description, supplier, quantity));
            }
        } catch (IOException e) {
            System.err.println("Error updating PPE file: " + e.getMessage());
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
            showErrorDialog("Invalid supplier ID.");
            return;
        }

        if (!inventory.containsKey(itemCode)) {
            showErrorDialog("Item does not exist in the PPE inventory.");
            return;
        }

        inventory.put(itemCode, inventory.getOrDefault(itemCode, 0) + quantity);
        recordTransaction(itemCode, supplierID, quantity, true);
        updatePPEFile(); // Update PPE file after adding items
        showConfirmationDialog("Items added successfully.");
    }

    // Distribute items to hospitals
    public void distributeItemToHospital(String itemCode, String hospitalID, int quantity) {
        // Check if the hospital ID is valid
        if (!hospitals.isHospitalIDInUse(hospitalID)) {
            showErrorDialog("Invalid hospital ID.");
            return;
        }
    
        // Check if the item exists in the inventory
        if (!inventory.containsKey(itemCode)) {
            showErrorDialog("Item does not exist in the PPE inventory.");
            return;
        }
    
        // Check if there is sufficient stock
        int currentQuantity = inventory.get(itemCode);
        if (currentQuantity < quantity) {
            showErrorDialog(String.format("Insufficient stock. Current stock: %d", currentQuantity));
            return;
        }
    
        // Update inventory and record the transaction
        inventory.put(itemCode, currentQuantity - quantity);
        recordTransaction(itemCode, hospitalID, quantity, false);
        updatePPEFile(); // Update PPE file after distributing items
        showConfirmationDialog("Items distributed successfully.");
    }
    

    private void showConfirmationDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
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
