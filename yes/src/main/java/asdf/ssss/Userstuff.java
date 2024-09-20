package asdf.ssss;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Userstuff {
    private static final String USER_FILE_NAME = "users.txt";
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    public static void addUser(String userID, String name, String password, String userType) throws IOException {
        validateUserInput(userID, name, password, userType);
        
        if (!isUserIDUnique(userID)) {
            throw new IllegalArgumentException("UserID already exists.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_NAME, true))) {
            String dateOfRegistration = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            writer.write(String.format("%s|%s|%s|%s|%s", userID, name, password, userType, dateOfRegistration));
            writer.newLine();
        }
    }

    public static void modifyUser(String userID, String newName, String newPassword, String newUserType) throws IOException {
        validateUserInput(userID, newName, newPassword, newUserType);
        
        File inputFile = new File(USER_FILE_NAME);
        File tempFile = new File("temp_users.txt");

        boolean success = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean userFound = false;

            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    writer.write(userID + "|" + newName + "|" + newPassword + "|" + newUserType + "|" + userData[4]);
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

            success = true;

        } catch (IOException | IllegalArgumentException e) {
            throw e;
        } finally {
            if (success) {
                if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                    throw new IOException("Error updating user file.");
                }
            } else {
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
                String[] userData = line.split("\\|");
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

    private static String currentUserID; // Store the logged-in user's ID

    // Set the current user ID
    public static void setCurrentUserID(String userID) {
        currentUserID = userID;
    }
    
    // Get the current user ID
    public static String getCurrentUserID() {
        return currentUserID;
    }
    
    public static void deleteUser(String userID, String password) throws IOException {
        // Retrieve the current user's ID
        String currentUserID = getCurrentUserID();
    
        // Check if the user is trying to delete their own account
        if (userID.equals(currentUserID)) {
            throw new IllegalArgumentException("You cannot delete your own account.");
        }
    
        // Validate the provided password for the userID
        if (!validateUserPassword(userID, password)) {
            throw new IllegalArgumentException("Invalid password.");
        }
    
        File inputFile = new File(USER_FILE_NAME);
        File tempFile = new File("temp_users.txt");
    
        boolean userFound = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    
            String line;
    
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    userFound = true; // User found
                    continue;
                }
                writer.write(line); // Write the line to temp file if not deleting
                writer.newLine();
            }
    
            if (!userFound) {
                throw new IllegalArgumentException("UserID not found.");
            }
        }
    
        // Replace the original user file with the updated temp file
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Error updating user file.");
        }
    }
    
    private static boolean validateUserPassword(String userID, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
    
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    String storedPassword = userData[2].trim();
                    return storedPassword.equals(password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    
        return false; // UserID not found or password mismatch
    }
    
    

    private static boolean isUserIDUnique(String userID) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData.length == 5 && userData[0].equals(userID)) {
                    return false; // UserID is not unique
                }
            }
        }
        return true; // UserID is unique
    }

    private static void validateUserInput(String userID, String name, String password, String userType) {
        if (!isAlphanumeric(userID) || !isAlphanumeric(name) || !isAlphanumeric(password) || !isAlphanumeric(userType)) {
            throw new IllegalArgumentException("UserID, Name, Password, and User Type must be alphanumeric.");
        }
    }

    private static boolean isAlphanumeric(String input) {
        return input != null && ALPHANUMERIC_PATTERN.matcher(input).matches();
    }
}

