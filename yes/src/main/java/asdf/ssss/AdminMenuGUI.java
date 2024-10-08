package asdf.ssss;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class AdminMenuGUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Map<String, JPanel> panelMap;

    public AdminMenuGUI() {
        // starts frame and panels
        setTitle("Admin Menu");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cardPanel.getLayout();
        getContentPane().add(cardPanel);
        panelMap = new HashMap<>();

        showAdminScreen();
    }

    private void showAdminScreen() {
        setTitle("Admin Menu");

        if (mainPanel == null) {
            mainPanel = new JPanel(new BorderLayout());
            createAdminMenu(); // this method adds components to mainPanel
            cardPanel.add(mainPanel, "AdminScreen");
        }

        cardLayout.show(cardPanel, "AdminScreen");
        setVisible(true); // ensures the screen is visible
    }

    private void createAdminMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(7, 1));
    
        JButton userManagementButton = new JButton("User Management");
        JButton supplierManagementButton = new JButton("Supplier Management");
        JButton hospitalManagementButton = new JButton("Hospital Management");
        JButton orderSearchButton = new JButton("Search Orders");
        JButton exitButton = new JButton("Exit");
        JButton logoutButton = new JButton("Logout");
    
        //action listeners
        userManagementButton.addActionListener(e -> showUserManagementPanel());
        supplierManagementButton.addActionListener(e -> showSupplierManagementPanel());
        hospitalManagementButton.addActionListener(e -> showHospitalManagementPanel());
        orderSearchButton.addActionListener(e -> showOrderSearchPanel());
        exitButton.addActionListener(e -> System.exit(0));
        logoutButton.addActionListener(e -> logout());
    
        //buttons to the panel
        menuPanel.add(userManagementButton);
        menuPanel.add(supplierManagementButton);
        menuPanel.add(hospitalManagementButton);
        menuPanel.add(orderSearchButton);
        menuPanel.add(exitButton);
        menuPanel.add(logoutButton);
    
        mainPanel.add(menuPanel, BorderLayout.CENTER);
    }
        
        private void showOrderSearchPanel() {
            JPanel searchPanel = new JPanel(new GridLayout(6, 2));
        
            searchPanel.add(new JLabel("Item Code:"));
            JTextField itemCodeField = new JTextField();
            searchPanel.add(itemCodeField);
        
            searchPanel.add(new JLabel("Hospital/Supplier ID:"));
            JTextField idField = new JTextField();
            searchPanel.add(idField);
        
            searchPanel.add(new JLabel("Date (yyyy-mm-dd):"));
            JTextField dateField = new JTextField();
            searchPanel.add(dateField);
        
            searchPanel.add(new JLabel("Quantity (min):"));
            JTextField minQuantityField = new JTextField();
            searchPanel.add(minQuantityField);
        
            searchPanel.add(new JLabel("Quantity (max):"));
            JTextField maxQuantityField = new JTextField();
            searchPanel.add(maxQuantityField);
        
            JButton searchButton = new JButton("Search");
            searchPanel.add(searchButton);
        
            //search button
            searchButton.addActionListener(e -> {
                String itemCode = itemCodeField.getText();
                String id = idField.getText();
                String date = dateField.getText();
                String minQuantity = minQuantityField.getText();
                String maxQuantity = maxQuantityField.getText();
                Search search = new Search();
                search.searchOrders(this, itemCode, id, date, minQuantity, maxQuantity);
            });
        
            JButton backButton = new JButton("Back");
            searchPanel.add(backButton);
            backButton.addActionListener(e -> cardLayout.show(cardPanel, "AdminScreen"));
        
            JPanel container = new JPanel(new BorderLayout());
            container.add(searchPanel, BorderLayout.CENTER);
            cardPanel.add(container, "OrderSearchPanel");
            cardLayout.show(cardPanel, "OrderSearchPanel");
        }        

        private void showHospitalManagementPanel() {
            if (!panelMap.containsKey("HospitalManagementPanel")) {
                JPanel hospitalManagementPanel = createHospitalManagementPanel();
                panelMap.put("HospitalManagementPanel", hospitalManagementPanel);
                cardPanel.add(hospitalManagementPanel, "HospitalManagementPanel");
            }
            cardLayout.show(cardPanel, "HospitalManagementPanel");
        }
    
        private void showUserManagementPanel() {
            if (!panelMap.containsKey("UserManagementPanel")) {
                JPanel userManagementPanel = createUserManagementPanel();
                panelMap.put("UserManagementPanel", userManagementPanel);
                cardPanel.add(userManagementPanel, "UserManagementPanel");
            }
            cardLayout.show(cardPanel, "UserManagementPanel");
        }
    
        private void showSupplierManagementPanel() {
            if (!panelMap.containsKey("SupplierManagementPanel")) {
                JPanel supplierManagementPanel = createSupplierManagementPanel();
                panelMap.put("SupplierManagementPanel", supplierManagementPanel);
                cardPanel.add(supplierManagementPanel, "SupplierManagementPanel");
            }
            cardLayout.show(cardPanel, "SupplierManagementPanel");
        }
    
        private JPanel createHospitalManagementPanel() {
            JPanel hospitalPanel = new JPanel(new BorderLayout());
            JPanel buttonPanel = new JPanel(new GridLayout(5, 1));
    
            JButton addHospitalButton = new JButton("Add Hospital");
            JButton modifyHospitalButton = new JButton("Modify Hospital");
            JButton searchHospitalButton = new JButton("Search Hospital");
            JButton deleteHospitalButton = new JButton("Delete Hospital");
            JButton backButton = new JButton("Back");
    
            addHospitalButton.addActionListener(e -> showAddHospitalPanel());
            modifyHospitalButton.addActionListener(e -> showModifySearchDeleteHospitalPanel("Modify"));
            searchHospitalButton.addActionListener(e -> showModifySearchDeleteHospitalPanel("Search"));
            deleteHospitalButton.addActionListener(e -> showModifySearchDeleteHospitalPanel("Delete"));
            backButton.addActionListener(e -> cardLayout.show(cardPanel, "AdminScreen"));
    
            buttonPanel.add(addHospitalButton);
            buttonPanel.add(modifyHospitalButton);
            buttonPanel.add(searchHospitalButton);
            buttonPanel.add(deleteHospitalButton);
            buttonPanel.add(backButton);
    
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
            backButton.addActionListener(e -> cardLayout.show(cardPanel, "HospitalManagementPanel"));
        
            JPanel container = new JPanel(new BorderLayout());
            container.add(addHospitalPanel, BorderLayout.CENTER);
        
            if (!panelMap.containsKey("AddHospitalPanel")) {
                panelMap.put("AddHospitalPanel", container);
                cardPanel.add(container, "AddHospitalPanel");
            }
            cardLayout.show(cardPanel, "AddHospitalPanel");
        }
        
        private void showModifySearchDeleteHospitalPanel(String action) {
            JPanel panel = new JPanel(new GridLayout(6, 2));
            panel.add(new JLabel("Hospital ID:"));
            JTextField idField = new JTextField();
            panel.add(idField);
        
            if (action.equals("Search")) {
                JButton searchButton = new JButton("Search");
                panel.add(searchButton);
        
                searchButton.addActionListener(e -> {
                    String id = idField.getText().trim();
                    if (id.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Hospital ID must be provided.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    searchHospital(id);
                });
            } else if (action.equals("Delete")) {
                JButton deleteButton = new JButton("Delete");
                panel.add(deleteButton);
                
                deleteButton.addActionListener(e -> {
                    String id = idField.getText().trim();
                    if (id.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Hospital ID must be provided.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    deleteHospital(id);
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
                    String id = idField.getText().trim();
                    String name = nameField.getText().trim();
                    String address = addressField.getText().trim();
                    String phone = phoneField.getText().trim();
        
                    if (id.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    modifyHospital(id, name, address, phone);  // Pass trimmed values
                });
            }
        
            JButton backButton = new JButton("Back");
            panel.add(backButton);
            backButton.addActionListener(e -> cardLayout.show(cardPanel, "HospitalManagementPanel"));
        
            JPanel container = new JPanel(new BorderLayout());
            container.add(panel, BorderLayout.CENTER);
        
            if (!panelMap.containsKey(action + "HospitalPanel")) {
                panelMap.put(action + "HospitalPanel", container);
                cardPanel.add(container, action + "HospitalPanel");
            }
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
    
            if (!panelMap.containsKey("AddUserPanel")) {
                panelMap.put("AddUserPanel", container);
                cardPanel.add(container, "AddUserPanel");
            }
            cardLayout.show(cardPanel, "AddUserPanel");
        }
    
        private void showModifySearchDeleteUserPanel(String action) {
            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.add(new JLabel("UserID:"));
            JTextField userIDField = new JTextField();
            panel.add(userIDField);
            
            // For action types
            if (action.equals("Delete")) {
                panel.add(new JLabel("Password:"));
                JPasswordField passwordField = new JPasswordField();
                panel.add(passwordField);
            } else if (action.equals("Search")) {
                JButton searchButton = new JButton("Search");
                panel.add(searchButton);
                
                searchButton.addActionListener(e -> {
                    String id = userIDField.getText();
                    searchUser(id); // Call searchUser method
                });
            } else { // Modify action
                panel.add(new JLabel("Name:"));
                JTextField nameField = new JTextField();
                panel.add(nameField);
                
                panel.add(new JLabel("Password:"));
                JTextField passwordField = new JTextField();
                panel.add(passwordField);
                
                panel.add(new JLabel("UserType (admin/staff):"));
                JTextField userTypeField = new JTextField();
                panel.add(userTypeField);
            }
        
            // Only add the action button if it's not a search action
            if (!action.equals("Search")) {
                JButton actionButton = new JButton(action);
                panel.add(actionButton);
                
                actionButton.addActionListener(e -> {
                    String id = userIDField.getText();
                    
                    if (action.equals("Delete")) {
                        String password = new String(((JPasswordField) panel.getComponent(3)).getPassword());
                        if (id.isEmpty() || password.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "UserID and Password must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        deleteUser(id, password);
                    } else if (action.equals("Modify")) {
                        String name = ((JTextField) panel.getComponent(2)).getText();
                        String password = ((JTextField) panel.getComponent(4)).getText();
                        String userType = ((JTextField) panel.getComponent(6)).getText();
                        
                        if (id.isEmpty() || name.isEmpty() || password.isEmpty() || userType.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        modifyUser(id, name, password, userType);
                    }
                });
            }
        
            JButton backButton = new JButton("Back");
            panel.add(backButton);
            
            backButton.addActionListener(e -> cardLayout.show(cardPanel, "UserManagementPanel"));
            
            JPanel container = new JPanel(new BorderLayout());
            container.add(panel, BorderLayout.CENTER);
            
            if (!panelMap.containsKey(action + "UserPanel")) {
                panelMap.put(action + "UserPanel", container);
                cardPanel.add(container, action + "UserPanel");
            }
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
    
            if (!panelMap.containsKey("AddSupplierPanel")) {
                panelMap.put("AddSupplierPanel", container);
                cardPanel.add(container, "AddSupplierPanel");
            }
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
                
                searchButton.addActionListener(e -> {
                    String supplierID = supplierIDField.getText().trim();
                    if (supplierID.isEmpty()) {
                        showErrorDialog("Supplier ID cannot be empty.");
                        return;
                    }
                    try {
                        String supplierDetails = Suppliers.searchSupplier(supplierID);
                        displaySearchSupplierWindow(supplierID, supplierDetails);
                    } catch (IllegalArgumentException ex) {
                        showErrorDialog("Error: " + ex.getMessage());
                    } catch (IOException ex) {
                        showErrorDialog("Error searching for supplier: " + ex.getMessage());
                    }
                });
            } else if (action.equals("Delete")) {
                JButton deleteButton = new JButton("Delete");
                panel.add(deleteButton);
                
                deleteButton.addActionListener(e -> {
                    String id = supplierIDField.getText().trim();
                    if (id.isEmpty()) {
                        showErrorDialog("Supplier ID cannot be empty.");
                        return;
                    }
        
                    try {
                        Suppliers.deleteSupplier(id);
                        showConfirmationDialog("Supplier deleted successfully: " + id);
                    } catch (IllegalArgumentException ex) {
                        showErrorDialog("Error: " + ex.getMessage());
                    } catch (IOException ex) {
                        showErrorDialog("Error: " + ex.getMessage());
                    }
                });
            } else { // Handle the Modify action
                panel.add(new JLabel("Name:"));
                JTextField supplierNameField = new JTextField();
                panel.add(supplierNameField);
                panel.add(new JLabel("Address:"));
                JTextField supplierAddressField = new JTextField();
                panel.add(supplierAddressField);
                panel.add(new JLabel("Phone:"));
                JTextField supplierPhoneField = new JTextField();
                panel.add(supplierPhoneField);
                
                JButton modifyButton = new JButton("Modify");
                panel.add(modifyButton);
                
                modifyButton.addActionListener(e -> {
                    String id = supplierIDField.getText().trim();
                    String name = supplierNameField.getText().trim();
                    String address = supplierAddressField.getText().trim();
                    String phone = supplierPhoneField.getText().trim();
                    
                    if (id.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                        showErrorDialog("All fields must be filled.");
                        return;
                    }
        
                    try {
                        Suppliers.modifySupplier(id, name, phone, address);
                        showConfirmationDialog("Supplier modified successfully: " + id);
                    } catch (IllegalArgumentException ex) {
                        showErrorDialog("Error: " + ex.getMessage());
                    } catch (IOException ex) {
                        showErrorDialog("Error: " + ex.getMessage());
                    }
                });
            }
            
            JButton backButton = new JButton("Back");
            panel.add(backButton);
            
            backButton.addActionListener(e -> cardLayout.show(cardPanel, "SupplierManagementPanel"));
            
            JPanel container = new JPanel(new BorderLayout());
            container.add(panel, BorderLayout.CENTER);
            
            if (!panelMap.containsKey(action + "SupplierPanel")) {
                panelMap.put(action + "SupplierPanel", container);
                cardPanel.add(container, action + "SupplierPanel");
            }
            cardLayout.show(cardPanel, action + "SupplierPanel");
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
        
        

    private void logout() {
        this.dispose(); // Close the current admin menu window
        new UserManagementGUI().setVisible(true); // Show the login screen again
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
            Userstuff.modifyUser(id, name, password, userType);
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
            String details = Userstuff.searchUser(userID);
            if (details == null || details.isEmpty()) {
                showErrorDialog("User not found.");
            } else {
                displaySearchUserWindow(userID, details); // method to display user details
            }
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
    

    public void deleteUser(String userID, String password) {
        if (userID.isEmpty()) {
            showErrorDialog("User ID cannot be empty.");
            return;
        }
    
        // Get the current user's ID from Userstuff
        String currentUserID = Userstuff.getCurrentUserID(); 
    
        // Debug output for verification
        System.out.println("Current User ID: " + currentUserID);
        System.out.println("User ID to delete: " + userID);
    
        // Check if the user is trying to delete their own account
        if (userID.trim().equals(currentUserID)) { // Using trim to avoid whitespace issues
            showErrorDialog("You cannot delete your own account.");
            return;
        }
    
        try {
            Userstuff.deleteUser(userID, password); // Call the Userstuff deleteUser method
            showConfirmationDialog("User deleted successfully: " + userID);
        } catch (IllegalArgumentException e) {
            showErrorDialog("Invalid User ID or Password: " + e.getMessage());
        } catch (IOException e) {
            showErrorDialog("Error deleting user: " + e.getMessage());
        } catch (Exception e) {
            showErrorDialog("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Call this method when the user successfully logs in
    public void onUserLogin(String userID) {
        Userstuff.setCurrentUserID(userID); // Set the current user ID in Userstuff
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


    private void addHospital(JTextField idField, JTextField nameField, JTextField addressField, JTextField phoneField) {
        String id = idField.getText();
        String name = nameField.getText();
        String address = addressField.getText();
        String contact = phoneField.getText();
    
        if (id.isEmpty() || name.isEmpty() || address.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try {
            Hospitals.addHospital(id, name, address, contact);
            JOptionPane.showMessageDialog(this, "Hospital added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error adding hospital: " + e.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchHospital(String hospitalID) {
        try {
            String result = Hospitals.searchHospital(hospitalID);
            JOptionPane.showMessageDialog(this, result, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteHospital(String hospitalID) {
        try {
            Hospitals.deleteHospital(hospitalID);
            JOptionPane.showMessageDialog(this, "Hospital deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifyHospital(String hospitalID, String name, String address, String contact) {
        try {
            Hospitals.modifyHospital(hospitalID, name, address, contact);
            JOptionPane.showMessageDialog(this, "Hospital modified successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showConfirmationDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
    }
}