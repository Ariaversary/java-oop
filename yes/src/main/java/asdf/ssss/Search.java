package asdf.ssss;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Search {
    private JFrame resultsFrame;

    private List<PPE.PPEItem> loadPPEItems() {
        List<PPE.PPEItem> ppeItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("ppe.txt"))) {
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

    private List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] transactionData = line.split(",");
                if (transactionData.length == 6) {
                    String itemCode = transactionData[0];
                    String supplierID = transactionData[1];
                    String entityCode = transactionData[2];
                    int quantity = Integer.parseInt(transactionData[3]);
                    String type = transactionData[4];
                    String timestamp = transactionData[5];
                    transactions.add(new Transaction(itemCode, supplierID, entityCode, quantity, type, timestamp));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions data: " + e.getMessage());
        }
        return transactions;
    }

    public void searchOrders(Component parentComponent, String itemCode, String id, String date, String minQuantity, String maxQuantity) {
        List<PPE.PPEItem> ppeItems = loadPPEItems();
        List<Transaction> transactions = loadTransactions();
        
        String[] columnNames = {"Item Code", "Supplier/Hospital ID", "Total Quantity", "Type", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        int minQty = minQuantity.isEmpty() ? 0 : Integer.parseInt(minQuantity);
        int maxQty = maxQuantity.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxQuantity);
        LocalDate filterDate = date.isEmpty() ? null : LocalDate.parse(date, DateTimeFormatter.ISO_DATE);

        Map<String, TransactionSummary> summaryMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String displayID = transaction.getType().equals("Received") ? transaction.getEntityCode() : transaction.getSupplierID();
            
            // Filter criteria
            if ((itemCode.isEmpty() || transaction.getItemCode().equals(itemCode)) &&
                (id.isEmpty() || displayID.equals(id)) &&
                (filterDate == null || LocalDate.parse(transaction.getTimestamp().split(" ")[0], DateTimeFormatter.ISO_DATE).isBefore(filterDate.plusDays(1))) &&
                (transaction.getQuantity() >= minQty && transaction.getQuantity() <= maxQty)) {

                String key = transaction.getItemCode() + "|" + displayID + "|" + transaction.getType();
                summaryMap.putIfAbsent(key, new TransactionSummary(transaction.getItemCode(), displayID, transaction.getType()));
                summaryMap.get(key).addQuantity(transaction.getQuantity());
            }
        }

        for (TransactionSummary summary : summaryMap.values()) {
            Object[] rowData = {
                summary.getItemCode(),
                summary.getDisplayID(),
                summary.getTotalQuantity(),
                summary.getType(),
                summary.getTimestamp()
            };
            tableModel.addRow(rowData);
        }

        JTable resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        resultsTable.setFillsViewportHeight(true);

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            resultsFrame.dispose();
        });

        // Create a panel for the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Display the results panel in the class-level resultsFrame
        resultsFrame = new JFrame("Search Results");
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setSize(800, 400);
        resultsFrame.add(resultsPanel);
        resultsFrame.setLocationRelativeTo(parentComponent);
        resultsFrame.setVisible(true);
    }

    public class Transaction {
        private String itemCode;
        private String supplierID;
        private String entityCode;
        private int quantity;
        private String type;
        private String timestamp;

        public Transaction(String itemCode, String supplierID, String entityCode, int quantity, String type, String timestamp) {
            this.itemCode = itemCode;
            this.supplierID = supplierID;
            this.entityCode = entityCode; // Assume this is the hospital ID for received orders
            this.quantity = quantity;
            this.type = type;
            this.timestamp = timestamp;
        }

        public String getItemCode() {
            return itemCode;
        }

        public String getSupplierID() {
            return supplierID;
        }

        public String getEntityCode() {
            return entityCode;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getType() {
            return type;
        }
    }

    private class TransactionSummary {
        private String itemCode;
        private String displayID;
        private int totalQuantity;
        private String type;
        private String timestamp;

        public TransactionSummary(String itemCode, String displayID, String type) {
            this.itemCode = itemCode;
            this.displayID = displayID;
            this.type = type;
            this.totalQuantity = 0;
            this.timestamp = ""; // Initialize as empty, will set later
        }

        public void addQuantity(int quantity) {
            totalQuantity += quantity;
        }

        public String getItemCode() {
            return itemCode;
        }

        public String getDisplayID() {
            return displayID;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public String getType() {
            return type;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}