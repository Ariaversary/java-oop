import javax.swing.*;
import java.awt.*;
import java.io.*;

public class UserManagementGUI extends JFrame {
    private static final String USER_FILE_NAME = "users.txt";
    private static final String SUPPLIER_FILE_NAME = "suppliers.txt";
    private static final int MAX_SUPPLIERS = 4;

    private JPanel mainPanel;
    private JPanel operationPanel;
    private JTextArea outputArea;

    public UserManagementGUI() {
        // Initialize the main frame
        setTitle("User Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize panels
        mainPanel = new JPanel(new BorderLayout());
        operationPanel = new JPanel(new BorderLayout());

        // Set up output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        operationPanel.add(scrollPane, BorderLayout.SOUTH);

        // Initialize files
        try {
            initializeFiles();
        } catch (IOException e) {
            showErrorDialog("Error initializing files: " + e.getMessage());
        }

        // Show login screen before displaying the main UI
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
        File ppeFile = new File("ppe.txt");
        if (!ppeFile.exists()) {
            ppeFile.createNewFile();
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
                    showAdminScreen(); // Redirect to admin screen
                } else if (userType.equals("staff")) {
                    showStaffScreen(); // Redirect to staff screen
                } else {
                    showErrorDialog("Invalid user type.");
                    showLoginScreen(); // Show login screen again if authentication fails
                }
            } else {
                showErrorDialog("Invalid username or password.");
                showLoginScreen(); // Show login screen again if authentication fails
            }
        } else {
            System.exit(0); // Exit if the user cancels the login
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
                    
                    // Debug output
                    System.out.println("Checking user: " + fileUserID + " with password: " + filePassword + " and usertype: " + fileUserType);
                    
                    // Validate the username, password, and usertype
                    if (fileUserID.equals(userID) && filePassword.equals(password)) {
                        System.out.println("User authenticated successfully.");
                        return fileUserType; // Return user type (e.g., "admin", "staff")
                    }
                } else {
                    // Log or print to help debug incorrect format
                    System.out.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            showErrorDialog("Error checking user credentials: " + e.getMessage());
        }
        
        // If no match is found, return null
        System.out.println("Authentication failed for user: " + userID);
        return null;
    }
    

    private void showAdminScreen() {
        setTitle("User Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the main panel
        mainPanel = new JPanel(new BorderLayout());
        operationPanel = new JPanel(new BorderLayout());

        // Create and add the main menu
        createMainMenu();
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        setVisible(true); // Make the frame visible
    }

    private void createMainMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(4, 1));

        JButton userManagementButton = new JButton("User Management");
        JButton supplierManagementButton = new JButton("Supplier Management");
        JButton exitButton = new JButton("Exit");

        userManagementButton.addActionListener(e -> showUserManagementPanel());
        supplierManagementButton.addActionListener(e -> showSupplierManagementPanel());
        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(userManagementButton);
        menuPanel.add(supplierManagementButton);
        menuPanel.add(exitButton);

        mainPanel.add(menuPanel, BorderLayout.CENTER);
    }

    private void showUserManagementPanel() {
        mainPanel.setVisible(false);

        operationPanel.removeAll();
        operationPanel.add(createUserManagementPanel(), BorderLayout.CENTER);
        operationPanel.revalidate();
        operationPanel.repaint();
        getContentPane().add(operationPanel, BorderLayout.CENTER);
    }

    private void showSupplierManagementPanel() {
        mainPanel.setVisible(false);

        operationPanel.removeAll();
        operationPanel.add(createSupplierManagementPanel(), BorderLayout.CENTER);
        operationPanel.revalidate();
        operationPanel.repaint();
        getContentPane().add(operationPanel, BorderLayout.CENTER);
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
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        JButton addUserButton = new JButton("Add User");
        JButton modifyUserButton = new JButton("Modify User");
        JButton searchUserButton = new JButton("Search User");
        JButton deleteUserButton = new JButton("Delete User");

        addUserButton.addActionListener(e -> addUser(userIDField, nameField, passwordField, userTypeField));
        modifyUserButton.addActionListener(e -> modifyUser(userIDField, nameField, passwordField, userTypeField));
        searchUserButton.addActionListener(e -> searchUser(userIDField));
        deleteUserButton.addActionListener(e -> deleteUser(userIDField));

        buttonPanel.add(addUserButton);
        buttonPanel.add(modifyUserButton);
        buttonPanel.add(searchUserButton);
        buttonPanel.add(deleteUserButton);

        userPanel.add(buttonPanel);

        return userPanel;
    }

    private JPanel createSupplierManagementPanel() {
        JPanel supplierPanel = new JPanel(new GridLayout(0, 2));
        supplierPanel.setBorder(BorderFactory.createTitledBorder("Supplier Management"));

        // Create input fields and labels for supplier management
        JTextField supplierIDField = new JTextField();
        JTextField supplierNameField = new JTextField();
        JTextField supplierContactField = new JTextField();
        JTextField supplierAddressField = new JTextField();

        supplierPanel.add(new JLabel("SupplierID:"));
        supplierPanel.add(supplierIDField);
        supplierPanel.add(new JLabel("Supplier Name:"));
        supplierPanel.add(supplierNameField);
        supplierPanel.add(new JLabel("Supplier Contact:"));
        supplierPanel.add(supplierContactField);
        supplierPanel.add(new JLabel("Supplier Address:"));
        supplierPanel.add(supplierAddressField);

        // Create buttons and add action listeners
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        JButton addSupplierButton = new JButton("Add Supplier");
        JButton modifySupplierButton = new JButton("Modify Supplier");
        JButton searchSupplierButton = new JButton("Search Supplier");
        JButton deleteSupplierButton = new JButton("Delete Supplier");

        addSupplierButton.addActionListener(e -> addSupplier(supplierIDField, supplierNameField, supplierContactField, supplierAddressField));
        modifySupplierButton.addActionListener(e -> modifySupplier(supplierIDField, supplierNameField, supplierContactField, supplierAddressField));
        searchSupplierButton.addActionListener(e -> searchSupplier(supplierIDField));
        deleteSupplierButton.addActionListener(e -> deleteSupplier(supplierIDField));

        buttonPanel.add(addSupplierButton);
        buttonPanel.add(modifySupplierButton);
        buttonPanel.add(searchSupplierButton);
        buttonPanel.add(deleteSupplierButton);

        supplierPanel.add(buttonPanel);

        return supplierPanel;
    }

    private void addUser(JTextField userIDField, JTextField nameField, JTextField passwordField, JTextField userTypeField) {
        String userID = userIDField.getText();
        if (userID.isEmpty() || !isUserIDUnique(userID)) {
            showErrorDialog("UserID already exists or is empty.");
            return;
        }

        String name = nameField.getText();
        String password = passwordField.getText();
        String userType = userTypeField.getText().trim().toLowerCase();

        if (!userType.equals("admin") && !userType.equals("staff")) {
            showErrorDialog("Invalid user type. Must be 'admin' or 'staff'.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_NAME, true))) {
            writer.write(userID + "," + name + "," + password + "," + userType);
            writer.newLine();
            outputArea.append("User added successfully: " + userID + "\n");
        } catch (IOException e) {
            showErrorDialog("Error adding user: " + e.getMessage());
        }
    }

    private boolean isUserIDUnique(String userID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 4 && userData[0].equals(userID)) {
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
        String name = nameField.getText();
        String password = passwordField.getText();
        String userType = userTypeField.getText().trim().toLowerCase();

        if (!userType.equals("admin") && !userType.equals("staff")) {
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
                if (userData.length == 4 && userData[0].equals(userID)) {
                    writer.write(userID + "," + name + "," + password + "," + userType);
                    writer.newLine();
                    userFound = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }

            if (!userFound) {
                showErrorDialog("UserID not found.");
            }

        } catch (IOException e) {
            showErrorDialog("Error modifying user: " + e.getMessage());
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);

        outputArea.append("User modified successfully: " + userID + "\n");
    }

    private void searchUser(JTextField userIDField) {
        String userID = userIDField.getText();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 4 && userData[0].equals(userID)) {
                    outputArea.append("User found: " + line + "\n");
                    return;
                }
            }
            outputArea.append("UserID not found.\n");
        } catch (IOException e) {
            showErrorDialog("Error searching user: " + e.getMessage());
        }
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
            }

        } catch (IOException e) {
            showErrorDialog("Error deleting user: " + e.getMessage());
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);

        outputArea.append("User deleted successfully: " + userID + "\n");
    }

    private void addSupplier(JTextField supplierIDField, JTextField supplierNameField, JTextField supplierContactField, JTextField supplierAddressField) {
        String supplierID = supplierIDField.getText();
        String name = supplierNameField.getText();
        String contact = supplierContactField.getText();
        String address = supplierAddressField.getText();
    
        try {
            Suppliers.addSupplier(supplierID, name, contact, address);
        } catch (IOException e) {
            showErrorDialog("Error adding supplier: " + e.getMessage());
        }
    }
    
    private void modifySupplier(JTextField supplierIDField, JTextField supplierNameField, JTextField supplierContactField, JTextField supplierAddressField) {
        String supplierID = supplierIDField.getText();
        String newName = supplierNameField.getText();
        String newContact = supplierContactField.getText();
        String newAddress = supplierAddressField.getText();
    
        try {
            Suppliers.modifySupplier(supplierID, newName, newContact, newAddress);
        } catch (IOException e) {
            showErrorDialog("Error modifying supplier: " + e.getMessage());
        }
    }
    
    private void searchSupplier(JTextField supplierIDField) {
        String supplierID = supplierIDField.getText();
    
        try {
            Suppliers.searchSupplier(supplierID);
        } catch (IOException e) {
            showErrorDialog("Error searching for supplier: " + e.getMessage());
        }
    }
    

    private void deleteSupplier(JTextField supplierIDField) {
        String supplierID = supplierIDField.getText();
    
        try {
            Suppliers.deleteSupplier(supplierID);
        } catch (IOException e) {
            showErrorDialog("Error deleting supplier: " + e.getMessage());
        }
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
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
        } catch(Exception ignored){}
        SwingUtilities.invokeLater(() -> new UserManagementGUI());
    }
}