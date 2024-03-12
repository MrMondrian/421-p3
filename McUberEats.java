import java.sql.*;
import java.util.Scanner;

class McUberEats {


  public static void main(String[] args) throws SQLException {
    // Unique table names. Either the user supplies a unique identifier as a command
    // line argument, or the program makes one up.
    int sqlCode = 0; // Variable to hold SQLCODE
    String sqlState = "00000"; // Variable to hold SQLSTATE

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
    
    Scanner scanner = new Scanner(System.in);
    System.out.println("What is your userid for DB2?");
    your_userid = scanner.nextLine().strip();
    System.out.println("What is your password for DB2?");
    your_password = scanner.nextLine().strip();

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
    UserAnalytics q3Executor = new UserAnalytics(conn);
    InsertUser q4Executor = new InsertUser(conn);
    InsertRestaurant q5Executor = new InsertRestaurant(conn);


    int choice;

    do {
      System.out.println("\nMcUber Eat options:");
      System.out.println("1. Place on order");
      System.out.println("2. Get a Restaurant's analytics");
      System.out.println("3. Get a User's analytics");
      System.out.println("4. Add a new user");
      System.out.println("5. Add a new restaurant");
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
          q3Executor.getUserAnalytics();
          break;
        case 4:
          q4Executor.insertUser();          
          break;
        case 5:
          q5Executor.insertRestaurant();
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
}
