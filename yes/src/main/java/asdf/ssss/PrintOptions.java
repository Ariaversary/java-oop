package asdf.ssss;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrintOptions extends JFrame {
    private InventoryManagement inventoryManagement;

    public PrintOptions(JFrame parentFrame, InventoryManagement inventoryManagement) {
        this.inventoryManagement = inventoryManagement;
        setTitle("Print Options");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);
        initializeComponents();
        pack(); // Size the frame to fit the preferred sizes of its components
        setVisible(true); // Set visibility after components are initialized
    }

    private void initializeComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
    
        JButton printPPEButton = new JButton("Print PPE Inventory");
        printPPEButton.addActionListener(e -> printInventoryQuantities());
    
        JButton printSearchButton = new JButton("Print Search Orders");
        printSearchButton.addActionListener(e -> printSearchOrders());
    
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
    
        panel.add(printPPEButton);
        panel.add(printSearchButton);
        panel.add(backButton);
    
        add(panel);
    }

    private void printSearchOrders() {
        List<Order> orders = inventoryManagement.fetchOrders();
        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No orders to print.", "Print", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String pdfPath = "search_orders.pdf";
            PdfWriter writer = new PdfWriter(pdfPath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
    
            document.add(new Paragraph("Search Orders Report"));
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    
            for (Order order : orders) {
                document.add(new Paragraph(String.format("Order ID: %s", order.getId())));
                document.add(new Paragraph(String.format("Hospital ID: %s", order.getHospitalID())));
                document.add(new Paragraph(String.format("Order Date: %s", order.getOrderDate().toString())));
                document.add(new Paragraph(String.format("Item Code: %s", order.getItemCode())));
                document.add(new Paragraph(String.format("Supplier ID: %s", order.getSupplierID())));
                document.add(new Paragraph(String.format("Quantity: %d", order.getQuantity())));
                document.add(new Paragraph(String.format("Type: %s", order.getType())));
                document.add(new Paragraph("----------"));
            }
    
            document.close();
            JOptionPane.showMessageDialog(this, "Search Orders printed to " + pdfPath, "Print", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void printInventoryQuantities() {
        List<InventoryItem> inventoryItems = inventoryManagement.fetchInventoryItems();
        if (inventoryItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No inventory items to print.", "Print", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String pdfPath = "inventory_quantities.pdf";
            PdfWriter writer = new PdfWriter(pdfPath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Inventory Quantities Report"));
            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

            for (InventoryItem item : inventoryItems) {
                document.add(new Paragraph(String.format("Item Code: %s", item.getCode())));
                document.add(new Paragraph(String.format("Description: %s", item.getDescription())));
                document.add(new Paragraph(String.format("Supplier: %s", item.getSupplier())));
                document.add(new Paragraph(String.format("Quantity Available: %d", item.getQuantity())));
                document.add(new Paragraph("----------"));
            }

            document.close();
            JOptionPane.showMessageDialog(this, "Inventory Quantities printed to " + pdfPath, "Print", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Suppliers suppliers = new Suppliers();
            Hospitals hospitals = new Hospitals();
            InventoryManagement inventoryManagement = new InventoryManagement(suppliers, hospitals);
            new PrintOptions(null, inventoryManagement);
        });
    }
}    