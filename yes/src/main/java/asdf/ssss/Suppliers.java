package asdf.ssss;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Suppliers {
    private static final String SUPPLIERS_FILE = "suppliers.txt";
    private static final String[] VALID_SUPPLIER_IDS = {"1", "2", "3", "4"};
    private static Set<String> usedSupplierIDs = new HashSet<>();

    static {
        loadUsedSupplierIDs();
    }

    private static void loadUsedSupplierIDs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split("\\|");
                if (supplierData.length > 0) {
                    usedSupplierIDs.add(supplierData[0]);
                }
            }
        } catch (IOException e) {
            // File may not exist, which is fine at initialization
        }
    }

    public static boolean isValidSupplierID(String supplierID) {
        return supplierID.length() == 1 && usedSupplierIDs.contains(supplierID) || isValidSupplierIDFormat(supplierID);
    }

    private static boolean isValidSupplierIDFormat(String supplierID) {
        for (String validID : VALID_SUPPLIER_IDS) {
            if (validID.equals(supplierID)) {
                return true;
            }
        }
        return false;
    }

    public static void addSupplier(String supplierID, String name, String contact, String address) throws IOException {
        if (!isValidSupplierIDFormat(supplierID)) {
            throw new IllegalArgumentException("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
        }

        if (isSupplierIDInUse(supplierID)) {
            throw new IllegalArgumentException("Supplier ID already in use.");
        }

        if (usedSupplierIDs.size() >= VALID_SUPPLIER_IDS.length) {
            throw new IllegalArgumentException("Cannot add more than 4 suppliers.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUPPLIERS_FILE, true))) {
            writer.write(String.join("|", supplierID, name, contact, address));
            writer.newLine();
        }

        usedSupplierIDs.add(supplierID);
    }

    public static void modifySupplier(String supplierIDToModify, String newName, String newContact, String newAddress) throws IOException {
        if (!isValidSupplierIDFormat(supplierIDToModify)) {
            throw new IllegalArgumentException("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
        }

        if (!isSupplierIDInUse(supplierIDToModify)) {
            throw new IllegalArgumentException("Supplier ID does not exist.");
        }

        File tempFile = new File("temp_suppliers.txt");
        boolean supplierFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split("\\|");
                if (supplierData[0].equals(supplierIDToModify)) {
                    supplierFound = true;
                    writer.write(String.join("|", supplierIDToModify, newName, newContact, newAddress));
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }

        if (supplierFound) {
            File originalFile = new File(SUPPLIERS_FILE);
            if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
                throw new IOException("Could not update supplier file.");
            }
        } else {
            throw new IOException("Supplier not found.");
        }
    }

    public static String searchSupplier(String supplierIDToSearch) throws IOException {
        if (!isValidSupplierIDFormat(supplierIDToSearch)) {
            throw new IllegalArgumentException("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
        }

        if (!isSupplierIDInUse(supplierIDToSearch)) {
            throw new IOException("Supplier not found.");
        }

        StringBuilder supplierDetails = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split("\\|");
                if (supplierData[0].equals(supplierIDToSearch)) {
                    supplierDetails.append("SupplierID: ").append(supplierData[0]).append("\n");
                    supplierDetails.append("Name: ").append(supplierData[1]).append("\n");
                    supplierDetails.append("Contact: ").append(supplierData[2]).append("\n");
                    supplierDetails.append("Address: ").append(supplierData[3]).append("\n");
                    break;
                }
            }
        }

        if (supplierDetails.length() == 0) {
            throw new IOException("Supplier not found.");
        }

        return supplierDetails.toString();
    }

    public static void deleteSupplier(String supplierID) throws IOException {
        if (supplierID == null || supplierID.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier ID cannot be null or empty.");
        }

        if (!isValidSupplierIDFormat(supplierID) || !isSupplierIDInUse(supplierID)) {
            throw new IllegalArgumentException("Invalid or non-existent Supplier ID.");
        }

        File file = new File(SUPPLIERS_FILE);
        File tempFile = new File("temp_suppliers.txt");

        boolean supplierFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split("\\|");
                if (supplierData[0].equals(supplierID)) {
                    supplierFound = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        if (supplierFound) {
            if (!file.delete() || !tempFile.renameTo(file)) {
                throw new IOException("Could not delete the supplier file.");
            }
            usedSupplierIDs.remove(supplierID);
        } else {
            throw new IllegalArgumentException("Supplier not found.");
        }
    }

    public static boolean isSupplierIDInUse(String supplierID) {
        return usedSupplierIDs.contains(supplierID);
    }
}
