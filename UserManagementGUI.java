import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagementGUI extends JFrame {
    private static final String USER_FILE_NAME = "users.txt";
    private static final String SUPPLIER_FILE_NAME = "suppliers.txt";
    private static final String PPE_FILE_NAME = "ppe.txt";

    private JPanel mainPanel;
    private JTextArea outputArea;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public UserManagementGUI() {
        // Initialize the frame and panels
        setTitle("User Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cardPanel.getLayout();
        getContentPane().add(cardPanel);

        try {
            initializeFiles();
            if (!isPPEFileExists()) {
                initializePPEFile();
            }
        } catch (IOException e) {
            showErrorDialog("Error initializing files: " + e.getMessage());
        }

        showLoginScreen();
    }

    private void initializeFiles() throws IOException {
        File userFile = new File(USER_FILE_NAME);
        if (!userFile.exists()) {
            userFile.createNewFile();
        }
        File supplierFile = new File(SUPPLIER_FILE_NAME);
        if (!supplierFile.exists()) {
            supplierFile.createNewFile();
        }
    }
    
    private boolean isPPEFileExists() {
        File ppeFile = new File(PPE_FILE_NAME);
        return ppeFile.exists();
    }

    private void initializePPEFile() {
        // Call the static method from the PPE class to create the PPE file
        PPE.createPPEFile();
    }

    // Inner PPE class remains the same
    public static class PPE {
        public static class PPEItem {
            private String code;
            private String description;
            private String type;
            private int quantity;

            public PPEItem(String code, String description, String type, int quantity) {
                this.code = code;
                this.description = description;
                this.type = type;
                this.quantity = quantity;
            }

            // Method to return a string suitable for file storage
            public String toFileFormat() {
                return String.format("%s,%s,%s,%d", code, description, type, quantity);
            }

            @Override
            public String toString() {
                return toFileFormat();
            }
        }

        public static void createPPEFile() {
            // List to hold PPE items
            List<PPEItem> ppeItems = new ArrayList<>();
            
            // Add PPE items with initial data
            ppeItems.add(new PPEItem("HC", "Head Cover", "1", 100));
            ppeItems.add(new PPEItem("FS", "Face Shield", "1", 100));
            ppeItems.add(new PPEItem("MS", "Mask", "2", 100));
            ppeItems.add(new PPEItem("GL", "Gloves", "3", 100));
            ppeItems.add(new PPEItem("GW", "Gown", "3", 100));
            ppeItems.add(new PPEItem("SC", "Shoe Covers", "4", 100));

            // File path
            String filePath = PPE_FILE_NAME;

            // Write the PPE items to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (PPEItem item : ppeItems) {
                    writer.write(item.toString());
                    writer.newLine();
                }
                System.out.println("PPE file created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

private void showLoginScreen() {
    JPanel loginPanel = new JPanel(new GridLayout(2, 2));
    loginPanel.add(new JLabel("Username:"));
    JTextField usernameField = new JTextField();
    loginPanel.add(usernameField);
    loginPanel.add(new JLabel("Password:"));
    JPasswordField passwordField = new JPasswordField();
    loginPanel.add(passwordField);

    int option = JOptionPane.showConfirmDialog(null, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (option == JOptionPane.OK_OPTION) {
        String userID = usernameField.getText();
        String password = new String(passwordField.getPassword());

        String userType = authenticate(userID, password);
        if (userType != null) {
            if (userType.equals("admin")) {
                showAdminScreen();
            } else if (userType.equals("staff")) {
                showStaffScreen();
            } else {
                showErrorDialog("Invalid user type.");
                showLoginScreen();
            }
        } else {
            showErrorDialog("Invalid username or password.");
            showLoginScreen();
        }
    } else {
        System.exit(0);
    }
}
    private String authenticate(String userID, String password) {
        // Check if the login is for the admin with hardcoded credentials
        if (userID.equals("admin")) {
            if (password.equals("admin_password")) { // Replace "admin_password" with the actual admin password
                return "admin"; // Return admin type
            }
        }

        // Check if the username and password match any user in the users.txt file
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into parts based on comma
                String[] userData = line.split(",");

                // Check if the line has the correct number of parts (username, name, password, usertype, dateofregistration)
                if (userData.length == 5) {
                    String fileUserID = userData[0];
                    String filePassword = userData[2];
                    String fileUserType = userData[3];

                    // Validate the username, password, and usertype
                    if (fileUserID.equals(userID) && filePassword.equals(password)) {
                        return fileUserType; // Return user type (e.g., "admin", "staff")
                    }
                }
            }
        } catch (IOException e) {
            showErrorDialog("Error checking user credentials: " + e.getMessage());
        }
        return null;
    }

    private void showAdminScreen() {
        setTitle("User Management System");

        // Create mainPanel for the admin screen
        mainPanel = new JPanel(new BorderLayout());
        createMainMenu();
        cardPanel.add(mainPanel, "AdminScreen");
        cardLayout.show(cardPanel, "AdminScreen");

        getContentPane().add(cardPanel);
        setVisible(true); // Make the frame visible
    }

    private void createMainMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(5, 1)); // Adjusted to accommodate the Logout button
    
        JButton userManagementButton = new JButton("User Management");
        JButton supplierManagementButton = new JButton("Supplier Management");
        JButton exitButton = new JButton("Exit");
        JButton logoutButton = new JButton("Logout"); // New button
    
        userManagementButton.addActionListener(e -> showUserManagementPanel());
        supplierManagementButton.addActionListener(e -> showSupplierManagementPanel());
        exitButton.addActionListener(e -> System.exit(0));
        logoutButton.addActionListener(e -> logout()); // Action for logout button
    
        menuPanel.add(userManagementButton);
        menuPanel.add(supplierManagementButton);
        menuPanel.add(exitButton);
        menuPanel.add(logoutButton); // Add Logout button to menu panel
    
        mainPanel.add(menuPanel, BorderLayout.CENTER);
    }

    private void showUserManagementPanel() {
        JPanel userManagementPanel = createUserManagementPanel();
        cardPanel.add(userManagementPanel, "UserManagementPanel");
        cardLayout.show(cardPanel, "UserManagementPanel");
    }

    private void showSupplierManagementPanel() {
        JPanel supplierManagementPanel = createSupplierManagementPanel();
        cardPanel.add(supplierManagementPanel, "SupplierManagementPanel");
        cardLayout.show(cardPanel, "SupplierManagementPanel");
    }

    private JPanel createUserManagementPanel() {
        JPanel userPanel = new JPanel(new GridLayout(0, 2));
        userPanel.setBorder(BorderFactory.createTitledBorder("User Management"));

        // Create input fields and labels for user management
        JTextField userIDField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField userTypeField = new JTextField();

        userPanel.add(new JLabel("UserID:"));
        userPanel.add(userIDField);
        userPanel.add(new JLabel("Name:"));
        userPanel.add(nameField);
        userPanel.add(new JLabel("Password:"));
        userPanel.add(passwordField);
        userPanel.add(new JLabel("UserType (admin/staff):"));
        userPanel.add(userTypeField);

        // Create buttons and add action listeners
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1)); // Adjusted to accommodate the Back button
        JButton addUserButton = new JButton("Add User");
        JButton modifyUserButton = new JButton("Modify User");
        JButton searchUserButton = new JButton("Search User");
        JButton deleteUserButton = new JButton("Delete User");
        JButton backButton = new JButton("Back");

        addUserButton.addActionListener(e -> addUser(userIDField, nameField, passwordField, userTypeField));
        modifyUserButton.addActionListener(e -> modifyUser(userIDField, nameField, passwordField, userTypeField));
        searchUserButton.addActionListener(e -> searchUser(userIDField));
        deleteUserButton.addActionListener(e -> deleteUser(userIDField));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "AdminScreen")); // Go back to admin screen

        buttonPanel.add(addUserButton);
        buttonPanel.add(modifyUserButton);
        buttonPanel.add(searchUserButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(backButton);

        userPanel.add(buttonPanel);

        return userPanel;
    }

    private JPanel createSupplierManagementPanel() {
        JPanel supplierPanel = new JPanel(new GridLayout(0, 2));
        supplierPanel.setBorder(BorderFactory.createTitledBorder("Supplier Management"));

        // Create input fields and labels for supplier management
        JTextField supplierIDField = new JTextField();
        JTextField supplierNameField = new JTextField();
        JTextField supplierAddressField = new JTextField();
        JTextField supplierPhoneField = new JTextField();

        supplierPanel.add(new JLabel("Supplier ID:"));
        supplierPanel.add(supplierIDField);
        supplierPanel.add(new JLabel("Name:"));
        supplierPanel.add(supplierNameField);
        supplierPanel.add(new JLabel("Address:"));
        supplierPanel.add(supplierAddressField);
        supplierPanel.add(new JLabel("Phone:"));
        supplierPanel.add(supplierPhoneField);

        // Create buttons and add action listeners
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1)); // Adjusted to accommodate the Back button
        JButton addSupplierButton = new JButton("Add Supplier");
        JButton modifySupplierButton = new JButton("Modify Supplier");
        JButton searchSupplierButton = new JButton("Search Supplier");
        JButton deleteSupplierButton = new JButton("Delete Supplier");
        JButton backButton = new JButton("Back");

        addSupplierButton.addActionListener(e -> addSupplier(supplierIDField, supplierNameField, supplierAddressField, supplierPhoneField));
        modifySupplierButton.addActionListener(e -> modifySupplier(supplierIDField, supplierNameField, supplierAddressField, supplierPhoneField));
        searchSupplierButton.addActionListener(e -> searchSupplier(supplierIDField));
        deleteSupplierButton.addActionListener(e -> deleteSupplier(supplierIDField));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "AdminScreen")); // Go back to admin screen

        buttonPanel.add(addSupplierButton);
        buttonPanel.add(modifySupplierButton);
        buttonPanel.add(searchSupplierButton);
        buttonPanel.add(deleteSupplierButton);
        buttonPanel.add(backButton);

        supplierPanel.add(buttonPanel);

        return supplierPanel;
    }

    private void logout() {
        // Remove all components from the card panel
        cardPanel.removeAll();
    
        // Reset the card layout
        cardPanel.setLayout(new CardLayout());
        cardLayout = (CardLayout) cardPanel.getLayout();
    
        // Add the login screen back to the card panel
        showLoginScreen();
    
        // Update the content pane to show the card panel with the login screen
        getContentPane().removeAll();
        getContentPane().add(cardPanel);
        revalidate();
        repaint();
    }

    private void addUser(JTextField userIDField, JTextField nameField, JTextField passwordField, JTextField userTypeField) {
        String userID = userIDField.getText();
    
        if (!isUserIDUnique(userID)) {
            SwingUtilities.invokeLater(() -> showErrorDialog("UserID already exists."));
            return; // Exit method if UserID is not unique
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_NAME, true))) {
            String name = nameField.getText();
            String password = passwordField.getText();
            String userType = userTypeField.getText();
            String dateOfRegistration = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    
            writer.write(String.format("%s,%s,%s,%s,%s", userID, name, password, userType, dateOfRegistration));
            writer.newLine();
    
            SwingUtilities.invokeLater(() -> {
                if (outputArea != null) { // Check if outputArea is initialized
                    outputArea.append("User added successfully.\n");
                } else {
                    showErrorDialog("Output area not initialized.");
                }
            });
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> showErrorDialog("Error adding user: " + e.getMessage()));
        }
    }
    
    private boolean isUserIDUnique(String userID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 5 && userData[0].equals(userID)) { // Updated length check
                    return false; // UserID is not unique
                }
            }
        } catch (IOException e) {
            showErrorDialog("Error checking userID uniqueness: " + e.getMessage());
        }
        return true; // UserID is unique
    }

    private void modifyUser(JTextField userIDField, JTextField nameField, JTextField passwordField, JTextField userTypeField) {
        String userID = userIDField.getText();
        String newName = nameField.getText();
        String newPassword = passwordField.getText();
        String newUserType = userTypeField.getText().trim().toLowerCase();
    
        if (!newUserType.equals("admin") && !newUserType.equals("staff")) {
            showErrorDialog("Invalid user type. Must be 'admin' or 'staff'.");
            return;
        }
    
        File inputFile = new File(USER_FILE_NAME);
        File tempFile = new File("temp_users.txt");
    
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    
            String line;
            boolean userFound = false;
    
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    writer.write(userID + "," + newName + "," + newPassword + "," + newUserType + "," + userData[4]);
                    writer.newLine();
                    userFound = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
    
            if (!userFound) {
                showErrorDialog("UserID not found.");
            } else {
                showConfirmationDialog("User modified successfully: " + userID);
            }
    
        } catch (IOException e) {
            showErrorDialog("Error modifying user: " + e.getMessage());
        }
    
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    private void searchUser(JTextField userIDField) {
        String userID = userIDField.getText();
        StringBuilder userDetails = new StringBuilder();
        boolean userFound = false; // Declare and initialize the variable
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    userDetails.append("UserID: ").append(userData[0]).append("\n");
                    userDetails.append("Name: ").append(userData[1]).append("\n");
                    userDetails.append("Password: ").append(userData[2]).append("\n");
                    userDetails.append("User Type: ").append(userData[3]).append("\n");
                    userDetails.append("Date of Registration: ").append(userData[4]).append("\n");
                    userFound = true; // Set the flag to true if user is found
                    break; // Exit the loop once the user is found
                }
            }
            
            if (userFound) {
                displaySearchUserWindow(userID, userDetails.toString());
            } else {
                showErrorDialog("UserID not found.");
            }
            
        } catch (IOException e) {
            showErrorDialog("Error searching user: " + e.getMessage());
        }
    }
    
    private void displaySearchUserWindow(String userID, String userDetails) {
        JFrame searchFrame = new JFrame("Search Results");
        searchFrame.setSize(300, 200);
        searchFrame.setLocationRelativeTo(this);
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextArea searchResultArea = new JTextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setText(userDetails);
        searchPanel.add(new JScrollPane(searchResultArea), BorderLayout.CENTER);
    
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> searchFrame.dispose());
        searchPanel.add(closeButton, BorderLayout.SOUTH);
    
        searchFrame.add(searchPanel);
        searchFrame.setVisible(true);
    }  

    private void deleteUser(JTextField userIDField) {
        String userID = userIDField.getText();
    
        File inputFile = new File(USER_FILE_NAME);
        File tempFile = new File("temp_users.txt");
    
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    
            String line;
            boolean userFound = false;
    
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 4 && userData[0].equals(userID)) {
                    userFound = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
    
            if (!userFound) {
                showErrorDialog("UserID not found.");
            } else {
                showConfirmationDialog("User deleted successfully: " + userID);
            }
    
        } catch (IOException e) {
            showErrorDialog("Error deleting user: " + e.getMessage());
        }
    
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }  

    private void addSupplier(JTextField supplierIDField, JTextField supplierNameField, JTextField supplierContactField, JTextField supplierAddressField) {
        String supplierID = supplierIDField.getText();
        String name = supplierNameField.getText();
        String contact = supplierContactField.getText();
        String address = supplierAddressField.getText();
    
        try {
            Suppliers.addSupplier(supplierID, name, contact, address);
            showConfirmationDialog("Supplier added successfully: " + supplierID);
        } catch (IllegalArgumentException | IOException e) {
            showErrorDialog(e.getMessage());
        }
    }
    
    private void modifySupplier(JTextField supplierIDField, JTextField supplierNameField, JTextField supplierContactField, JTextField supplierAddressField) {
        String supplierID = supplierIDField.getText();
        String newName = supplierNameField.getText();
        String newContact = supplierContactField.getText();
        String newAddress = supplierAddressField.getText();
        
        try {
            // Call method to modify supplier and handle any exceptions
            Suppliers.modifySupplier(supplierID, newName, newContact, newAddress);
            showConfirmationDialog("Supplier modified successfully: " + supplierID);
        } catch (IllegalArgumentException e) {
            // Handle invalid supplier ID error
            showErrorDialog("Invalid Supplier ID: " + e.getMessage());
        } catch (IOException e) {
            // Handle IO exceptions
            showErrorDialog("Error modifying supplier: " + e.getMessage());
        }
    }
    
    private void searchSupplier(JTextField supplierIDField) {
        String supplierID = supplierIDField.getText();
        StringBuilder supplierDetails = new StringBuilder();
    
        try {
            // Call method to search for supplier and get the details
            String details = Suppliers.searchSupplier(supplierID);
            // Append the details if supplier is found
            supplierDetails.append(details);
            
            // Display the search results
            displaySearchSupplierWindow(supplierID, supplierDetails.toString());
        } catch (IllegalArgumentException e) {
            // Handle invalid Supplier ID error
            showErrorDialog("Error: " + e.getMessage());
        } catch (IOException e) {
            // Handle IO exceptions
            showErrorDialog("Error searching for supplier: " + e.getMessage());
        }
    }

    private void displaySearchSupplierWindow(String supplierID, String supplierDetails) {
        JFrame searchFrame = new JFrame("Search Results");
        searchFrame.setSize(300, 200);
        searchFrame.setLocationRelativeTo(this);
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextArea searchResultArea = new JTextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setText(supplierDetails);
        searchPanel.add(new JScrollPane(searchResultArea), BorderLayout.CENTER);
    
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> searchFrame.dispose());
        searchPanel.add(closeButton, BorderLayout.SOUTH);
    
        searchFrame.add(searchPanel);
        searchFrame.setVisible(true);
    }

    private void deleteSupplier(JTextField supplierIDField) {
        String supplierID = supplierIDField.getText();
    
        try {
            // Call method to delete supplier and handle any exceptions
            Suppliers.deleteSupplier(supplierID);
            showConfirmationDialog("Supplier deleted successfully: " + supplierID);
        } catch (IOException e) {
            showErrorDialog("Error deleting supplier: " + e.getMessage());
        }
    }

    private void showConfirmationDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showStaffScreen() {
        // Code to display the staff screen
        System.out.println("Staff screen not yet implemented.");
        // Example: new StaffScreen().setVisible(true);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger.getLogger(UserManagementGUI.class.getName()).log(Level.SEVERE, null, e);
        }

        SwingUtilities.invokeLater(() -> new UserManagementGUI().setVisible(true));
    }
}