import java.sql.*;
import java.util.Scanner;

public class InsertUser {

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Scanner scanner = new Scanner(System.in)) {

            // Prompt for user information
            System.out.print("Enter user email: ");
            String userEmail = scanner.nextLine();

            System.out.print("Enter user name: ");
            String userName = scanner.nextLine();

            System.out.print("Enter user phone number: ");
            String phoneNumber = scanner.nextLine();

            // Insert user record
            insertUser(conn, userEmail, userName, phoneNumber);

            // Prompt for additional user information
            System.out.print("Is the user a customer? (y/n): ");
            String isCustomer = scanner.nextLine();
            if (isCustomer.equalsIgnoreCase("y")) {
                System.out.print("Enter customer address: ");
                String address = scanner.nextLine();
                insertCustomer(conn, userEmail, address);
            }

            System.out.print("Is the user a driver? (y/n): ");
            String isDriver = scanner.nextLine();
            if (isDriver.equalsIgnoreCase("y")) {
                System.out.print("Enter driver rating (0-5): ");
                float rating = scanner.nextFloat();
                scanner.nextLine(); // Consume newline

                System.out.print("Enter number of trips: ");
                int numTrips = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                System.out.print("Enter join date (YYYY-MM-DD): ");
                String joinDateStr = scanner.nextLine();
                Date joinDate = Date.valueOf(joinDateStr);

                System.out.print("Enter car license plate: ");
                String licensePlate = scanner.nextLine();

                System.out.print("Enter car type: ");
                String carType = scanner.nextLine();

                insertDriver(conn, userEmail, rating, numTrips, joinDate, licensePlate, carType);
            }

            System.out.println("User added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertUser(Connection conn, String userEmail, String userName, String phoneNumber) throws SQLException {
        String query = "INSERT INTO User (UserEmail, Name, PhoneNumber) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            stmt.setString(2, userName);
            stmt.setString(3, phoneNumber);
            stmt.executeUpdate();
        }
    }

    private static void insertCustomer(Connection conn, String userEmail, String address) throws SQLException {
        String query = "INSERT INTO Customer (CustomerEmail, Address) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            stmt.setString(2, address);
            stmt.executeUpdate();
        }
    }

    private static void insertDriver(Connection conn, String userEmail, float rating, int numTrips, Date joinDate,
                                     String licensePlate, String carType) throws SQLException {
        String query = "INSERT INTO Driver (DriverEmail, Rating, NumberTrips, JoinDate, CarLicensePlate, CarType) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            stmt.setFloat(2, rating);
            stmt.setInt(3, numTrips);
            stmt.setDate(4, joinDate);
            stmt.setString(5, licensePlate);
            stmt.setString(6, carType);
            stmt.executeUpdate();
        }
    }
}