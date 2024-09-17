import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Hospitals {
    private static final String HOSPITAL_FILE_NAME = "hospitals.txt";
    private static final Set<String> USED_IDS = new HashSet<>();
    private static final int MAX_HOSPITALS = 10;

    // Make this method public
    public static void initializeHospitalFile() {
        File hospitalFile = new File(HOSPITAL_FILE_NAME);
        if (!hospitalFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(hospitalFile))) {
                // Adding some test data
                writer.write("1,Hospital A,Address A,Contact A\n");
                writer.write("2,Hospital B,Address B,Contact B\n");
                writer.write("3,Hospital C,Address C,Contact C\n");
                writer.write("4,Hospital D,Address D,Contact D\n");
                writer.write("5,Hospital E,Address E,Contact E\n");
                writer.write("6,Hospital F,Address F,Contact F\n");
                writer.write("7,Hospital G,Address G,Contact G\n");
                writer.write("8,Hospital H,Address H,Contact H\n");
                writer.write("9,Hospital I,Address I,Contact I\n");
                writer.write("10,Hospital J,Address J,Contact J\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadHospitalIDs(); // Load IDs after file is initialized
    }
    
    private static void loadHospitalIDs() {
        USED_IDS.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                if (hospitalData.length > 0) {
                    USED_IDS.add(hospitalData[0]); // Assuming the ID is the first element
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean isHospitalIDInUse(String hospitalID) {
        return USED_IDS.contains(hospitalID);
    }

    public static Set<String> getAvailableHospitalIDs() {
        return new HashSet<>(USED_IDS); // Return a copy of the set for debugging
    }

    public static List<String[]> getAllHospitals() throws IOException {
        List<String[]> hospitalList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                hospitalList.add(hospitalData);
            }
        }
        return hospitalList;
    }

    public static void modifyHospital(String hospitalID, String newName, String newAddress, String newContact) throws IOException {
        if (!isHospitalIDInUse(hospitalID)) {
            throw new IllegalArgumentException("Hospital ID does not exist.");
        }
    
        File inputFile = new File(HOSPITAL_FILE_NAME);
        File tempFile = new File("temp_hospitals.txt");
    
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    
            String line;
            boolean hospitalFound = false;
    
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                if (hospitalData[0].equals(hospitalID)) {
                    hospitalFound = true;
                    // Write updated hospital information
                    writer.write(hospitalID + "," + newName + "," + newAddress + "," + newContact);
                } else {
                    // Write existing line if no match
                    writer.write(line);
                }
                writer.newLine();
            }
    
            if (!hospitalFound) {
                throw new IOException("Hospital not found.");
            }
        }
    
        // Replace original file with updated file
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Could not update the hospital file.");
        }
    }

    public static String searchHospital(String hospitalID) throws IOException {
        if (!isHospitalIDInUse(hospitalID)) {
            throw new IllegalArgumentException("Invalid Hospital ID.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                if (hospitalData[0].equals(hospitalID)) {
                    return String.format("HospitalID: %s\nName: %s\nAddress: %s\nContact: %s\n",
                            hospitalData[0], hospitalData[1], hospitalData[2], hospitalData[3]);
                }
            }
        }

        throw new IOException("Hospital not found.");
    }

    public static void addHospital(String hospitalID, String name, String address, String contact) throws IOException {
        if (USED_IDS.size() >= MAX_HOSPITALS) {
            throw new IllegalArgumentException("Cannot add more than " + MAX_HOSPITALS + " hospitals.");
        }
    
        if (isHospitalIDInUse(hospitalID)) {
            throw new IllegalArgumentException("Hospital ID already in use.");
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HOSPITAL_FILE_NAME, true))) {
            writer.write(hospitalID + "," + name + "," + address + "," + contact);
            writer.newLine();
        }
    
        USED_IDS.add(hospitalID);
    }

    public static void deleteHospital(String hospitalID) throws IOException {
        if (!isHospitalIDInUse(hospitalID)) {
            throw new IllegalArgumentException("Hospital ID does not exist.");
        }

        File inputFile = new File(HOSPITAL_FILE_NAME);
        File tempFile = new File("temp_hospitals.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean hospitalFound = false;

            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                if (!hospitalData[0].equals(hospitalID)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    hospitalFound = true;
                }
            }

            if (!hospitalFound) {
                throw new IOException("Hospital not found.");
            }
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Could not update the hospital file.");
        }

        USED_IDS.remove(hospitalID);
    }
}
