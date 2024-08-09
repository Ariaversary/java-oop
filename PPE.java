import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PPE {

    static class PPEItem {
        String itemCode;
        String itemName;
        String supplierID;
        int quantityInStock;

        PPEItem(String itemCode, String itemName, String supplierID, int quantityInStock) {
            this.itemCode = itemCode;
            this.supplierID = supplierID;
            this.quantityInStock = quantityInStock;
            this.itemName = itemName;
        }

        @Override
        public String toString() {
            return itemCode + "," + itemName + "," + supplierID + "," + quantityInStock;
        }
    }

    public static void main(String[] args) {
        // List to hold PPE items
        List<PPEItem> ppeItems = new ArrayList<>();
        
        // Add PPE items with initial data
        ppeItems.add(new PPEItem("HC", "Head Cover", "1", 100));
        ppeItems.add(new PPEItem("FS", "Face Should", "1", 100));
        ppeItems.add(new PPEItem("MS", "Mask", "2", 100));
        ppeItems.add(new PPEItem("GL", "Gloves", "3", 100));
        ppeItems.add(new PPEItem("GW", "Gown", "3", 100));
        ppeItems.add(new PPEItem("SC", "Shoe Covers", "4", 100));

        // File path
        String filePath = "ppe.txt";

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
