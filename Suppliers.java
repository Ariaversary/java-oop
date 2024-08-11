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
            throw new IllegalArgumentException("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUPPLIERS_FILE, true))) {
            writer.write(supplierID + "," + name + "," + contact + "," + address);
            writer.newLine();
        }
    }

    public static void modifySupplier(String supplierIDToModify, String newName, String newContact, String newAddress) throws IOException {
        if (!isValidSupplierID(supplierIDToModify)) {
            throw new IllegalArgumentException("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
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
                throw new IOException("Could not update supplier file.");
            }
        } else {
            throw new IOException("Supplier not found.");
        }
    }

    public static String searchSupplier(String supplierIDToSearch) throws IOException {
        if (!isValidSupplierID(supplierIDToSearch)) {
            throw new IllegalArgumentException("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
        }
        boolean supplierFound = false;
        StringBuilder supplierDetails = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(SUPPLIERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split(",");
                if (supplierData[0].equals(supplierIDToSearch)) {
                    supplierDetails.append("SupplierID: ").append(supplierData[0]).append("\n");
                    supplierDetails.append("Name: ").append(supplierData[1]).append("\n");
                    supplierDetails.append("Contact: ").append(supplierData[2]).append("\n");
                    supplierDetails.append("Address: ").append(supplierData[3]).append("\n");
                    supplierFound = true;
                    break;
                }
            }
        }

        if (!supplierFound) {
            throw new IOException("Supplier not found.");
        }

        return supplierDetails.toString();
    }

    public static void deleteSupplier(String supplierIDToDelete) throws IOException {
        if (!isValidSupplierID(supplierIDToDelete)) {
            throw new IllegalArgumentException("Invalid Supplier ID. Valid IDs are: 1, 2, 3, 4.");
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
                throw new IOException("Could not update supplier file.");
            }
        } else {
            throw new IOException("Supplier not found.");
        }
    }
}
