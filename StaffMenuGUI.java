import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class StaffMenuGUI extends JFrame {
    private JPanel mainPanel;
    private InventoryManagement inventoryManagement;

    public StaffMenuGUI() {
        setTitle("Staff Menu");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize Inventory Management
        Suppliers suppliers = new Suppliers();
        Hospitals hospitals = new Hospitals();
        inventoryManagement = new InventoryManagement(suppliers, hospitals);

        mainPanel = new JPanel(new BorderLayout());
        createStaffMenu();
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    private void createStaffMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(7, 1));

        JButton viewPPEButton = new JButton("View PPE Inventory");
        JButton viewHospitalButton = new JButton("View Hospital Info");
        JButton addItemsButton = new JButton("Add Items from Supplier");
        JButton distributeItemsButton = new JButton("Distribute Items to Hospital");
        JButton exitButton = new JButton("Exit");
        JButton logoutButton = new JButton("Logout");

        viewPPEButton.addActionListener(e -> showPPEInventoryPanel());
        viewHospitalButton.addActionListener(e -> showHospitalInfoPanel());
        addItemsButton.addActionListener(e -> showAddItemsPanel());
        distributeItemsButton.addActionListener(e -> showDistributeItemsPanel());
        exitButton.addActionListener(e -> System.exit(0));
        logoutButton.addActionListener(e -> logout());

        menuPanel.add(viewPPEButton);
        menuPanel.add(viewHospitalButton);
        menuPanel.add(addItemsButton);
        menuPanel.add(distributeItemsButton);
        menuPanel.add(exitButton);
        menuPanel.add(logoutButton);

        mainPanel.add(menuPanel, BorderLayout.CENTER);
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
        backButton.addActionListener(e -> ((JDialog) SwingUtilities.getWindowAncestor(panel)).dispose());
        panel.add(backButton, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "PPE Inventory", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private Object[][] getPPEData() {
        // Implement this method based on your PPE data source
        return new Object[0][0];
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
        backButton.addActionListener(e -> ((JDialog) SwingUtilities.getWindowAncestor(panel)).dispose());
        panel.add(backButton, BorderLayout.SOUTH);
    
        JDialog dialog = new JDialog(this, "Hospital Information", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);
        dialog.setVisible(true);
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
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String itemCode = itemCodeField.getText();
                    String supplierID = supplierIDField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());

                    inventoryManagement.addItemFromSupplier(itemCode, supplierID, quantity);
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid quantity. Please enter a number.");
                }
            }
        });

        panel.add(addButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> ((JDialog) SwingUtilities.getWindowAncestor(panel)).dispose());
        panel.add(backButton);

        JDialog dialog = new JDialog(this, "Add Items from Supplier", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);
        dialog.setVisible(true);
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
        distributeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String itemCode = itemCodeField.getText();
                    String hospitalID = hospitalIDField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());

                    inventoryManagement.distributeItemToHospital(itemCode, hospitalID, quantity);
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid quantity. Please enter a number.");
                }
            }
        });

        panel.add(distributeButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> ((JDialog) SwingUtilities.getWindowAncestor(panel)).dispose());
        panel.add(backButton);

        JDialog dialog = new JDialog(this, "Distribute Items to Hospital", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void logout() {
        this.dispose();
        new UserManagementGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffMenuGUI().setVisible(true));
    }
}
