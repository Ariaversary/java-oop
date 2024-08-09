import java.io.*;

public class Suppliers {
    private static final String SUPPLIERS_FILE = "suppliers.txt";
    private static final String[] VALID_SUPPLIER_IDS = {"1", "2", "3", "4"};

    private static boolean isValidSupplierID(String supplierID) {
        for (String validID : VALID_SUPPLIER_IDS) {
            if (validID.equals(supplierID)) {
                return true;
            }
        }
        return false;
    }

    public static void addSupplier(String supplierID, String name, String contact, String address) throws IOException {
        if (!isValidSupplierID(supplierID)) {
            System.out.println("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUPPLIERS_FILE, true))) {
            writer.write(supplierID + "," + name + "," + contact + "," + address);
            writer.newLine();
            System.out.println("Supplier added successfully.");
        }
    }

    public static void modifySupplier(String supplierIDToModify, String newName, String newContact, String newAddress) throws IOException {
        if (!isValidSupplierID(supplierIDToModify)) {
            System.out.println("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
            return;
        }

        File tempFile = new File("temp_suppliers.txt");
        boolean supplierFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split(",");
                if (supplierData[0].equals(supplierIDToModify)) {
                    supplierFound = true;
                    writer.write(String.join(",", supplierIDToModify, newName, newContact, newAddress));
                    writer.newLine();
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        if (supplierFound) {
            File originalFile = new File(SUPPLIERS_FILE);
            if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
                System.err.println("Could not update supplier file.");
            }
        } else {
            System.out.println("Supplier not found.");
        }
    }

    public static void searchSupplier(String supplierIDToSearch) throws IOException {
        if (!isValidSupplierID(supplierIDToSearch)) {
            System.out.println("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
            return;
        }
        boolean supplierFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split(",");
                if (supplierData[0].equals(supplierIDToSearch)) {
                    System.out.println("SupplierID: " + supplierData[0]);
                    System.out.println("Name: " + supplierData[1]);
                    System.out.println("Contact: " + supplierData[2]);
                    System.out.println("Address: " + supplierData[3]);
                    supplierFound = true;
                    break;
                }
            }
        }

        if (!supplierFound) {
            System.out.println("Supplier not found.");
        }
    }

    public static void deleteSupplier(String supplierIDToDelete) throws IOException {
        if (!isValidSupplierID(supplierIDToDelete)) {
            System.out.println("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
            return;
        }
        File tempFile = new File("temp_suppliers.txt");
        boolean supplierFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split(",");
                if (supplierData[0].equals(supplierIDToDelete)) {
                    supplierFound = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        if (supplierFound) {
            File originalFile = new File(SUPPLIERS_FILE);
            if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
                System.err.println("Could not update supplier file.");
            }
        } else {
            System.out.println("Supplier not found.");
        }
    }
}
