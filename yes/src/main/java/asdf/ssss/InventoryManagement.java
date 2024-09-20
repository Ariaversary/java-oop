package asdf.ssss;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManagement {

    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String PPE_FILE_NAME = "ppe.txt";
    private static final String SUPPLIERS_FILE = "suppliers.txt";

    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, String> itemDescriptionMap = new HashMap<>();
    private Map<String, String> itemSupplierMap;

    public InventoryManagement(Suppliers suppliers, Hospitals hospitals) {
        itemSupplierMap = new HashMap<>();
        loadSuppliers();
        Hospitals.initializeHospitalFile();
        loadInventory();
        initializeItemSupplierMap();
    }

    private void initializeItemSupplierMap() {
        itemSupplierMap.put("HC", "1"); // Head Cover
        itemSupplierMap.put("FS", "1"); // Face Shield
        itemSupplierMap.put("MS", "2"); // Mask
        itemSupplierMap.put("GL", "3"); // Gloves
        itemSupplierMap.put("GW", "3"); // Gown
        itemSupplierMap.put("SC", "4"); // Shoe Covers

        itemDescriptionMap.put("HC", "Head Cover");
        itemDescriptionMap.put("FS", "Face Shield");
        itemDescriptionMap.put("MS", "Mask");
        itemDescriptionMap.put("GL", "Gloves");
        itemDescriptionMap.put("GW", "Gown");
        itemDescriptionMap.put("SC", "Shoe Covers");
    }

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

    private void updatePPEFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PPE_FILE_NAME))) {
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                String code = entry.getKey();
                int quantity = entry.getValue();
                String description = itemDescriptionMap.getOrDefault(code, "Unknown Description");
                String supplier = itemSupplierMap.getOrDefault(code, "Unknown Supplier");
                writer.write(String.format("%s,%s,%s,%d%n", code, description, supplier, quantity));
            }
        } catch (IOException e) {
            System.err.println("Error updating PPE file: " + e.getMessage());
        }
    }

    private void recordTransaction(String itemCode, String supplierID, String entityCode, int quantity, boolean isReceived) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            String type = isReceived ? "Received" : "Distributed";
            String entry = String.format("%s|%s|%s|%d|%s|%s", itemCode, supplierID, entityCode, quantity, type, getCurrentDateTime());
            bw.write(entry);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public void addItemFromSupplier(String itemCode, String supplierID, int quantity) {
        if (!Suppliers.isValidSupplierID(supplierID)) {
            showErrorDialog("Invalid supplier ID.");
            return;
        }
    
        if (!inventory.containsKey(itemCode)) {
            showErrorDialog("Item does not exist in the PPE inventory.");
            return;
        }
    
        inventory.put(itemCode, inventory.getOrDefault(itemCode, 0) + quantity);
        recordTransaction(itemCode, supplierID, "N/A", quantity, true); // Record as received
        updatePPEFile();
        showConfirmationDialog("Items added successfully."); // Only show on success
    }
    

    public void distributeItemToHospital(String itemCode, String hospitalID, int quantity) {
        if (!Hospitals.isHospitalIDInUse(hospitalID)) {
            showErrorDialog("Invalid hospital ID.");
            return;
        }
    
        if (!inventory.containsKey(itemCode)) {
            showErrorDialog("Item does not exist in the PPE inventory.");
            return;
        }
    
        int currentQuantity = inventory.get(itemCode);
        if (currentQuantity < quantity) {
            showErrorDialog(String.format("Insufficient stock. Current stock: %d", currentQuantity));
            return;
        }
    
        inventory.put(itemCode, currentQuantity - quantity);
        String supplierID = getSupplierIDForItem(itemCode);
        recordTransaction(itemCode, supplierID, hospitalID, quantity, false); // Record as distributed
        updatePPEFile();
        showConfirmationDialog("Items distributed successfully."); // Only show on success
    }

    private void loadSuppliers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String supplierID = parts[0];
                    String itemCode = parts[1];
                    itemSupplierMap.put(itemCode, supplierID);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading suppliers: " + e.getMessage());
        }
    }

    private String getSupplierIDForItem(String itemCode) {
        return itemSupplierMap.getOrDefault(itemCode, "Unknown Supplier ID");
    }

    public List<Order> fetchOrders() {
        List<Order> orders = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|"); // Changed to use | as delimiter
                if (parts.length >= 6) {
                    String itemCode = parts[0];
                    String supplierID = parts[1];
                    String hospitalID = parts[2];
                    int quantity = Integer.parseInt(parts[3]);
                    String type = parts[4];
                    String dateString = parts[5];
                    LocalDateTime orderDate;
                    try {
                        orderDate = LocalDateTime.parse(dateString, formatter);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing date for itemCode " + itemCode + ": " + e.getMessage());
                        continue;
                    }

                    String orderId = String.valueOf(index);
                    orders.add(new Order(orderId, hospitalID, orderDate, itemCode, supplierID, quantity, type));
                }
                index++;
            }
        } catch (IOException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return orders;
    }

    public List<InventoryItem> fetchInventoryItems() {
        List<InventoryItem> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            String code = entry.getKey();
            int quantity = entry.getValue();
            String description = itemDescriptionMap.getOrDefault(code, "Unknown Description");
            String supplier = getSupplierIDForItem(code);
            items.add(new InventoryItem(code, description, supplier, quantity));
        }
        return items;
    }

    private void showConfirmationDialog(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "Confirmation", JOptionPane.INFORMATION_MESSAGE));
    }
    
    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    public static void main(String[] args) {
        Suppliers suppliers = new Suppliers();
        Hospitals hospitals = new Hospitals();
        InventoryManagement inventoryManagement = new InventoryManagement(suppliers, hospitals);
        
        // Create PPE file if it doesn't exist
        PPE.createPPEFile();
        
        JFrame frame = new JFrame();
        PrintOptions printOptions = new PrintOptions(inventoryManagement);
        frame.add(printOptions);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
