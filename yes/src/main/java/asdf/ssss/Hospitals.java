package asdf.ssss;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Hospitals {
    private static final String HOSPITAL_FILE_NAME = "hospitals.txt";
    private static final Set<String> USED_IDS = new HashSet<>();
    private static final int MAX_HOSPITALS = 10;

    public static void init() {
        initializeHospitalFile(); // Ensure the file is initialized
        loadHospitalIDs(); // Load the IDs immediately
    }

    public static void initializeHospitalFile() {
        File hospitalFile = new File(HOSPITAL_FILE_NAME);
        if (!hospitalFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(hospitalFile))) {
                for (int i = 1; i <= MAX_HOSPITALS; i++) {
                    writer.write(i + "|Hospital " + i + "|Address " + i + "|Contact " + i);
                    writer.newLine();
                }
            } catch (IOException e) {
                showErrorDialog("Error initializing hospital file: " + e.getMessage());
            }
        }
        loadHospitalIDs(); // Load IDs after file is initialized
    }

    private static void loadHospitalIDs() {
        USED_IDS.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split("\\|");
                if (hospitalData.length > 0) {
                    String id = hospitalData[0].trim();
                    USED_IDS.add(id);
                }
            }
        } catch (IOException e) {
            showErrorDialog("Error loading hospital IDs: " + e.getMessage());
        }
    }

    public static boolean isHospitalIDInUse(String hospitalID) {
        return USED_IDS.contains(hospitalID);
    }

    public static List<String[]> getAllHospitals() throws IOException {
        List<String[]> hospitalList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split("\\|");
                hospitalList.add(hospitalData);
            }
        } catch (IOException e) {
            showErrorDialog("Error retrieving hospitals: " + e.getMessage());
            throw e; // Rethrow to indicate failure
        }
        return hospitalList;
    }

    public static void modifyHospital(String hospitalID, String newName, String newAddress, String newContact) throws IOException {
        Hospitals.loadHospitalIDs();
        if (!isHospitalIDInUse(hospitalID)) {
            throw new IllegalArgumentException("Hospital ID " + hospitalID + " does not exist.");
        }

        File inputFile = new File(HOSPITAL_FILE_NAME);
        File tempFile = new File("temp_hospitals.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean hospitalFound = false;

            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split("\\|");
                if (hospitalData[0].equals(hospitalID)) {
                    hospitalFound = true;
                    // Write updated hospital information
                    writer.write(hospitalID + "|" + newName + "|" + newAddress + "|" + newContact);
                } else {
                    writer.write(line); // Write existing line if no match
                }
                writer.newLine();
            }

            if (!hospitalFound) {
                throw new IOException("Hospital ID " + hospitalID + " not found in the file.");
            }
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Could not update the hospital file.");
        }

        // Update USED_IDS
        loadHospitalIDs(); // Reload IDs
    }

    public static String searchHospital(String hospitalID) throws IOException {
        Hospitals.loadHospitalIDs();
        if (!isHospitalIDInUse(hospitalID)) {
            throw new IllegalArgumentException("Invalid Hospital ID: " + hospitalID);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split("\\|");
                if (hospitalData[0].equals(hospitalID)) {
                    return String.format("HospitalID: %s\nName: %s\nAddress: %s\nContact: %s\n",
                            hospitalData[0], hospitalData[1], hospitalData[2], hospitalData[3]);
                }
            }
        }

        throw new IOException("Hospital with ID " + hospitalID + " not found.");
    }

    public static void addHospital(String hospitalID, String name, String address, String contact) throws IOException {
        if (!hospitalID.matches("[1-9]|10")) {
            throw new IllegalArgumentException("Hospital ID must be between 1 and 10.");
        }

        // Load IDs to ensure we have the latest
        loadHospitalIDs();

        if (isHospitalIDInUse(hospitalID)) {
            throw new IllegalArgumentException("Hospital ID " + hospitalID + " is already in use.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HOSPITAL_FILE_NAME, true))) {
            writer.write(hospitalID + "|" + name + "|" + address + "|" + contact);
            writer.newLine();
        }

        USED_IDS.add(hospitalID); // Update in-memory set
    }

    public static void deleteHospital(String hospitalID) throws IOException {
        Hospitals.loadHospitalIDs();
        if (!isHospitalIDInUse(hospitalID)) {
            throw new IllegalArgumentException("Hospital ID " + hospitalID + " does not exist.");
        }

        File inputFile = new File(HOSPITAL_FILE_NAME);
        File tempFile = new File("temp_hospitals.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean hospitalFound = false;

            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split("\\|");
                if (!hospitalData[0].equals(hospitalID)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    hospitalFound = true;
                }
            }

            if (!hospitalFound) {
                throw new IOException("Hospital ID " + hospitalID + " not found.");
            }
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Could not update the hospital file.");
        }

        USED_IDS.remove(hospitalID); // Update USED_IDS
    }

    private static void showErrorDialog(String message) {
        System.err.println(message);
    }
}
