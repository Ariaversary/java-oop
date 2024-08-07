import java.io.*;
import java.util.*;

public class UserManagementSystem {

    private static final String FILE_NAME = "users.txt";
    private static final String ADMIN_PASSWORD = "asd"; // Predefined admin password.
    private static final int MAX_SUPPLIERS = 4; // Max amount of suppliers that can be added into suppliers.txt

                // Initialize files if they don't exist.
                private static void initializeFiles() throws IOException {
                    File userFile = new File(FILE_NAME);
                    if (!userFile.exists()) {
                        userFile.createNewFile();
                    }
                    File supplierFile = new File("suppliers.txt");
                    if (!supplierFile.exists()) {
                        supplierFile.createNewFile();
                    }
                    File ppeFile = new File("ppe.txt");
                    if (!ppeFile.exists()) {
                        ppeFile.createNewFile();
                    }
                }

    public static void main(String[] args) {
        Scanner scanner = null;
        try {
            // Initialize files if they don't exist.
            initializeFiles();

            // Use try-with-resources to ensure the Scanner is closed properly.
            scanner = new Scanner(System.in);
            System.out.println("Welcome to the User Management System");
            System.out.print("Enter admin password: ");
            String password = scanner.nextLine();
            
            // Literally just checks whether the initial admin password inputted is correct or not, if yes it will continue, otherwise it will stop runnning.
            if (!ADMIN_PASSWORD.equals(password)) {
                System.out.println("Invalid password.");
                return;
            }

            while (true) {
                System.out.println("1. Add User");
                System.out.println("2. Modify User");
                System.out.println("3. Search User");
                System.out.println("4. Delete User");
                System.out.println("5. Add Supplier");
                System.out.println("6. Modify Supplier");
                System.out.println("7. Search Supplier");
                System.out.println("8. Delete Supplier");
                System.out.println("9. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addUser(scanner);
                        break;
                    case 2:
                        modifyUser(scanner);
                        break;
                    case 3:
                        searchUser(scanner);
                        break;
                    case 4:
                        deleteUser(scanner);
                        break;
                    case 5:
                        addSupplier(scanner);
                        break;
                    case 6:
                        modifySupplier(scanner);
                        break;
                    case 7:
                        searchSupplier(scanner);
                        break;
                    case 8:
                        deleteSupplier(scanner);
                        break;
                    case 9:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            // Ensure Scanner is closed properly
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    
    private static boolean isUserIDUnique(String userID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData[0].equals(userID)) {
                    return false; // UserID already exists
                }
            }
        }
        return true; // UserID is unique
    }

    private static void addUser(Scanner scanner) throws IOException {
        System.out.print("Enter UserID: ");
        String userID = scanner.nextLine();
        
        // Check if UserID is unique
        if (!isUserIDUnique(userID)) {
            System.out.println("UserID already exists. Please enter a unique UserID.");
            return; // Exits this function if it already exists.
        }
    
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter UserType (admin/staff): ");
        
        String userType;
        while (true) {
            System.out.print("Enter UserType (admin/staff): ");
            userType = scanner.nextLine().trim().toLowerCase(); // Convert to lowercase for consistency
    
            if (userType.equals("admin") || userType.equals("staff")) {
                break; // Exit loop if valid
            } else {
                System.out.println("Invalid UserType. Please enter 'admin' or 'staff'.");
            }
        }
        
        String dateOfRegistration = new Date().toString();
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(String.join(",", userID, name, password, userType, dateOfRegistration));
            writer.newLine();
            System.out.println("User added successfully.");
        }
    }

    private static boolean isUserIDExists(String userID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData[0].equals(userID)) {
                    return true; // UserID exists
                }
            }
        }
        return false; // UserID does not exist
    }
    
    private static void modifyUser(Scanner scanner) throws IOException {
        System.out.print("Enter UserID of the user to modify: ");
        String userIDToModify = scanner.nextLine();
    
        if (!isUserIDExists(userIDToModify)) {
            System.out.println("UserID does not exist.");
            return;
        }
    
        List<String> lines = new ArrayList<>();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData[0].equals(userIDToModify)) {
                    userFound = true;
                    System.out.print("Enter new Name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new Password: ");
                    String newPassword = scanner.nextLine();
                    System.out.print("Enter new UserType (admin/staff): ");
                    String newUserType;
                    while (true) {
                        System.out.print("Enter new UserType (admin/staff): ");
                        newUserType = scanner.nextLine().trim().toLowerCase(); // Convert to lowercase for consistency

                        if (newUserType.equals("admin") || newUserType.equals("staff")) {
                            break; // Exit loop if valid
                        } else {
                            System.out.println("Invalid UserType. Please enter 'admin' or 'staff'.");
                        }
                    }
                    String dateOfRegistration = userData[4]; // Preserve the original registration date
                    lines.add(String.join(",", userIDToModify, newName, newPassword, newUserType, dateOfRegistration));
                } else {
                    lines.add(line);
                }
            }
        }

        if (userFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("User modified successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    private static void searchUser(Scanner scanner) throws IOException {
        System.out.print("Enter UserID of the user to search: ");
        String userIDToSearch = scanner.nextLine();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData[0].equals(userIDToSearch)) {
                    System.out.println("UserID: " + userData[0]);
                    System.out.println("Name: " + userData[1]);
                    System.out.println("Password: " + userData[2]);
                    System.out.println("UserType: " + userData[3]);
                    System.out.println("Date of Registration: " + userData[4]);
                    userFound = true;
                    break;
                }
            }
        }

        if (!userFound) {
            System.out.println("User not found.");
        }
    }

    private static void deleteUser(Scanner scanner) throws IOException {
        System.out.print("Enter UserID of the user to delete: ");
        String userIDToDelete = scanner.nextLine();
        List<String> lines = new ArrayList<>();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData[0].equals(userIDToDelete)) {
                    userFound = true;
                    System.out.println("User deleted successfully.");
                } else {
                    lines.add(line);
                }
            }
        }

        if (userFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private static boolean isSupplierIDUnique(String supplierID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("suppliers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split(",");
                if (supplierData[0].equals(supplierID)) {
                    return false; // SupplierID already exists
                }
            }
        }
        return true; // SupplierID is unique
    }

    private static void addSupplier(Scanner scanner) throws IOException {
        // Check the current number of suppliers
        if (getSupplierCount() >= MAX_SUPPLIERS) {
            System.out.println("Cannot add more suppliers. The maximum limit of " + MAX_SUPPLIERS + " suppliers has been reached.");
            return;
        }

        // Checks if the SupplierID is unique
        String supplierID;
        while (true) {
            System.out.print("Enter SupplierID: ");
            supplierID = scanner.nextLine();
            if (!isSupplierIDUnique(supplierID)) {
                System.out.println("SupplierID already exists. Please enter a unique SupplierID.");
            } else {
                break; // Exit loop if SupplierID is unique
            }
        }
    
        // Collect supplier information from the user
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Contact: ");
        String contact = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
    
        // Add the supplier to the file
        Suppliers.addSupplier(supplierID, name, contact, address);
    }
    
    private static int getSupplierCount() throws IOException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("suppliers.txt"))) {
            while (reader.readLine() != null) {
                count++;
            }
        }
        return count;
    }

    private static void modifySupplier(Scanner scanner) throws IOException {
        System.out.print("Enter SupplierID of the supplier to modify: ");
        String supplierIDToModify = scanner.nextLine();
        List<String> lines = new ArrayList<>();
        boolean supplierFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("suppliers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split(",");
                if (supplierData[0].equals(supplierIDToModify)) {
                    supplierFound = true;
                    System.out.print("Enter new SupplierID (or press Enter to keep the same): ");
                    String newSupplierID = scanner.nextLine();
                    if (newSupplierID.isEmpty()) {
                        newSupplierID = supplierIDToModify; // Keep the original ID if no new ID is provided
                    } else if (!newSupplierID.equals(supplierIDToModify) && !isSupplierIDUnique(newSupplierID)) {
                        System.out.println("New SupplierID already exists. Modification aborted.");
                        return;
                    }
                    System.out.print("Enter new Name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new Contact: ");
                    String newContact = scanner.nextLine();
                    System.out.print("Enter new Address: ");
                    String newAddress = scanner.nextLine();

                    lines.add(String.join(",", newSupplierID, newName, newContact, newAddress));
                } else {
                    lines.add(line);
                }
            }
        }
        if (supplierFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("suppliers.txt"))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("Supplier modified successfully.");
        } else {
            System.out.println("Supplier not found.");
        }
    }
    private static void searchSupplier(Scanner scanner) throws IOException {
        System.out.print("Enter SupplierID of the supplier to search: ");
        String supplierIDToSearch = scanner.nextLine();
        Suppliers.searchSupplier(supplierIDToSearch);
    }

    private static void deleteSupplier(Scanner scanner) throws IOException {
        System.out.print("Enter SupplierID of the supplier to delete: ");
        String supplierIDToDelete = scanner.nextLine();
        Suppliers.deleteSupplier(supplierIDToDelete);
    }
}
