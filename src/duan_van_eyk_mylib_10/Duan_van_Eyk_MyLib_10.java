/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package duan_van_eyk_mylib_10;


// Imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.text.ParseException;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author Duan van Eyk
 */
public class Duan_van_Eyk_MyLib_10 {

    // Public Variables
    public static boolean validInput = true;
    public static String UserMail, Pass;
    public static String NewMail, NewUser, NewPass, titleSearch, codeSearch, userSearch, uEmailSearch, username, checkedOut = " ";
    public static int UserChoice, UserClearance = 0;
    public static double fines = 0.0;
    public static String currentUserEmail = ""; // To store the current user's email
    
    public static void main(String[] args) {
        // Start the application by calling the logPage method
        logPage(); 
    }
    
    

    // Method to handle the connection to the database
    public static Connection connectToDatabase() {
    Connection connection = null; // Initialize the connection variable

    try {
        // Load the UCanAccess driver class to enable database connection
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

        // Define the path to the .accdb file (ensure this path is correct)
        String databaseURL = "jdbc:ucanaccess://" + System.getProperty("user.dir") + "/src/database/LibrarySystem.accdb";

        // Establish the connection to the database using the provided URL
        connection = DriverManager.getConnection(databaseURL);
    } catch (ClassNotFoundException e) {
        // Handle the case where the driver class is not found
        System.out.println("UCanAccess driver not found.");
        e.printStackTrace();
    } catch (SQLException e) {
        // Handle SQL exceptions that may occur during the connection process
        System.out.println("Database connection error.");
        e.printStackTrace();
    }

    return connection; // Return the established connection object
}


    
    // Method to display the login options to the user
public static void logPage() {
    validInput = true; // Flag to control the input loop

    // Start a loop to repeatedly display the login options until a valid choice is made
    while (validInput) {
        UserChoice = 0; // Reset user choice for each iteration

        // Display the welcome message and options for the user
        System.out.println("=======================================");
        System.out.println("            Welcome to MyLib          ");
        System.out.println("=======================================");
        System.out.println("Select an option:");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Quit");
        System.out.println("=======================================");

        // Prompt user for input
        String input = JOptionPane.showInputDialog("Please select the number: ");

        // Input processing
        if (input != null && !input.isEmpty()) {
            try {
                // Parse the user input to an integer
                UserChoice = Integer.parseInt(input); 
            } catch (NumberFormatException e) {
                // Handle invalid input (not a number)
                System.out.println("Invalid input. Please enter a number.");
                continue; // Restart the loop if input is invalid
            }
        } else {
            // Handle case where no input is provided
            System.out.println("No input provided. Please try again.");
            continue; // Restart the loop if no input is given
        }

        // Process the user's choice based on the input
        switch (UserChoice) {
            case 1:
                register(); // Call the register function to register a new user
                break;
            case 2:
                login(); // Call the login function for user authentication
                break;
            case 3:
                validInput = false; // Exit the loop to quit the application
                System.out.println("Thank you for using MyLib. Goodbye!"); // Exit message
                break;
            default:
                // Handle invalid choice
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
        }
    }
}

    
    // Method to handle user login
public static void login() {
    validInput = true; // Flag to control the input loop

    // Start a loop to repeatedly prompt the user for login credentials
    while (validInput) {
        // Reset UserClearance for each login attempt
        UserClearance = 0;

        // Display input prompts for email and password
        String MailIn = JOptionPane.showInputDialog("Please enter your email:");
        String PassIn = JOptionPane.showInputDialog("Please enter your password:");

        // Initialize the database connection
        Connection connection = null; 
        try {
            connection = connectToDatabase(); // Call method to connect to the database
            // SQL query to check credentials
            String sql = "SELECT Role FROM Users WHERE Email = ? AND Password = ?"; 
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, MailIn); // Set email parameter
            preparedStatement.setString(2, PassIn); // Set password parameter
            ResultSet resultSet = preparedStatement.executeQuery(); // Execute the query

            // Process the result set to check if credentials are valid
            if (resultSet.next()) {
                String role = resultSet.getString("Role"); // Get the user's role

                // Check the user's role and assign clearance level accordingly
                if (role.equalsIgnoreCase("Staff")) {
                    UserClearance = 2; // Set clearance for staff
                    currentUserEmail = MailIn; // Store the email of the logged-in user
                } else if (role.equalsIgnoreCase("User")) {
                    UserClearance = 1; // Set clearance for regular user
                    currentUserEmail = MailIn; // Store the email of the logged-in user
                } else {
                    // Handle unexpected roles
                    System.out.println("Unexpected role: " + role);
                }
            } else {
                // Handle invalid login details
                System.out.println("Invalid login details. Please try again.");
            }

            resultSet.close(); // Close the result set
        } catch (SQLException e) {
            // Handle database connection errors
            System.out.println("Database connection error.");
            e.printStackTrace();
        } finally {
            closeConnection(connection); // Ensure the database connection is closed
        }

        // Call the appropriate menu based on UserClearance (this might need to be adjusted based on the application's logic)
        returnToMenu(); // Return to the main menu after login attempt
    }
}


    // Method to retrieve the current user's email
public static String getCurrentUserEmail() {
    // Return the email of the currently logged-in user
    return currentUserEmail; 
}



    // Method to handle user registration
public static void register() {
    validInput = true; // Flag to control the input loop

    while (validInput) {
        // Get user input for registration
        String email = JOptionPane.showInputDialog("Please enter your email");
        String password = JOptionPane.showInputDialog("Please enter your password");
        String username = JOptionPane.showInputDialog("Please enter your username");

        // Default role
        String role = "User"; // All registered users will have the role "User"

        // Check for existing email
        Connection connection = null; // Initialize connection variable
        try {
            connection = connectToDatabase(); // Establish database connection

            // SQL query to check if the email already exists
            String checkEmailSQL = "SELECT Email FROM Users WHERE Email = ?";
            PreparedStatement checkEmailStmt = connection.prepareStatement(checkEmailSQL);
            checkEmailStmt.setString(1, email); // Set the email parameter
            ResultSet resultSet = checkEmailStmt.executeQuery(); // Execute the query

            if (resultSet.next()) {
                // Email already exists
                JOptionPane.showMessageDialog(null, "Email already registered. Please use a different email.");
            } else {
                // SQL query to insert new user into the database
                String insertSQL = "INSERT INTO Users (Email, Password, Username, Role) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSQL);
                insertStmt.setString(1, email); // Set email parameter
                insertStmt.setString(2, password); // Set password parameter
                insertStmt.setString(3, username); // Set username parameter
                insertStmt.setString(4, role); // Set the default role

                // Execute the insert statement
                insertStmt.executeUpdate(); // Insert the new user into the database
                JOptionPane.showMessageDialog(null, "Registration successful!");
                JOptionPane.showMessageDialog(null, "Please Login");

                login(); // Call the login method after successful registration
                validInput = false; // End the registration loop
            }
            resultSet.close(); // Close the ResultSet
        } catch (SQLException e) {
            System.out.println("Database connection error.");
            e.printStackTrace();
        } finally {
            closeConnection(connection); // Ensure the connection is closed
        }
    }
}


    
    // Method to display staff options and handle staff functionalities
public static void Staff() {
    validInput = true; // Flag to control the input loop

    while (validInput) {
        UserChoice = 0; // Reset user choice for each iteration

        // Display user options
        System.out.println("=======================================");
        System.out.println("              Staff Menu              ");
        System.out.println("=======================================");
        System.out.println("Select an option:");
        System.out.println("1. Add book");
        System.out.println("2. Remove book");
        System.out.println("3. Search book");
        System.out.println("4. Search user");
        System.out.println("5. Log out");
        System.out.println("=======================================");

        // User Input
        String input = JOptionPane.showInputDialog("Please select the number: ");

        // Input processing
        if (input != null && !input.isEmpty()) {
            try {
                UserChoice = Integer.parseInt(input); // Parse the user input to an integer
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue; // Restart the loop if input is invalid
            }
        } else {
            System.out.println("No input provided. Please try again.");
            continue; // Restart the loop if no input is given
        }

        // Process the user's choice
        switch (UserChoice) {
            case 1:
                System.out.println("Adding a Book..."); // Inform staff about the action
                addBook(); // Call the addBook function to add a book
                break;
            case 2:
                System.out.println("Removing a Book..."); // Inform staff about the action
                removeBook(); // Call the removeBook function to remove a book
                break;
            case 3:
                System.out.println("Searching for a book..."); // Inform staff about the action
                bookSearch(); // Call the bookSearch function to search for a book
                break;
            case 4:
                System.out.println("Searching for a user..."); // Inform staff about the action
                searchUser(); // Call the searchUser function to search for a user
                break;
            case 5:
                System.out.println("Goodbye!"); // Log out message
                validInput = false; // Exit the loop to log out
                break;
            default:
                System.out.println("Invalid choice. Please select a valid option."); // Inform about invalid choice
        }
    }
}


  
    // Method to display user options and handle user functionalities
public static void User() {
    validInput = true; // Flag to control the input loop

    while (validInput) {
        UserChoice = 0; // Reset user choice for each iteration

        // Display user options
        System.out.println("=======================================");
        System.out.println("               User Menu              ");
        System.out.println("=======================================");
        System.out.println("Select an option:");
        System.out.println("1. Search book");
        System.out.println("2. Check book in");
        System.out.println("3. Account");
        System.out.println("4. Log out");
        System.out.println("=======================================");

        // User Input
        String input = JOptionPane.showInputDialog("Please select the number: ");

        // Input processing
        if (input != null && !input.isEmpty()) {
            try {
                UserChoice = Integer.parseInt(input); // Parse the user input to an integer
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue; // Restart the loop if input is invalid
            }
        } else {
            System.out.println("No input provided. Please try again.");
            continue; // Restart the loop if no input is given
        }

        // Process the user's choice
        switch (UserChoice) {
            case 1:
                System.out.println("Searching for a book..."); // Inform user about the action
                bookSearch(); // Call the bookSearch function to search for a book
                break;
            case 2:
                System.out.println("Checking a book in..."); // Inform user about the action
                String bookCode = JOptionPane.showInputDialog("Enter the book code to check in:"); // Prompt for book code
                checkinBook(bookCode); // Call checkinBook with the bookCode parameter
                break;
            case 3:
                System.out.println("Displaying account details..."); // Inform user about the action
                account(); // Call the account method to display account details
                break;
            case 4:
                System.out.println("Goodbye!"); // Log out message
                validInput = false; // Exit the loop to log out
                break;
            default:
                System.out.println("Invalid choice. Please select a valid option."); // Inform about invalid choice
        }
    }
}



    // Method to search for a user by email and perform actions based on the search result
public static void searchUser() {
    validInput = true; // Flag to control the input loop

    while (validInput) {
        // Prompt for user email
        String userEmail = JOptionPane.showInputDialog("Please enter the user's email to search:");

        // Check credentials against the database
        Connection connection = null;
        try {
            connection = connectToDatabase(); // Connect to the database
            String sql = "SELECT Username, Email FROM Users WHERE Email = ?"; // SQL query to find user by email
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userEmail); // Set the email parameter
            ResultSet resultSet = preparedStatement.executeQuery(); // Execute the query

            // Check if user exists
            if (resultSet.next()) {
                String username = resultSet.getString("Username"); // Get username
                String email = resultSet.getString("Email"); // Get email

                // Display user information
                String userInfo = "Username: " + username + "\nEmail: " + email;
                System.out.println("User Found:\n" + userInfo);

                // Offer options for further actions
                System.out.println("What would you like to do?");
                String[] options = {"Check Fines", "View Account", "View User Details", "Delete User", "Go Back"};
                for (int i = 0; i < options.length; i++) {
                    System.out.println((i + 1) + ". " + options[i]); // Display action options
                }

                // User input for action choice
                int choice = Integer.parseInt(JOptionPane.showInputDialog("Please select the number: "));

                switch (choice) {
                    case 1:
                        // Call method to check fines with the user's email
                        checkFines(userEmail);
                        break;
                    case 2:
                        // Call method to view account details
                        staffAccount(userEmail); // Call your staffAccount method, passing the user's email
                        break;
                    case 3:
                        // Call staffViewUser to view user details
                        staffViewUser(userEmail);
                        break;
                    case 4:
                        // Call method to delete user
                        if (deleteUser(userEmail)) {
                            System.out.println("User deleted successfully. Returning to menu...");
                        } else {
                            System.out.println("Error deleting user. Please try again.");
                        }
                        break;
                    case 5:
                        validInput = false; // Go back to the menu
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a valid option."); // Handle invalid choice
                }
            } else {
                // User not found
                System.out.println("No user found with that email.");

                // Prompt to return to the menu
                int option = JOptionPane.showConfirmDialog(null, "Would you like to return to the menu?", "Return to Menu", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    validInput = false; // Go back to the menu
                }
            }

            resultSet.close(); // Close the ResultSet
        } catch (SQLException e) {
            System.out.println("Database connection error.");
            e.printStackTrace();
        } finally {
            closeConnection(connection); // Ensure the connection is closed
        }
    }
}


    // Method to search for books by title or book code
public static void bookSearch() {
    validInput = true; // Flag to control the input loop

    while (validInput) {
        UserChoice = 0; // Reset user choice for each iteration

        // User Options
        System.out.println("=======================================");
        System.out.println("            Book Search               ");
        System.out.println("=======================================");
        System.out.println("Select an option: ");
        System.out.println("1. Search title");
        System.out.println("2. Search book code");
        System.out.println("3. Go back");
        System.out.println("=======================================");

        // User Input
        String input = JOptionPane.showInputDialog("Please select the number: ");

        // Input Processing
        if (input != null && !input.isEmpty()) {
            try {
                UserChoice = Integer.parseInt(input); // Parse the user input to an integer
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue; // Restart the loop if input is invalid
            }
        } else {
            System.out.println("No input provided. Please try again.");
            continue; // Restart the loop if no input is given
        }

        // Process the user's choice
        switch (UserChoice) {
            case 1:
                // Prompt for book title to search
                String titleSearch = JOptionPane.showInputDialog("Please enter the name of the book: ");
                searchBookByTitle(titleSearch); // Call the searchBookByTitle method
                break;
            case 2:
                // Prompt for book code to search
                String codeSearch = JOptionPane.showInputDialog("Please enter the book code of the book: ");
                searchBookByCode(codeSearch); // Call the searchBookByCode method
                break;
            case 3:
                // Go back to the previous menu
                returnToMenu(); // Assuming you have a method to return to the menu
                break;
            default:
                System.out.println("Invalid choice. Please select a valid option."); // Handle invalid choice
        }
    }
}



    // Method to search for a book by its title
public static void searchBookByTitle(String title) {
    Connection connection = null; // Initialize database connection

    try {
        // Connect to the database
        connection = connectToDatabase();
        String sql = "SELECT * FROM Books WHERE Title = ?"; // SQL query to find the book by title
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, title); // Set the title parameter in the query
        ResultSet resultSet = preparedStatement.executeQuery(); // Execute the query

        // Check if the book exists in the result set
        if (resultSet.next()) {
            // Retrieve book details
            String bookCode = resultSet.getString("BookCode");
            String availability = resultSet.getString("Availability");
            int bookID = resultSet.getInt("BookID");

            // Display book details
            System.out.println("Book Found: " + title + " | Code: " + bookCode + " | Availability: " + availability);

            // Check if the book is available
            if (availability.equalsIgnoreCase("Available")) {
                // Prompt user to check out the book
                int response = JOptionPane.showConfirmDialog(null, "Would you like to check out this book?", "Check Out", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    checkoutBook(bookCode); // Call the checkoutBook method with the book code
                }
            } else {
                // Handle the case where the book is not available
                System.out.println("The book is currently not available.");
                int response = JOptionPane.showConfirmDialog(null, "The book is currently not available. Would you like to search for another book?", "Book Not Available", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    bookSearch(); // Allow user to search for another book
                } else {
                    // Go back to the menu based on user role
                    if (UserClearance == 1) {
                        User(); // Return to user menu
                    } else if (UserClearance == 2) {
                        Staff(); // Return to staff menu
                    }
                }
            }
        } else {
            // Handle the case where the book was not found
            System.out.println("The book was not found in the library.");
            int response = JOptionPane.showConfirmDialog(null, "The book was not found in the library. Would you like to search for another book?", "Book Not Found", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                bookSearch(); // Allow user to search for another book
            } else {
                returnToMenu(); // Go back to the menu
            }
        }

        resultSet.close(); // Close the result set
    } catch (SQLException e) {
        System.out.println("Database error."); // Handle SQL exceptions
        e.printStackTrace();
    } finally {
        closeConnection(connection); // Ensure the database connection is closed
    }
}


    // Method to search for a book by its code
public static void searchBookByCode(String bookCode) {
    Connection connection = null; // Initialize database connection

    try {
        // Connect to the database
        connection = connectToDatabase();
        String sql = "SELECT * FROM Books WHERE BookCode = ?"; // SQL query to find the book by code
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, bookCode); // Set the book code parameter in the query
        ResultSet resultSet = preparedStatement.executeQuery(); // Execute the query

        // Check if the book exists in the result set
        if (resultSet.next()) {
            // Retrieve book details
            String title = resultSet.getString("Title");
            String availability = resultSet.getString("Availability");

            // Display book details
            System.out.println("Book Found: " + title + " | Code: " + bookCode + " | Availability: " + availability);

            // Check if the book is available
            if (availability.equalsIgnoreCase("Available")) {
                // Prompt user to check out the book
                int response = JOptionPane.showConfirmDialog(null, "Would you like to check out this book?", "Check Out", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    checkoutBook(bookCode); // Call the checkoutBook method with the book code
                }
            } else {
                // Handle the case where the book is not available
                System.out.println("The book is currently not available.");
                int response = JOptionPane.showConfirmDialog(null, "The book is currently not available. Would you like to search for another book?", "Book Not Available", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    bookSearch(); // Allow user to search for another book
                } else {
                    returnToMenu(); // Go back to the menu
                }
            }
        } else {
            // Handle the case where the book was not found
            System.out.println("The book was not found in the library.");
            int response = JOptionPane.showConfirmDialog(null, "The book was not found in the library. Would you like to search for another book?", "Book Not Found", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                bookSearch(); // Allow user to search for another book
            } else {
                returnToMenu(); // Go back to the menu
            }
        }

        resultSet.close(); // Close the result set
    } catch (SQLException e) {
        System.out.println("Database error."); // Handle SQL exceptions
        e.printStackTrace();
    } finally {
        closeConnection(connection); // Ensure the database connection is closed
    }
}



// Method to add a book to the library database
public static void addBook() {
    Connection connection = null; // Initialize database connection

    try {
        // Establish connection to the database
        connection = connectToDatabase();

        String isbn = null; // Variable to hold the ISBN

        // Loop until a valid ISBN is entered or the user cancels
        while (true) {
            // Prompt for the ISBN
            isbn = JOptionPane.showInputDialog("Enter the ISBN of the book (or type 'cancel' to cancel):");

            // Check if the user wants to cancel the operation
            if (isbn == null || isbn.trim().equalsIgnoreCase("cancel")) {
                System.out.println("Operation cancelled.");
                return; // Exit the method if canceled
            }

            // Validate the ISBN format
            if (!validateISBN(isbn)) {
                System.out.println("Invalid ISBN. Please enter a valid ISBN."); // Notify user of invalid input
                continue; // Prompt again for the ISBN
            }

            break; // Valid ISBN entered, exit the loop
        }

        // Generate the Book Code based on the ISBN
        String bookCode = createBookCode(isbn); // Generate the book code

        // Check if bookCode is null (this shouldn't happen if ISBN is valid)
        if (bookCode == null) {
            System.out.println("Error generating book code."); // Notify user of error
            return; // Exit if book code generation fails
        }

        // Get additional book details from staff
        String title = JOptionPane.showInputDialog("Enter the book title:");
        String author = JOptionPane.showInputDialog("Enter the book author:");
        String availability = "Available"; // Default availability status

        // SQL statement to insert the new book into the database
        String sql = "INSERT INTO Books (Title, Author, ISBN, BookCode, Availability) VALUES (?, ?, ?, ?, ?)";

        // Prepare the SQL statement
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, title); // Set book title
        preparedStatement.setString(2, author); // Set book author
        preparedStatement.setString(3, isbn); // Set ISBN
        preparedStatement.setString(4, bookCode); // Set generated book code
        preparedStatement.setString(5, availability); // Set availability status

        // Execute the insertion and check the result
        int rowsAffected = preparedStatement.executeUpdate(); // Execute the SQL insert statement
        if (rowsAffected > 0) {
            System.out.println("Book added successfully with Book Code: " + bookCode); // Confirm successful addition
        } else {
            System.out.println("Failed to add the book."); // Notify user of failure
        }

        // Close the prepared statement
        preparedStatement.close();

    } catch (SQLException e) {
        // Handle SQL exceptions
        System.out.println("Database error: " + e.getMessage());
    } finally {
        // Ensure the database connection is closed
        closeConnection(connection);
    }

    // Call the staff menu again to return to the staff interface
    Staff(); // Go back to the staff menu
}



    // Method to get the current count of books for a given ISBN
public static int getBookCountByISBN(Connection connection, String isbn) throws SQLException {
    // SQL query to count the number of books with the given ISBN in the database
    String sql = "SELECT COUNT(*) AS BookCount FROM Books WHERE ISBN = ?";
    
    // Prepare the SQL statement to prevent SQL injection
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, isbn); // Set the ISBN in the query

    // Execute the query and retrieve the result
    ResultSet resultSet = preparedStatement.executeQuery();
    
    // Variable to store the count of books
    int count = 0;
    
    // Check if a result was returned
    if (resultSet.next()) {
        // Retrieve the count from the result set
        count = resultSet.getInt("BookCount");
    }

    // Close the result set and the prepared statement
    resultSet.close();
    preparedStatement.close();
    
    // Return the count of existing books for this ISBN
    return count;
}


// Method to validate ISBN
public static boolean validateISBN(String isbn) {
    // Remove any hyphens or spaces for validation
    isbn = isbn.replace("-", "").replace(" ", "");

    // Validate ISBN-10 (10 characters long)
    if (isbn.length() == 10) {
        int sum = 0;

        // Loop through the first 9 characters of the ISBN-10
        for (int i = 0; i < 9; i++) {
            // Ensure that each character is a digit
            if (!Character.isDigit(isbn.charAt(i))) {
                return false; // Invalid character found
            }
            // Sum each digit multiplied by its position (i+1)
            sum += (isbn.charAt(i) - '0') * (i + 1);
        }

        // Check the last character for either a digit or 'X'
        char checkChar = isbn.charAt(9);
        // Convert 'X' to 10, otherwise convert the digit, or return false if invalid
        int checkValue = (checkChar == 'X') ? 10 : (Character.isDigit(checkChar) ? checkChar - '0' : -1);
        if (checkValue == -1) return false; // Invalid character in check position

        // Add the check digit (multiplied by 10) to the sum
        sum += checkValue * 10;
        // Valid ISBN-10 if sum modulo 11 is 0
        return (sum % 11 == 0);
    }

    // Validate ISBN-13 (13 characters long)
    if (isbn.length() == 13) {
        int sum = 0;

        // Loop through the first 12 characters of the ISBN-13
        for (int i = 0; i < 12; i++) {
            // Ensure that each character is a digit
            if (!Character.isDigit(isbn.charAt(i))) {
                return false; // Invalid character found
            }
            // Multiply even-positioned digits by 1 and odd-positioned digits by 3
            sum += (i % 2 == 0 ? 1 : 3) * (isbn.charAt(i) - '0');
        }

        // Calculate the check digit as the remainder from the sum
        int checkDigit = (10 - (sum % 10)) % 10;

        // ISBN-13 is valid if the check digit matches the last character
        return checkDigit == (isbn.charAt(12) - '0');
    }

    // Invalid length for both ISBN-10 and ISBN-13
    return false;
}




// Method to create a unique book code based on a valid ISBN and current count of books
public static String createBookCode(String isbn) {
    // Ensure the ISBN is valid before creating a book code
    if (!validateISBN(isbn)) {
        // If ISBN is invalid, return null to indicate an error
        return null; 
    }

    // Prepare to count existing books with the same ISBN
    Connection connection = null;
    int bookCount = 0;

    try {
        // Establish a connection to the database
        connection = connectToDatabase();

        // SQL query to count the number of books with the same ISBN in the database
        String countSql = "SELECT COUNT(*) FROM Books WHERE ISBN = ?";
        PreparedStatement countStatement = connection.prepareStatement(countSql);
        countStatement.setString(1, isbn); // Set the ISBN in the query

        // Execute the query and get the count of books with this ISBN
        ResultSet resultSet = countStatement.executeQuery();
        if (resultSet.next()) {
            bookCount = resultSet.getInt(1); // Get the count from the result set
        }

        // Close the result set and statement after execution
        resultSet.close();
        countStatement.close();
    } catch (SQLException e) {
        // Handle SQL exceptions and print the error message
        System.out.println("Error while counting books: " + e.getMessage());
    } finally {
        // Ensure the connection is closed even if an exception occurs
        closeConnection(connection);
    }

    // Generate a unique book code using the ISBN and the count of books with the same ISBN
    String bookCode = isbn + "-" + (bookCount + 1);

    // Return the generated unique book code
    return bookCode;
}



    // Method to remove a book from the library using its unique book code
public static void removeBook() {
    Connection connection = null;

    try {
        // Establish connection to the database
        connection = connectToDatabase();

        // Prompt staff to enter the book code of the book they want to remove
        String bookCode = JOptionPane.showInputDialog("Enter the book code of the book to remove:");

        // SQL statement to delete the book from the database based on its book code
        String sql = "DELETE FROM Books WHERE BookCode = ?";

        // Prepare the SQL statement with the provided book code
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, bookCode); // Set the book code parameter

        // Execute the deletion query and get the number of rows affected
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            // If at least one row was deleted, confirm that the book was removed
            System.out.println("Book removed successfully!");
        } else {
            // If no rows were affected, inform the user that no book was found with the given code
            System.out.println("No book found with the provided code.");
        }

        // Close the prepared statement after execution
        preparedStatement.close();

    } catch (SQLException e) {
        // Handle any SQL exceptions and print the error message
        System.out.println("Database error: " + e.getMessage());
    } finally {
        // Ensure the connection is closed even if an exception occurs
        closeConnection(connection);
    }

    // Call the staff menu again to return to the staff options
    Staff(); // Go back to the staff menu
}


    
  // Method to check out a book using its unique book code
public static void checkoutBook(String bookCode) {
    Connection connection = null;

    try {
        // Establish connection to the database
        connection = connectToDatabase();

        // Get the current date for the borrow date
        Date borrowDate = new Date();

        // Calculate the due date (e.g., 14 days after the borrow date)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(borrowDate); // Set the current date in the calendar
        calendar.add(Calendar.DAY_OF_YEAR, 14); // Add 14 days for the due date
        Date dueDate = calendar.getTime(); // Get the calculated due date

        // Retrieve the book title for citation generation based on the book code
        String bookTitle = getBookTitle(bookCode); // Implement this method to get the book title from the bookCode
        if (bookTitle == null) {
            // If the book title is not found, print a message and exit the method
            System.out.println("Book not found. Please check the book code.");
            return; // Exit the checkout process if the book is not found
        }

        // Insert a new record into the Borrowed Books table
        String sqlBorrow = "INSERT INTO [Borrowed Books] (BookCode, Email, BorrowDate, DueDate, ReturnDate, Fine) VALUES (?, ?, ?, ?, NULL, 0)";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlBorrow);
        preparedStatement.setString(1, bookCode); // Set the book code
        preparedStatement.setString(2, getCurrentUserEmail()); // Set the current user's email
        preparedStatement.setDate(3, new java.sql.Date(borrowDate.getTime())); // Set the borrow date
        preparedStatement.setDate(4, new java.sql.Date(dueDate.getTime())); // Set the due date
        preparedStatement.executeUpdate(); // Execute the SQL insert

        // Update the availability of the book to 'Not Available' in the Books table
        String sqlUpdate = "UPDATE Books SET Availability = 'Not Available' WHERE BookCode = ?";
        preparedStatement = connection.prepareStatement(sqlUpdate);
        preparedStatement.setString(1, bookCode); // Set the book code
        preparedStatement.executeUpdate(); // Execute the SQL update

        // Create a citation for the book checkout
        createCitation(getCurrentUserEmail(), bookTitle, borrowDate); // Pass the current user's email, book title, and borrow date

        // Print a success message with the due date
        System.out.println("Book successfully checked out. Due date: " + new java.sql.Date(dueDate.getTime()));
        returnToMenu(); // Return to the main menu after a successful checkout

    } catch (SQLException e) {
        // Handle any SQL exceptions that occur during the checkout process
        System.out.println("Database error during checkout: " + e.getMessage());
        JOptionPane.showMessageDialog(null, "An error occurred while checking out the book. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        // Handle any other unexpected exceptions that may occur
        System.out.println("An unexpected error occurred: " + e.getMessage());
        JOptionPane.showMessageDialog(null, "An unexpected error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        // Ensure the database connection is closed in the end
        closeConnection(connection);
    }
}


    
// Method to create a citation for a checked-out book
public static void createCitation(String userEmail, String bookTitle, Date checkoutDate) {
    Connection connection = null;

    try {
        // Establish connection to the database
        connection = connectToDatabase();

        // Convert java.util.Date to java.sql.Date for SQL compatibility
        java.sql.Date sqlCheckoutDate = new java.sql.Date(checkoutDate.getTime());

        // Create the citation text that will be inserted into the database
        String citation = "Citation for '" + bookTitle + "' checked out on " + sqlCheckoutDate.toString();

        // SQL query to insert the citation data into the Citations table
        String sql = "INSERT INTO Citations (UserEmail, BookTitle, CheckoutDate, Citation) VALUES (?, ?, ?, ?)";

        // Prepare the SQL statement for execution
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, userEmail); // Set the user's email
        preparedStatement.setString(2, bookTitle); // Set the book title
        preparedStatement.setDate(3, sqlCheckoutDate); // Set the checkout date
        preparedStatement.setString(4, citation); // Set the generated citation text

        // Debugging statements to print details for verification
        System.out.println(userEmail);
        System.out.println(bookTitle);
        System.out.println(sqlCheckoutDate);
        System.out.println(citation);

        // Execute the insertion into the Citations table
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            // Citation successfully created
            System.out.println("Citation created successfully."); // Optional message for success
        } else {
            System.out.println("Failed to create citation."); // Message if no rows were affected
        }

        // Close the prepared statement after execution
        preparedStatement.close();

    } catch (SQLException e) {
        // Handle any SQL exceptions that occur during the citation creation process
        System.out.println("Database error: " + e.getMessage());
    } finally {
        // Ensure the database connection is closed at the end
        closeConnection(connection);
    }
}


    // Method to retrieve the book title based on the provided book code
public static String getBookTitle(String bookCode) {
    Connection connection = null;
    String bookTitle = null; // Variable to store the retrieved book title

    try {
        // Establish connection to the database
        connection = connectToDatabase();

        // SQL query to fetch the book title for the given book code
        String sql = "SELECT Title FROM Books WHERE BookCode = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, bookCode); // Set the book code in the SQL query

        // Execute the query and get the result
        ResultSet resultSet = preparedStatement.executeQuery();

        // If a result is found, retrieve the book title
        if (resultSet.next()) {
            bookTitle = resultSet.getString("Title"); // Get the title from the result set
        }

        // Close the result set and prepared statement after use
        resultSet.close();
        preparedStatement.close();

    } catch (SQLException e) {
        // Print an error message if something goes wrong with the database query
        System.out.println("Error retrieving book title.");
        e.printStackTrace(); // Print the stack trace for detailed debugging
    } finally {
        // Close the database connection in the 'finally' block to ensure it always happens
        closeConnection(connection);
    }

    return bookTitle; // Return the retrieved book title or null if not found
}


   // Method to check in a book based on the provided book code
public static void checkinBook(String bookCode) {
    Connection connection = null; // Variable to hold the database connection

    try {
        // Establish connection to the database
        connection = connectToDatabase();

        // Step 1: Retrieve details of the borrowed book
        String getBorrowedBookSql = "SELECT * FROM [Borrowed Books] WHERE BookCode = ? AND ReturnDate IS NULL";
        PreparedStatement getBorrowedBookStmt = connection.prepareStatement(getBorrowedBookSql);
        getBorrowedBookStmt.setString(1, bookCode); // Set the book code in the SQL query
        ResultSet borrowedBookResultSet = getBorrowedBookStmt.executeQuery(); // Execute the query

        // Check if the borrowed book record exists
        if (borrowedBookResultSet.next()) {
            String userEmail = borrowedBookResultSet.getString("Email"); // Get the email of the user who borrowed the book
            Date borrowDate = borrowedBookResultSet.getDate("BorrowDate"); // Get the date the book was borrowed
            Date dueDate = borrowedBookResultSet.getDate("DueDate"); // Get the due date for returning the book
            Date returnDate = new Date(System.currentTimeMillis()); // Current date as the return date

            // Step 2: Calculate the fine (if any) for late return
            double fineAmount = calculateFines(dueDate, returnDate);

            // Step 3: Update the BorrowedBooks table to set ReturnDate and Fine
            String updateBorrowedBookSql = "UPDATE [Borrowed Books] SET ReturnDate = ?, Fine = ? WHERE BookCode = ? AND Email = ?";
            PreparedStatement updateBorrowedBookStmt = connection.prepareStatement(updateBorrowedBookSql);
            updateBorrowedBookStmt.setDate(1, new java.sql.Date(returnDate.getTime())); // Set the return date
            updateBorrowedBookStmt.setDouble(2, fineAmount); // Set the calculated fine
            updateBorrowedBookStmt.setString(3, bookCode); // Set the book code
            updateBorrowedBookStmt.setString(4, userEmail); // Set the user's email
            updateBorrowedBookStmt.executeUpdate(); // Execute the update

            // Step 4: Update book availability in the Books table
            String updateBookSql = "UPDATE Books SET Availability = 'Available' WHERE BookCode = ?";
            PreparedStatement updateBookStmt = connection.prepareStatement(updateBookSql);
            updateBookStmt.setString(1, bookCode); // Set the book code
            updateBookStmt.executeUpdate(); // Execute the update

            // Step 5: Update the user's fines in the Users table
            updateUserFines(userEmail, fineAmount); // Call the method to update user fines

            // Step 6: Notify the user of the fine (if any) and successful check-in
            if (fineAmount > 0) {
                System.out.println("Book checked in successfully, but you have been fined: $" + fineAmount); // Use terminal message
            } else {
                System.out.println("Book checked in successfully. No fines were applied."); // Use terminal message
            }

            returnToMenu(); // Go back to the menu

        } else {
            // Notify if the book is not found or already checked in
            System.out.println("Book with code " + bookCode + " not found or already checked in."); // Use terminal message
        }

    } catch (SQLException e) {
        // Handle SQL exceptions during the check-in process
        System.out.println("Error during check-in.");
        e.printStackTrace(); // Print the stack trace for debugging
    } finally {
        // Ensure the database connection is closed
        closeConnection(connection);
    }
}


    // Method to calculate fines based on due date and return date
public static double calculateFines(Date dueDate, Date returnDate) {
    // Calculate the number of days late
    long daysLate = (returnDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);

    // Calculate the fine amount; assume a fine of 1.0 per day late
    double fineAmount = (daysLate > 0) ? daysLate * 10.0 : 0; 

    // Return the total fine amount
    return fineAmount;
}


    // Method to update a user's fines in the Users table
public static void updateUserFines(String userEmail, double fineAmount) {
    Connection connection = null;

    try {
        // Establish connection to the database
        connection = connectToDatabase();

        // Retrieve the current fines for the user
        String getFinesSql = "SELECT Fines FROM Users WHERE Email = ?";
        PreparedStatement getFinesStmt = connection.prepareStatement(getFinesSql);
        getFinesStmt.setString(1, userEmail);
        ResultSet resultSet = getFinesStmt.executeQuery();

        double currentFines = 0; // Variable to hold the current fines
        if (resultSet.next()) {
            currentFines = resultSet.getDouble("Fines"); // Get the current fines
        }

        // Calculate the updated fines by adding the new fine amount
        double updatedFines = currentFines + fineAmount;

        // Update the user's fines in the Users table
        String updateFinesSql = "UPDATE Users SET Fines = ? WHERE Email = ?";
        PreparedStatement updateFinesStmt = connection.prepareStatement(updateFinesSql);
        updateFinesStmt.setDouble(1, updatedFines); // Set the updated fines
        updateFinesStmt.setString(2, userEmail); // Set the user's email
        updateFinesStmt.executeUpdate(); // Execute the update

    } catch (SQLException e) {
        // Handle SQL exceptions
        System.out.println("Error updating user fines: " + e.getMessage());
    } finally {
        // Close the database connection
        closeConnection(connection);
    }
}


    
 // Function to handle fines for a user
public static void checkFines(String userEmail) {
    Connection connection = null;

    try {
        // Connect to the database
        connection = connectToDatabase();
        
        // Prepare and execute the SQL statement to get the user's fines
        String sql = "SELECT Fines FROM Users WHERE Email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, userEmail);
        ResultSet resultSet = preparedStatement.executeQuery();

        // Check if the user exists and retrieve their fines
        if (resultSet.next()) {
            double currentFines = resultSet.getDouble("Fines");
            System.out.println("You have R" + currentFines + " in fines."); // Terminal message

            // Prompt user to pay fines
            int response = JOptionPane.showConfirmDialog(null, "You have R" + currentFines + " in fines. Would you like to pay now?", "Pay Fines", JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                // Get payment amount from user
                String paymentInput = JOptionPane.showInputDialog("Enter payment amount: ");
                try {
                    double payment = Double.parseDouble(paymentInput); // Parse payment input
                    // Validate payment amount
                    if (payment <= currentFines && payment > 0) {
                        // Update fines in the user's account
                        updateFinesInAccount(userEmail, currentFines - payment);
                        JOptionPane.showMessageDialog(null, "Fines successfully paid. Remaining fines: R" + (currentFines - payment));
                        System.out.println("Payment successful. New fines balance: R" + (currentFines - payment)); // Terminal message
                    } else {
                        JOptionPane.showMessageDialog(null, "Payment exceeds current fines or is invalid.");
                        System.out.println("Invalid payment attempt. Amount entered: R" + payment); // Terminal message
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid payment input
                    JOptionPane.showMessageDialog(null, "Invalid payment amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Error in payment input: " + e.getMessage()); // Terminal message
                }
            } else {
                // If the user chooses not to pay, return to the menu
                returnToMenu();
            }
        } else {
            // Handle case where user is not found
            JOptionPane.showMessageDialog(null, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("No user found for email: " + userEmail); // Terminal message
        }

        // Close the result set
        resultSet.close();
    } catch (SQLException e) {
        // Handle SQL exceptions
        System.out.println("Database connection error: " + e.getMessage()); // Terminal message
        e.printStackTrace();
    } finally {
        // Ensure the database connection is closed
        closeConnection(connection);
    }
}



    // Update fines in the user's account
public static void updateFinesInAccount(String email, double updatedFines) {
    Connection connection = null;
    try {
        // Establish a connection to the database
        connection = connectToDatabase();
        
        // Prepare the SQL statement to update the user's fines
        String sql = "UPDATE Users SET Fines = ? WHERE Email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        
        // Set the updated fines and the user's email
        preparedStatement.setDouble(1, updatedFines);
        preparedStatement.setString(2, email);
        
        // Execute the update statement
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        // Handle any SQL exceptions
        System.out.println("Error updating fines.");
        e.printStackTrace();
    } finally {
        // Ensure the database connection is closed
        closeConnection(connection);
    }
}


 // Method to display account details for the current user
public static void account() {
    Connection connection = null; // Initialize connection variable
    try {
        // Connect to the database
        connection = connectToDatabase();
        
        // Prepare SQL statement to retrieve user details
        String sql = "SELECT * FROM Users WHERE Email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, getCurrentUserEmail()); // Get the current user's email
        ResultSet resultSet = preparedStatement.executeQuery();

        // Check if the user exists in the database
        if (resultSet.next()) {
            // Retrieve user details from the result set
            String username = resultSet.getString("Username");
            String email = resultSet.getString("Email");
            String password = resultSet.getString("Password");
            double fines = resultSet.getDouble("Fines");

            // Display account details
            System.out.println("=======================================");
            System.out.println("            Account Details            ");
            System.out.println("=======================================");
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);
            System.out.println("Fines: R" + fines);
            System.out.println("=======================================");

            // Offer options to the user
            int choice = 0; // Initialize choice variable
            boolean validInput = true; // Flag to control the input loop

            while (validInput) {
                // Display options
                String options = "Select an option:\n" +
                                 "1. Go back\n" +
                                 "2. View Fines\n" +
                                 "=======================================";

                String input = JOptionPane.showInputDialog(options); // Prompt for user input

                // Input processing
                if (input != null && !input.isEmpty()) {
                    try {
                        choice = Integer.parseInt(input); // Parse the user input to an integer
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue; // Restart the loop if input is invalid
                    }
                } else {
                    System.out.println("No input provided. Please try again.");
                    continue; // Restart the loop if no input is given
                }

                // Process the user's choice
                switch (choice) {
                    case 1:
                        returnToMenu(); // Go back to the main menu
                        validInput = false; // Exit the loop
                        break;
                    case 2:
                        checkFines(getCurrentUserEmail()); // Check the user's fines
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            }
        } else {
            // Handle case where no account is found
            JOptionPane.showMessageDialog(null, "No account found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Close the result set
        resultSet.close();
    } catch (SQLException e) {
        // Handle SQL exceptions
        System.out.println("Database error.");
        e.printStackTrace();
    } finally {
        // Ensure the database connection is closed
        closeConnection(connection);
    }
}


    // Method for staff to view user details with an option to delete the user
public static void staffViewUser(String userEmail) {
    Connection connection = null; // Initialize connection variable
    try {
        // Connect to the database
        connection = connectToDatabase();
        
        // Prepare SQL statement to retrieve user details
        String sql = "SELECT * FROM Users WHERE Email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, userEmail); // Set the user's email in the query
        ResultSet resultSet = preparedStatement.executeQuery();

        // Check if the user exists in the database
        if (resultSet.next()) {
            // Retrieve user details from the result set
            String username = resultSet.getString("Username");
            double fines = resultSet.getDouble("Fines");

            // Display user information in a dialog
            String userInfo = "=======================================\n" +
                              "              User Details            \n" +
                              "=======================================\n" +
                              "Username: " + username + "\n" +
                              "Fines: R" + fines + "\n" +
                              "=======================================";

            JOptionPane.showMessageDialog(null, userInfo, "User Information", JOptionPane.INFORMATION_MESSAGE);

            // Ask for confirmation to delete the user
            int response = JOptionPane.showConfirmDialog(null, "Would you like to delete this user?", "Delete User", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                // Call the deleteUser method to delete the user
                deleteUser(userEmail);
                JOptionPane.showMessageDialog(null, "User successfully deleted."); // Confirmation message
            }
        } else {
            // Handle case where the user is not found
            JOptionPane.showMessageDialog(null, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Close the result set
        resultSet.close();
    } catch (SQLException e) {
        // Handle SQL exceptions
        System.out.println("Error retrieving user account.");
        e.printStackTrace();
    } finally {
        // Ensure the database connection is closed
        closeConnection(connection);
    }
}



// Method to delete a user from the Users table
public static boolean deleteUser(String email) {
    Connection connection = null; // Initialize connection variable
    try {
        // Connect to the database
        connection = connectToDatabase();
        
        // Prepare SQL statement to delete the user
        String sql = "DELETE FROM Users WHERE Email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email); // Set the user's email in the query
        
        // Execute the update and get the number of affected rows
        int rowsAffected = preparedStatement.executeUpdate();
        
        // Return true if a user was deleted (i.e., rowsAffected is greater than 0)
        return rowsAffected > 0; 
    } catch (SQLException e) {
        // Handle SQL exceptions
        System.out.println("Error deleting user.");
        e.printStackTrace();
        return false; // Return false on error
    } finally {
        // Ensure the database connection is closed
        closeConnection(connection);
    }
}


   // Method for staff to view a user's account details
public static void staffAccount(String userEmail) {
    Connection connection = null; // Initialize connection variable
    try {
        // Connect to the database
        connection = connectToDatabase();
        
        // Prepare SQL statement to retrieve user account details
        String sql = "SELECT Username, Email, Fines FROM Users WHERE Email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, userEmail); // Set the user's email in the query
        
        // Execute the query and get the result set
        ResultSet resultSet = preparedStatement.executeQuery();

        // Check if a user account exists
        if (resultSet.next()) {
            String username = resultSet.getString("Username");
            String email = resultSet.getString("Email");
            double fines = resultSet.getDouble("Fines");

            // Format account information with a clear layout
            String accountInfo = "=======================================\n" +
                                 "              Account Details          \n" +
                                 "=======================================\n" +
                                 "Username: " + username + "\n" +
                                 "Email: " + email + "\n" +
                                 "Fines: R" + fines + "\n" +
                                 "=======================================";

            // Display account information
            JOptionPane.showMessageDialog(null, accountInfo, "Account Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Display error if no account is found
            JOptionPane.showMessageDialog(null, "No account found for this user.", "Account Not Found", JOptionPane.ERROR_MESSAGE);
        }

        // Close the result set
        resultSet.close();
    } catch (SQLException e) {
        // Handle SQL exceptions
        System.out.println("Error retrieving user account.");
        e.printStackTrace();
    } finally {
        // Ensure the database connection is closed
        closeConnection(connection);
    }
}

// Method to return to the appropriate menu based on user clearance level
public static void returnToMenu() {
    // Check if the user has normal user clearance
    if (UserClearance == 1) {
        User(); // Go back to the User menu
    } 
    // Check if the user has staff clearance
    else if (UserClearance == 2) {
        Staff(); // Go back to the Staff menu
    }
}

    // Method to close the database connection
public static void closeConnection(Connection connection) {
    // Check if the connection is not null
    if (connection != null) {
        try {
            connection.close(); // Attempt to close the connection
        } catch (SQLException e) {
            // Handle any SQL exceptions that may occur while closing
            System.out.println("Error closing the connection."); // Log the error message
            e.printStackTrace(); // Print the stack trace for debugging purposes
        }
    }
}


 
    
    
    
    
    
    
    
}
