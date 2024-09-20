package asdf.ssss;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffMenuGUI extends JFrame {
    private JPanel mainPanel;
    private InventoryManagement inventoryManagement;
    private CardLayout cardLayout;
    

    public StaffMenuGUI() {
        setTitle("Staff Menu");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize Inventory Management
        Suppliers suppliers = new Suppliers();
        Hospitals hospitals = new Hospitals();
        inventoryManagement = new InventoryManagement(suppliers, hospitals);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        createStaffMenu();
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    private void createStaffMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(9, 1));
    
        JButton viewPPEButton = new JButton("View PPE Inventory");
        JButton viewHospitalButton = new JButton("View Hospital Info");
        JButton addItemsButton = new JButton("Add Items from Supplier");
        JButton distributeItemsButton = new JButton("Distribute Items to Hospital");
        JButton searchOrdersButton = new JButton("Search Orders");
        JButton printOptionsButton = new JButton("Print Options");
        JButton exitButton = new JButton("Exit");
        JButton logoutButton = new JButton("Logout");
    
        //buttons
        viewPPEButton.addActionListener(e -> showPPEInventoryPanel());
        viewHospitalButton.addActionListener(e -> showHospitalInfoPanel());
        addItemsButton.addActionListener(e -> showAddItemsPanel());
        distributeItemsButton.addActionListener(e -> showDistributeItemsPanel());
        searchOrdersButton.addActionListener(e -> showOrderSearchPanel());
        
        //Print Options button
        printOptionsButton.addActionListener(e -> openPrintOptions());
        
        //exit and logout buttons
        exitButton.addActionListener(e -> System.exit(0));
        logoutButton.addActionListener(e -> logout());
    
        //panel
        menuPanel.add(viewPPEButton);
        menuPanel.add(viewHospitalButton);
        menuPanel.add(addItemsButton);
        menuPanel.add(distributeItemsButton);
        menuPanel.add(searchOrdersButton);
        menuPanel.add(printOptionsButton);
        menuPanel.add(exitButton);
        menuPanel.add(logoutButton);
    
        mainPanel.add(menuPanel, "MenuPanel");
        cardLayout.show(mainPanel, "MenuPanel");
    }
    
    private void openPrintOptions() {
        new PrintOptions(this, inventoryManagement).setVisible(true);
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

        searchButton.addActionListener(e -> {
            String itemCode = itemCodeField.getText();
            String id = idField.getText();
            String date = dateField.getText();
            String minQuantity = minQuantityField.getText();
            String maxQuantity = maxQuantityField.getText();
            searchOrders(itemCode, id, date, minQuantity, maxQuantity);
        });

        JButton backButton = new JButton("Back");
        searchPanel.add(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MenuPanel"));

        mainPanel.add(searchPanel, "OrderSearchPanel");
        cardLayout.show(mainPanel, "OrderSearchPanel");
    }

    private void searchOrders(String itemCode, String id, String date, String minQuantity, String maxQuantity) {
        //search instance
        Search search = new Search();
        search.searchOrders(this, itemCode, id, date, minQuantity, maxQuantity);
    }

    private void showPPEInventoryPanel() {
        String[] columnNames = {"Code", "Description", "Type", "Quantity"};
        Object[][] data = getPPEData();

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("PPE Inventory"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MenuPanel"));
        panel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "PPEInventoryPanel");
        cardLayout.show(mainPanel, "PPEInventoryPanel");
    }

    private Object[][] getPPEData() {
        List<PPE.PPEItem> ppeItems = loadPPEItems();
        Object[][] data = new Object[ppeItems.size()][4];

        for (int i = 0; i < ppeItems.size(); i++) {
            PPE.PPEItem item = ppeItems.get(i);
            data[i][0] = item.getCode();
            data[i][1] = item.getDescription();
            data[i][2] = item.getType();
            data[i][3] = item.getQuantity();
        }

        return data;
    }

    private List<PPE.PPEItem> loadPPEItems() {
        List<PPE.PPEItem> ppeItems = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PPE.PPE_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] itemData = line.split(",");
                if (itemData.length == 4) {
                    String code = itemData[0];
                    String description = itemData[1];
                    String type = itemData[2];
                    int quantity = Integer.parseInt(itemData[3]);
                    ppeItems.add(new PPE.PPEItem(code, description, type, quantity));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading PPE data: " + e.getMessage());
        }

        return ppeItems;
    }

    private void showHospitalInfoPanel() {
        String[] columnNames = {"Hospital ID", "Name", "Address", "Contact"};
        Object[][] data = getHospitalData();

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Hospital Information"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MenuPanel"));
        panel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(panel, "HospitalInfoPanel");
        cardLayout.show(mainPanel, "HospitalInfoPanel");
    }

    private Object[][] getHospitalData() {
        List<String[]> hospitals = loadHospitalInfo();
        Object[][] data = new Object[hospitals.size()][4];
        for (int i = 0; i < hospitals.size(); i++) {
            String[] hospital = hospitals.get(i);
            data[i][0] = hospital[0]; // ID
            data[i][1] = hospital[1]; // Name
            data[i][2] = hospital[2]; // Address
            data[i][3] = hospital[3]; // Contact
        }
        return data;
    }

    private List<String[]> loadHospitalInfo() {
        List<String[]> hospitals = new ArrayList<>();
        try {
            hospitals = Hospitals.getAllHospitals();
        } catch (IOException e) {
            showErrorDialog("Error loading hospital data: " + e.getMessage());
        }
        return hospitals;
    }

    private void showAddItemsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
    
        JTextField itemCodeField = new JTextField();
        JTextField supplierIDField = new JTextField();
        JTextField quantityField = new JTextField();
    
        panel.add(new JLabel("Item Code:"));
        panel.add(itemCodeField);
        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierIDField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
    
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            try {
                String itemCode = itemCodeField.getText().trim();
                String supplierID = supplierIDField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
    
                // Validate inputs
                if (itemCode.isEmpty() || supplierID.isEmpty() || quantity <= 0) {
                    showErrorDialog("All fields must be filled, and quantity must be a positive integer.");
                    return; // Exit the listener if validation fails
                }
    
                inventoryManagement.addItemFromSupplier(itemCode, supplierID, quantity);
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid quantity. Please enter a positive integer.");
            }
        });
    
        panel.add(addButton);
    
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MenuPanel"));
        panel.add(backButton);
    
        mainPanel.add(panel, "AddItemsPanel");
        cardLayout.show(mainPanel, "AddItemsPanel");
    }
    
    
    private void showDistributeItemsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
    
        JTextField itemCodeField = new JTextField();
        JTextField hospitalIDField = new JTextField();
        JTextField quantityField = new JTextField();
    
        panel.add(new JLabel("Item Code:"));
        panel.add(itemCodeField);
        panel.add(new JLabel("Hospital ID:"));
        panel.add(hospitalIDField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
    
        JButton distributeButton = new JButton("Distribute");
        distributeButton.addActionListener(e -> {
            try {
                String itemCode = itemCodeField.getText().trim();
                String hospitalID = hospitalIDField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
    
                // Validate inputs
                if (itemCode.isEmpty() || hospitalID.isEmpty() || quantity <= 0) {
                    showErrorDialog("All fields must be filled, and quantity must be a positive integer.");
                    return; // Exit the listener if validation fails
                }
    
                inventoryManagement.distributeItemToHospital(itemCode, hospitalID, quantity);
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid quantity. Please enter a positive integer.");
            }
        });
    
        panel.add(distributeButton);
    
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MenuPanel"));
        panel.add(backButton);
    
        mainPanel.add(panel, "DistributeItemsPanel");
        cardLayout.show(mainPanel, "DistributeItemsPanel");
    }
    
    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void logout() {
        this.dispose();
        new UserManagementGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffMenuGUI().setVisible(true));
    }
}
