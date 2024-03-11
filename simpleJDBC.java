import java.sql.*;
import java.util.Scanner;

class simpleJDBC {


  public static void main(String[] args) throws SQLException {
    // Unique table names. Either the user supplies a unique identifier as a command
    // line argument, or the program makes one up.
    String tableName = "";
    int sqlCode = 0; // Variable to hold SQLCODE
    String sqlState = "00000"; // Variable to hold SQLSTATE

    if (args.length > 0)
      tableName += args[0];
    else
      tableName += "exampletbl";

    // Register the driver. You must register the driver before you can use it.
    try {
      DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
    } catch (Exception cnfe) {
      System.out.println("Class not found");
    }

    // This is the url you must use for DB2.
    // Note: This url may not valid now ! Check for the correct year and semester
    // and server name.
    String url = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/comp421";

    // REMEMBER to remove your user id and password before submitting your code!!
    String your_userid = null;
    String your_password = null;
    // AS AN ALTERNATIVE, you can just set your password in the shell environment in
    // the Unix (as shown below) and read it from there.
    // $ export SOCSPASSWD=yoursocspasswd
    if (your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null) {
      System.err.println("Error!! do not have a password to connect to the database!");
      System.exit(1);
    }
    if (your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null) {
      System.err.println("Error!! do not have a password to connect to the database!");
      System.exit(1);
    }

    Connection conn = DriverManager.getConnection(url, your_userid, your_password);
    PlaceOrder q1Executor = new PlaceOrder(conn);
    RestaurantAnalytics q2Executor = new RestaurantAnalytics(conn);


    Scanner scanner = new Scanner(System.in);
    int choice;

    do {
      System.out.println("\nSkating Main Menu");
      System.out.println("1. Place on order");
      System.out.println("2. Get a restaurants analytics");
      System.out.println("3. Add a new user");
      System.out.println("4. Add a new restaurant");
      System.out.println("5. Change a restaurant's menu");
      System.out.println("6. Quit");
      System.out.print("Please Enter Your Option: ");

      // Check if the input is a valid integer
      while (!scanner.hasNextInt()) {
        System.out.println("Invalid input. Please enter a number.");
        scanner.next(); // Consume the invalid input
      }

      choice = scanner.nextInt();

      switch (choice) {
        case 1:
          q1Executor.placeOrder();
          break;
        case 2:
          q2Executor.restaurantAnalytics();
          break;
        case 3:
          // Code for competition cancellation
          System.out.println("Competition cancellation...");
          break;
        case 4:
          // Code for adding a new skater
          System.out.println("Adding a new skater...");
          break;
        case 5:
          System.out.println("...");
          break;
        case 6:
          System.out.println("Quitting the program...");
          break;
        
        default:
          System.out.println("Invalid option. Please try again.");
          break;
      }
    } while (choice != 6);

    scanner.close();


}
