package asdf.ssss;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PPE {
    public static final String PPE_FILE_NAME = "ppe.txt";

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

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public String getType() {
            return type;
        }

        public int getQuantity() {
            return quantity;
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
        File ppeFile = new File(PPE_FILE_NAME);

 
        if (ppeFile.exists()) {
            System.out.println("PPE file already exists.");
            return;
        }


        List<PPEItem> ppeItems = new ArrayList<>();

        // Add PPE items with initial data, ensuring correct description and supplier ID
        ppeItems.add(new PPEItem("HC", "Head Cover", "1", 100));
        ppeItems.add(new PPEItem("FS", "Face Shield", "1", 100));
        ppeItems.add(new PPEItem("MS", "Mask", "2", 100));
        ppeItems.add(new PPEItem("GL", "Gloves", "3", 100));
        ppeItems.add(new PPEItem("GW", "Gown", "3", 100));
        ppeItems.add(new PPEItem("SC", "Shoe Covers", "4", 100));

        // Write the PPE items to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ppeFile))) {
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