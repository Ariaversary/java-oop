import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Hospitals {
    private static final String HOSPITAL_FILE_NAME = "hospitals.txt";
    private static final int MAX_HOSPITALS = 10;
    private static final Set<String> USED_IDS = new HashSet<>();

    static {
        initializeHospitalFile();
    }

    public static void initializeHospitalFile() {
        File hospitalFile = new File(HOSPITAL_FILE_NAME);
        if (!hospitalFile.exists()) {
            try {
                hospitalFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating hospital file: " + e.getMessage());
            }
        } else {
            loadUsedIDs();
        }
    }

    private static void loadUsedIDs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                if (hospitalData.length > 0) {
                    USED_IDS.add(hospitalData[0]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading hospital IDs: " + e.getMessage());
        }
    }

    public static void addHospital(String name, String id, String address, String phone, String email) throws IOException {
        validateId(id);

        if (USED_IDS.size() >= MAX_HOSPITALS) {
            throw new IllegalArgumentException("Cannot add more than 10 hospitals.");
        }

        if (USED_IDS.contains(id)) {
            throw new IllegalArgumentException("Hospital ID already exists.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HOSPITAL_FILE_NAME, true))) {
            writer.write(String.join(",", id, name, address, phone, email));
            writer.newLine();
        }

        USED_IDS.add(id);
    }

    public static void modifyHospital(String idToModify, String newName, String newAddress, String newPhone, String newEmail) throws IOException {
        validateId(idToModify);

        File tempFile = new File("temp_hospitals.txt");
        boolean hospitalFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                if (hospitalData[0].equals(idToModify)) {
                    hospitalFound = true;
                    writer.write(String.join(",", idToModify, newName, newAddress, newPhone, newEmail));
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }

        if (!hospitalFound) {
            throw new IllegalArgumentException("Hospital ID not found.");
        }

        if (!new File(HOSPITAL_FILE_NAME).delete() || !tempFile.renameTo(new File(HOSPITAL_FILE_NAME))) {
            throw new IOException("Could not update hospital file.");
        }
    }

    public static String searchHospital(String idToSearch) throws IOException {
        validateId(idToSearch);

        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                if (hospitalData[0].equals(idToSearch)) {
                    return String.format("ID: %s\nName: %s\nAddress: %s\nPhone: %s\nEmail: %s",
                            hospitalData[0], hospitalData[1], hospitalData[2], hospitalData[3], hospitalData[4]);
                }
            }
        }
        throw new IllegalArgumentException("Hospital ID not found.");
    }

    public static void deleteHospital(String idToDelete) throws IOException {
        validateId(idToDelete);

        File tempFile = new File("temp_hospitals.txt");
        boolean hospitalFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(HOSPITAL_FILE_NAME));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] hospitalData = line.split(",");
                if (hospitalData[0].equals(idToDelete)) {
                    hospitalFound = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        if (!hospitalFound) {
            throw new IllegalArgumentException("Hospital ID not found.");
        }

        if (!new File(HOSPITAL_FILE_NAME).delete() || !tempFile.renameTo(new File(HOSPITAL_FILE_NAME))) {
            throw new IOException("Could not update hospital file.");
        }

        USED_IDS.remove(idToDelete);
    }

    private static void validateId(String id) {
        if (!id.matches("\\d+")) {
            throw new IllegalArgumentException("Hospital ID must be numeric.");
        }

        int idValue = Integer.parseInt(id);
        if (idValue < 1 || idValue > MAX_HOSPITALS) {
            throw new IllegalArgumentException("Hospital ID must be between 1 and " + MAX_HOSPITALS + ".");
        }
    }
}
