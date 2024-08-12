import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Userstuff {
    private static final String USER_FILE_NAME = "users.txt";

    public static void addUser(String userID, String name, String password, String userType) throws IOException {
        if (!isUserIDUnique(userID)) {
            throw new IllegalArgumentException("UserID already exists.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_NAME, true))) {
            String dateOfRegistration = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            writer.write(String.format("%s,%s,%s,%s,%s", userID, name, password, userType, dateOfRegistration));
            writer.newLine();
        }
    }

    public static void modifyUser(String userID, String newName, String newPassword, String newUserType) throws IOException {
        File inputFile = new File(USER_FILE_NAME);
        File tempFile = new File("temp_users.txt");
        
        boolean success = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    
            String line;
            boolean userFound = false;
    
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    writer.write(userID + "," + newName + "," + newPassword + "," + newUserType + "," + userData[4]);
                    writer.newLine();
                    userFound = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
    
            if (!userFound) {
                throw new IllegalArgumentException("UserID not found.");
            }
            
            success = true; // Mark as successful if no exceptions were thrown
    
        } catch (IOException | IllegalArgumentException e) {
            // Handle exceptions if needed (already handled by throwing further)
            throw e;
        } finally {
            if (success) {
                // Only try to delete the temp file if the operation was successful
                if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                    throw new IOException("Error updating user file.");
                }
            } else {
                // Cleanup the temp file if the operation failed
                if (tempFile.exists() && !tempFile.delete()) {
                    System.err.println("Warning: Temporary file could not be deleted.");
                }
            }
        }
    }
    
    public static String searchUser(String userID) throws IOException {
        StringBuilder userDetails = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            boolean userFound = false;

            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    userDetails.append("UserID: ").append(userData[0]).append("\n");
                    userDetails.append("Name: ").append(userData[1]).append("\n");
                    userDetails.append("Password: ").append(userData[2]).append("\n");
                    userDetails.append("User Type: ").append(userData[3]).append("\n");
                    userDetails.append("Date of Registration: ").append(userData[4]).append("\n");
                    userFound = true;
                    break;
                }
            }

            if (!userFound) {
                throw new IllegalArgumentException("UserID not found.");
            }
        }

        return userDetails.toString();
    }

    public static void deleteUser(String userID) throws IOException {
        File inputFile = new File(USER_FILE_NAME);
        File tempFile = new File("temp_users.txt");

        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    userFound = true;
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }

            if (!userFound) {
                throw new IllegalArgumentException("UserID not found.");
            }
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Error updating user file.");
        }
    }

    private static boolean isUserIDUnique(String userID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    return false; // UserID is not unique
                }
            }
        }
        return true; // UserID is unique
    }
}
