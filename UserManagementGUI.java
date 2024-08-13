import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagementGUI extends JFrame {
    private static final String USER_FILE_NAME = "users.txt";
    private static final String SUPPLIER_FILE_NAME = "suppliers.txt";
    private static final String PPE_FILE_NAME = "ppe.txt";
    private static final String HOSPITAL_FILE_NAME = "hospitals.txt";

    private JPanel mainPanel;
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
            if (!isHospitalFileExists()) {
                initializeHospitalFile();
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

    private boolean isHospitalFileExists() {
        File hospitalFile = new File(HOSPITAL_FILE_NAME);
        return hospitalFile.exists();
    }

    private void initializePPEFile() {
        // Call the static method from the PPE class to create the PPE file
        PPE.createPPEFile();
    }

    private void initializeHospitalFile() {
        Hospitals.initializeHospitalFile();
    }

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
            
            ppeItems.add(new PPEItem("HC", "Head Cover", "1", 100));
            ppeItems.add(new PPEItem("FS", "Face Shield", "1", 100));
            ppeItems.add(new PPEItem("MS", "Mask", "2", 100));
            ppeItems.add(new PPEItem("GL", "Gloves", "3", 100));
            ppeItems.add(new PPEItem("GW", "Gown", "3", 100));
            ppeItems.add(new PPEItem("SC", "Shoe Covers", "4", 100));

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
        
        int option = JOptionPane.showConfirmDialog(this, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
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
            if (password.equals("admin_password")) {
                return "admin";
            }
        }

        // Check if the username and password match any user in the users.txt file
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");

                // Check if the line has the correct number of data
                if (userData.length == 5) {
                    String fileUserID = userData[0];
                    String filePassword = userData[2];
                    String fileUserType = userData[3];

                    // Validate the username, password, and usertype
                    if (fileUserID.equals(userID) && filePassword.equals(password)) {
                        return fileUserType;
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

        mainPanel = new JPanel(new BorderLayout());
        createMainMenu();
        cardPanel.add(mainPanel, "AdminScreen");
        cardLayout.show(cardPanel, "AdminScreen");

        getContentPane().add(cardPanel);
        setVisible(true);
    }

    private void createMainMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(6, 1));
    
        JButton userManagementButton = new JButton("User Management");
        JButton supplierManagementButton = new JButton("Supplier Management");
        JButton hospitalManagementButton = new JButton("Hospital Management");
        JButton exitButton = new JButton("Exit");
        JButton logoutButton = new JButton("Logout");
    
        userManagementButton.addActionListener(e -> showUserManagementPanel());
        supplierManagementButton.addActionListener(e -> showSupplierManagementPanel());
        hospitalManagementButton.addActionListener(e -> showHospitalManagementPanel());
        exitButton.addActionListener(e -> System.exit(0));
        logoutButton.addActionListener(e -> logout());
    
        menuPanel.add(userManagementButton);
        menuPanel.add(supplierManagementButton);
        menuPanel.add(hospitalManagementButton);
        menuPanel.add(exitButton);
        menuPanel.add(logoutButton);
    
        mainPanel.add(menuPanel, BorderLayout.CENTER);
    }

    private void showHospitalManagementPanel() {
        JPanel hospitalManagementPanel = createHospitalManagementPanel();
        cardPanel.add(hospitalManagementPanel, "HospitalManagementPanel");
        cardLayout.show(cardPanel, "HospitalManagementPanel");
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

    private JPanel createHospitalManagementPanel() {
        JPanel hospitalPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1)); // Changed to 5 to accommodate the new button
    
        JButton addHospitalButton = new JButton("Add Hospital");
        JButton modifyHospitalButton = new JButton("Modify Hospital");
        JButton searchHospitalButton = new JButton("Search Hospital");
        JButton deleteHospitalButton = new JButton("Delete Hospital");
        JButton backButton = new JButton("Back"); // Added the Back button
    
        addHospitalButton.addActionListener(e -> showAddHospitalPanel());
        modifyHospitalButton.addActionListener(e -> showModifySearchDeleteHospitalPanel("Modify"));
        searchHospitalButton.addActionListener(e -> showModifySearchDeleteHospitalPanel("Search"));
        deleteHospitalButton.addActionListener(e -> showModifySearchDeleteHospitalPanel("Delete"));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "AdminScreen")); // Action for Back button
    
        buttonPanel.add(addHospitalButton);
        buttonPanel.add(modifyHospitalButton);
        buttonPanel.add(searchHospitalButton);
        buttonPanel.add(deleteHospitalButton);
        buttonPanel.add(backButton); // Added Back button to panel
    
        hospitalPanel.add(buttonPanel, BorderLayout.CENTER);
        return hospitalPanel;
    }
    

    private void showAddHospitalPanel() {
        JPanel addHospitalPanel = new JPanel(new GridLayout(7, 2));
        addHospitalPanel.add(new JLabel("Hospital ID:"));
        JTextField idField = new JTextField();
        addHospitalPanel.add(idField);
        addHospitalPanel.add(new JLabel("Hospital Name:"));
        JTextField nameField = new JTextField();
        addHospitalPanel.add(nameField);
        addHospitalPanel.add(new JLabel("Hospital Address:"));
        JTextField addressField = new JTextField();
        addHospitalPanel.add(addressField);
        addHospitalPanel.add(new JLabel("Phone Number:"));
        JTextField phoneField = new JTextField();
        addHospitalPanel.add(phoneField);
        
        JButton addButton = new JButton("Add");
        addHospitalPanel.add(addButton);
        
        JButton backButton = new JButton("Back");
        addHospitalPanel.add(backButton);
        
        addButton.addActionListener(e -> addHospital(idField, nameField, addressField, phoneField));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "HospitalManagementPanel")); // Return to the hospital management panel
        
        JPanel addHospitalContainer = new JPanel(new BorderLayout());
        addHospitalContainer.add(addHospitalPanel, BorderLayout.CENTER);
    
        cardPanel.add(addHospitalContainer, "AddHospitalPanel");
        cardLayout.show(cardPanel, "AddHospitalPanel");
    }
    

    private void showModifySearchDeleteHospitalPanel(String action) {
        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Hospital ID:"));
        JTextField idField = new JTextField();
        panel.add(idField);
        
        if (action.equals("Search")) {
            JButton searchButton = new JButton("Search");
            panel.add(searchButton);
    
            searchButton.addActionListener(e -> {
                String id = idField.getText();
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Hospital ID must be provided.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                searchHospital(idField);
            });
        } else {
            panel.add(new JLabel("Hospital Name:"));
            JTextField nameField = new JTextField();
            panel.add(nameField);
            panel.add(new JLabel("Hospital Address:"));
            JTextField addressField = new JTextField();
            panel.add(addressField);
            panel.add(new JLabel("Phone Number:"));
            JTextField phoneField = new JTextField();
            panel.add(phoneField);

            JButton actionButton = new JButton(action);
            panel.add(actionButton);
    
            actionButton.addActionListener(e -> {
                String id = idField.getText();
                String name = nameField.getText();
                String address = addressField.getText();
                String phone = phoneField.getText();
    
                if (id.isEmpty() || (action.equals("Modify") && (name.isEmpty() || address.isEmpty() || phone.isEmpty()))) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                try {
                    if (action.equals("Modify")) {
                        modifyHospital(idField.getText(), nameField.getText(), addressField.getText(), phoneField.getText());
                    } else if (action.equals("Delete")) {
                        deleteHospital(idField.getText());
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
        
        JButton backButton = new JButton("Back");
        panel.add(backButton);
    
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "HospitalManagementPanel"));
        
        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);
    
        cardPanel.add(container, action + "HospitalPanel");
        cardLayout.show(cardPanel, action + "HospitalPanel");
    }

    private JPanel createUserManagementPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1));
    
        JButton addUserButton = new JButton("Add User");
        JButton modifyUserButton = new JButton("Modify User");
        JButton searchUserButton = new JButton("Search User");
        JButton deleteUserButton = new JButton("Delete User");
        JButton backButton = new JButton("Back");
    
        addUserButton.addActionListener(e -> showAddUserPanel());
        modifyUserButton.addActionListener(e -> showModifySearchDeleteUserPanel("Modify"));
        searchUserButton.addActionListener(e -> showModifySearchDeleteUserPanel("Search"));
        deleteUserButton.addActionListener(e -> showModifySearchDeleteUserPanel("Delete"));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "AdminScreen"));
    
        buttonPanel.add(addUserButton);
        buttonPanel.add(modifyUserButton);
        buttonPanel.add(searchUserButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(backButton);
    
        userPanel.add(buttonPanel, BorderLayout.CENTER);
        return userPanel;
    }

    private JPanel createSupplierManagementPanel() {
        JPanel supplierPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1));
    
        JButton addSupplierButton = new JButton("Add Supplier");
        JButton modifySupplierButton = new JButton("Modify Supplier");
        JButton searchSupplierButton = new JButton("Search Supplier");
        JButton deleteSupplierButton = new JButton("Delete Supplier");
        JButton backButton = new JButton("Back");
    
        addSupplierButton.addActionListener(e -> showAddSupplierPanel());
        modifySupplierButton.addActionListener(e -> showModifySearchDeleteSupplierPanel("Modify"));
        searchSupplierButton.addActionListener(e -> showModifySearchDeleteSupplierPanel("Search"));
        deleteSupplierButton.addActionListener(e -> showModifySearchDeleteSupplierPanel("Delete"));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "AdminScreen"));
    
        buttonPanel.add(addSupplierButton);
        buttonPanel.add(modifySupplierButton);
        buttonPanel.add(searchSupplierButton);
        buttonPanel.add(deleteSupplierButton);
        buttonPanel.add(backButton);
    
        supplierPanel.add(buttonPanel, BorderLayout.CENTER);
        return supplierPanel;
    }

    private void showAddUserPanel() {
        JPanel addUserPanel = new JPanel(new GridLayout(5, 2));
        addUserPanel.add(new JLabel("UserID:"));
        JTextField userIDField = new JTextField();
        addUserPanel.add(userIDField);
        addUserPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        addUserPanel.add(nameField);
        addUserPanel.add(new JLabel("Password:"));
        JTextField passwordField = new JTextField();
        addUserPanel.add(passwordField);
        addUserPanel.add(new JLabel("UserType (admin/staff):"));
        JTextField userTypeField = new JTextField();
        addUserPanel.add(userTypeField);
    
        JButton addButton = new JButton("Add");
        addUserPanel.add(addButton);
    
        JButton backButton = new JButton("Back");
        addUserPanel.add(backButton);
    
        addButton.addActionListener(e -> addUser(userIDField, nameField, passwordField, userTypeField));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "UserManagementPanel"));
    
        JPanel container = new JPanel(new BorderLayout());
        container.add(addUserPanel, BorderLayout.CENTER);
    
        cardPanel.add(container, "AddUserPanel");
        cardLayout.show(cardPanel, "AddUserPanel");
    }

    private void showModifySearchDeleteUserPanel(String action) {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("UserID:"));
        JTextField userIDField = new JTextField();
        panel.add(userIDField);

        if (action.equals("Search")) {
            JButton searchButton = new JButton("Search");
            panel.add(searchButton);

            searchButton.addActionListener(e -> searchUser(userIDField.getText()));
        } else {
            panel.add(new JLabel("Name:"));
            JTextField nameField = new JTextField();
            panel.add(nameField);
            panel.add(new JLabel("Password:"));
            JTextField passwordField = new JTextField();
            panel.add(passwordField);
            panel.add(new JLabel("UserType (admin/staff):"));
            JTextField userTypeField = new JTextField();
            panel.add(userTypeField);

            JButton actionButton = new JButton(action);
            panel.add(actionButton);

            actionButton.addActionListener(e -> {
                String id = userIDField.getText();
                String name = nameField.getText();
                String password = passwordField.getText();
                String userType = userTypeField.getText();

                if (id.isEmpty() || (action.equals("Modify") && (name.isEmpty() || password.isEmpty() || userType.isEmpty()))) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    if (action.equals("Modify")) {
                        modifyUser(id, name, password, userType);
                    } else if (action.equals("Delete")) {
                        deleteUser(id);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        JButton backButton = new JButton("Back");
        panel.add(backButton);

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "UserManagementPanel"));

        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);

        cardPanel.add(container, action + "UserPanel");
        cardLayout.show(cardPanel, action + "UserPanel");
    }
            

    private void showAddSupplierPanel() {
        JPanel addSupplierPanel = new JPanel(new GridLayout(5, 2));
        addSupplierPanel.add(new JLabel("Supplier ID:"));
        JTextField supplierIDField = new JTextField();
        addSupplierPanel.add(supplierIDField);
        addSupplierPanel.add(new JLabel("Name:"));
        JTextField supplierNameField = new JTextField();
        addSupplierPanel.add(supplierNameField);
        addSupplierPanel.add(new JLabel("Address:"));
        JTextField supplierAddressField = new JTextField();
        addSupplierPanel.add(supplierAddressField);
        addSupplierPanel.add(new JLabel("Phone:"));
        JTextField supplierPhoneField = new JTextField();
        addSupplierPanel.add(supplierPhoneField);
    
        JButton addButton = new JButton("Add");
        addSupplierPanel.add(addButton);
    
        JButton backButton = new JButton("Back");
        addSupplierPanel.add(backButton);
    
        addButton.addActionListener(e -> addSupplier(supplierIDField, supplierNameField, supplierAddressField, supplierPhoneField));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "SupplierManagementPanel"));
    
        JPanel container = new JPanel(new BorderLayout());
        container.add(addSupplierPanel, BorderLayout.CENTER);
    
        cardPanel.add(container, "AddSupplierPanel");
        cardLayout.show(cardPanel, "AddSupplierPanel");
    }
    
    private void showModifySearchDeleteSupplierPanel(String action) {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Supplier ID:"));
        JTextField supplierIDField = new JTextField();
        panel.add(supplierIDField);

        if (action.equals("Search")) {
            JButton searchButton = new JButton("Search");
            panel.add(searchButton);

            searchButton.addActionListener(e -> searchSupplier(supplierIDField.getText()));
        } else {
            panel.add(new JLabel("Name:"));
            JTextField supplierNameField = new JTextField();
            panel.add(supplierNameField);
            panel.add(new JLabel("Address:"));
            JTextField supplierAddressField = new JTextField();
            panel.add(supplierAddressField);
            panel.add(new JLabel("Phone:"));
            JTextField supplierPhoneField = new JTextField();
            panel.add(supplierPhoneField);

            JButton actionButton = new JButton(action);
            panel.add(actionButton);

            actionButton.addActionListener(e -> {
                String id = supplierIDField.getText();
                String name = supplierNameField.getText();
                String address = supplierAddressField.getText();
                String phone = supplierPhoneField.getText();

                if (id.isEmpty() || (action.equals("Modify") && (name.isEmpty() || address.isEmpty() || phone.isEmpty()))) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    if (action.equals("Modify")) {
                        modifySupplier(id, name, address, phone);
                    } else if (action.equals("Delete")) {
                        deleteSupplier(id);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        JButton backButton = new JButton("Back");
        panel.add(backButton);

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "SupplierManagementPanel"));

        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);

        cardPanel.add(container, action + "SupplierPanel");
        cardLayout.show(cardPanel, action + "SupplierPanel");
    }

    private void logout() {
        cardPanel.removeAll();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        showLoginScreen();
        getContentPane().removeAll();
        getContentPane().add(cardPanel);
        revalidate();
        repaint();
    }

    private void addUser(JTextField userIDField, JTextField nameField, JTextField passwordField, JTextField userTypeField) {
        String userID = userIDField.getText().trim();
        String name = nameField.getText().trim();
        String password = passwordField.getText().trim();
        String userType = userTypeField.getText().trim();

        // Validate inputs
        if (userID.isEmpty() || name.isEmpty() || password.isEmpty() || userType.isEmpty()) {
            showErrorDialog("All fields must be filled out.");
            return;
        }

        if (!userType.equalsIgnoreCase("admin") && !userType.equalsIgnoreCase("staff")) {
            showErrorDialog("Invalid user type. Must be 'admin' or 'staff'.");
            return;
        }

        try {
            Userstuff.addUser(userID, name, password, userType);
            showConfirmationDialog("User added successfully: " + userID);
        } catch (IllegalArgumentException e) {
            showErrorDialog(e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Error adding user: " + e.getMessage());
        }
    }

    private void modifyUser(String id, String name, String password, String userType) {
        if (id.isEmpty() || name.isEmpty() || password.isEmpty() || userType.isEmpty()) {
            showErrorDialog("All fields must be filled out.");
            return;
        }
    
        try {
            Userstuff.modifyUser(id, name, password, userType); // Assuming Users is your user management class
            showConfirmationDialog("User modified successfully: " + id);
        } catch (IllegalArgumentException e) {
            showErrorDialog("Invalid User ID: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Error modifying user: " + e.getMessage());
        }
    }

    private void searchUser(String userID) {
        if (userID.isEmpty()) {
            showErrorDialog("User ID cannot be empty.");
            return;
        }
    
        try {
            String details = Userstuff.searchUser(userID); // Assuming Users is your user management class
            displaySearchUserWindow(userID, details); // Implement this method to display user details
        } catch (IllegalArgumentException e) {
            showErrorDialog("Error: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Error searching for user: " + e.getMessage());
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

    private void deleteUser(String id) {
        if (id.isEmpty()) {
            showErrorDialog("User ID cannot be empty.");
            return;
        }
    
        try {
            Userstuff.deleteUser(id); // Assuming Users is your user management class
            showConfirmationDialog("User deleted successfully: " + id);
        } catch (IllegalArgumentException e) {
            showErrorDialog("Invalid User ID: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Error deleting user: " + e.getMessage());
        } catch (Exception e) {
            showErrorDialog("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    private void addSupplier(JTextField supplierIDField, JTextField supplierNameField, JTextField supplierAddressField, JTextField supplierPhoneField) {
        String supplierID = supplierIDField.getText().trim();
        String name = supplierNameField.getText().trim();
        String address = supplierAddressField.getText().trim();
        String phone = supplierPhoneField.getText().trim();
    
        if (supplierID.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            showErrorDialog("All fields must be filled out.");
            return;
        }
    
        try {
            Suppliers.addSupplier(supplierID, name, phone, address);
            showConfirmationDialog("Supplier added successfully: " + supplierID);
        } catch (IllegalArgumentException | IOException e) {
            showErrorDialog(e.getMessage());
        }
    }
    
    
    private void modifySupplier(String supplierID, String newName, String newAddress, String newPhone) {
        if (supplierID.isEmpty() || newName.isEmpty() || newAddress.isEmpty() || newPhone.isEmpty()) {
            showErrorDialog("All fields must be filled out.");
            return;
        }
    
        try {
            Suppliers.modifySupplier(supplierID, newName, newPhone, newAddress);
            showConfirmationDialog("Supplier modified successfully: " + supplierID);
        } catch (IllegalArgumentException e) {
            showErrorDialog("Invalid Supplier ID: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Error modifying supplier: " + e.getMessage());
        }
    }
    
    
    private void searchSupplier(String supplierID) {
        if (supplierID.isEmpty()) {
            showErrorDialog("Supplier ID cannot be empty.");
            return;
        }
    
        try {
            String details = Suppliers.searchSupplier(supplierID);
            displaySearchSupplierWindow(supplierID, details);
        } catch (IllegalArgumentException e) {
            showErrorDialog("Error: " + e.getMessage());
        } catch (IOException e) {
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

    private void deleteSupplier(String supplierID) {
        if (supplierID.isEmpty()) {
            showErrorDialog("Supplier ID cannot be empty.");
            return;
        }
    
        try {
            Suppliers.deleteSupplier(supplierID);
            showConfirmationDialog("Supplier deleted successfully: " + supplierID);
        } catch (IllegalArgumentException e) {
            showErrorDialog("Invalid Supplier ID: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Error deleting supplier: " + e.getMessage());
        } catch (Exception e) {
            showErrorDialog("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addHospital(JTextField idField, JTextField nameField, JTextField addressField, JTextField phoneField) {
        String id = idField.getText();
        String name = nameField.getText();
        String address = addressField.getText();
        String contact = phoneField.getText(); // Assuming phone field is used for contact info
    
        if (id.isEmpty() || name.isEmpty() || address.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }
    
        try {
            Hospitals.addHospital(id, name, address, contact);
            JOptionPane.showMessageDialog(this, "Hospital added successfully.");
        } catch (IllegalArgumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error adding hospital: " + e.getMessage());
        }
    }

    private void modifyHospital(String idToModify, String newName, String newAddress, String newPhone) {
        try {
            Hospitals.modifyHospital(idToModify, newName, newAddress, newPhone);
            JOptionPane.showMessageDialog(this, "Hospital modified successfully.");
        } catch (IllegalArgumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error modifying hospital: " + e.getMessage());
        }
    }

    private void searchHospital(JTextField idField) {
        String id = idField.getText();
        
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hospital ID must be provided.");
            return;
        }
    
        try {
            String details = Hospitals.searchHospital(id);
            JOptionPane.showMessageDialog(this, details);
        } catch (IllegalArgumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error searching hospital: " + e.getMessage());
        }
    }

    private void deleteHospital(String id) {
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hospital ID must be provided.");
            return;
        }
    
        try {
            Hospitals.deleteHospital(id);
            JOptionPane.showMessageDialog(this, "Hospital deleted successfully.");
        } catch (IllegalArgumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error deleting hospital: " + e.getMessage());
        }
    }
    

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showConfirmationDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStaffScreen() {
        // Code to display the staff screen
        System.out.println("Staff screen not yet implemented.");
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