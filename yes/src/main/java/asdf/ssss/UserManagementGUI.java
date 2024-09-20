package asdf.ssss;

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
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private AdminMenuGUI adminMenuGUI;
    private StaffMenuGUI staffMenuGUI;

    public UserManagementGUI() {
        setTitle("User Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cardPanel.getLayout();
        getContentPane().add(cardPanel);

        // Initialize login screen
        initializeLoginScreen();

        // Initialize files
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

            public String toFileFormat() {
                return String.format("%s,%s,%s,%d", code, description, type, quantity);
            }

            @Override
            public String toString() {
                return toFileFormat();
            }
        }

        public static void createPPEFile() {
            List<PPEItem> ppeItems = new ArrayList<>();
            ppeItems.add(new PPEItem("HC", "Head Cover", "1", 100));
            ppeItems.add(new PPEItem("FS", "Face Shield", "1", 100));
            ppeItems.add(new PPEItem("MS", "Mask", "2", 100));
            ppeItems.add(new PPEItem("GL", "Gloves", "3", 100));
            ppeItems.add(new PPEItem("GW", "Gown", "3", 100));
            ppeItems.add(new PPEItem("SC", "Shoe Covers", "4", 100));

            String filePath = PPE_FILE_NAME;

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

    private void initializeLoginScreen() {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2));
        loginPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin(usernameField.getText(), new String(passwordField.getPassword())));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(loginPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(panel, "LoginScreen");
        cardLayout.show(cardPanel, "LoginScreen");
    }

    private void handleLogin(String userID, String password) {
        String userType = authenticate(userID, password);
        if (userType != null) {
            // Set the current user ID
            Userstuff.setCurrentUserID(userID); // Set the current user ID here

            if (userType.equals("admin")) {
                showAdminScreen();
            } else if (userType.equals("staff")) {
                showStaffScreen();
            }
        } else {
            showErrorDialog("Invalid username or password.");
            initializeLoginScreen(); // Redisplay the login screen for retry
        }
    }

    private String authenticate(String userID, String password) {
        if (userID.equals("admin")) {
            if (password.equals("admin_password")) {
                return "admin";
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\|"); // Changed to split by '|'
                if (userData.length == 5) {
                    String fileUserID = userData[0].trim();
                    String filePassword = userData[2].trim();
                    String fileUserType = userData[3].trim();
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
        if (adminMenuGUI == null) {
            adminMenuGUI = new AdminMenuGUI();
        }

        // Dispose of the current frame and open AdminMenuGUI
        dispose();
        adminMenuGUI.setVisible(true);
    }

    private void showStaffScreen() {
        if (staffMenuGUI == null) {
            staffMenuGUI = new StaffMenuGUI();
        }

        // Dispose of the current frame and open StaffMenuGUI
        dispose();
        staffMenuGUI.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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
        Hospitals.init();
    }
}
