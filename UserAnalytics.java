import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserAnalytics {
    private Connection conn;

    public UserAnalytics(Connection c) {
        conn = c;
    }

    public void getUserAnalytics() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Prompt for user email
        System.out.print("Enter user email: ");
        String userEmail = scanner.nextLine();

        // Check if the user is a customer or a driver
        boolean isCustomer = isCustomer(userEmail);
        boolean isDriver = isDriver(userEmail);

        if (isCustomer) {
            System.out.println("\nCustomer Analytics for " + userEmail + ":");
            getCustomerAnalytics(userEmail);
        } else if (isDriver) {
            System.out.println("\nDriver Analytics for " + userEmail + ":");
            getDriverAnalytics(userEmail);
        } else {
            System.out.println("User not found or not a customer or driver.");
        }
    }

    private boolean isCustomer(String userEmail) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Customer WHERE CustomerEmail = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    private boolean isDriver(String userEmail) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Driver WHERE DriverEmail = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    private void getCustomerAnalytics(String userEmail) throws SQLException {
        // Get total orders placed by the customer
        int totalOrders = getTotalOrdersForCustomer(userEmail);
        System.out.println("Total Orders: " + totalOrders);

        // Get average order cost for the customer
        double avgOrderCost = getAverageOrderCostForCustomer(userEmail);
        System.out.println("Average Order Cost: $" + avgOrderCost);

        // Get most visited restaurants for the customer
        System.out.println("Most Visited Restaurants:");
        getMostVisitedRestaurantsForCustomer(userEmail);
    }

    private void getDriverAnalytics(String userEmail) throws SQLException {
        // Get total trips made by the driver
        int totalTrips = getTotalTripsForDriver(userEmail);
        System.out.println("Total Trips: " + totalTrips);

        // Get average rating for the driver
        float avgRating = getAverageRatingForDriver(userEmail);
        System.out.println("Average Rating: " + avgRating);

        // Get most delivered restaurants for the driver
        System.out.println("Most Delivered Restaurants:");
        getMostDeliveredRestaurantsForDriver(userEmail);
    }

    private int getTotalOrdersForCustomer(String userEmail) throws SQLException {
        String query = "SELECT COUNT(*) AS total_orders FROM Order WHERE CustomerEmail = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_orders");
            }
        }
        return 0;
    }

    private double getAverageOrderCostForCustomer(String userEmail) throws SQLException {
        String query = "SELECT AVG(Cost) AS avg_cost FROM Order WHERE CustomerEmail = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_cost");
            }
        }
        return 0.0;
    }

    private void getMostVisitedRestaurantsForCustomer(String userEmail) throws SQLException {
        String query = "SELECT r.Name, COUNT(*) AS visit_count " +
                       "FROM Order o " +
                       "JOIN Restaurant r ON o.RID = r.RID " +
                       "WHERE o.CustomerEmail = ? " +
                       "GROUP BY r.Name " +
                       "ORDER BY visit_count DESC " +
                       "LIMIT 5";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String restaurantName = rs.getString("Name");
                int visitCount = rs.getInt("visit_count");
                System.out.println("- " + restaurantName + ": " + visitCount + " visits");
            }
        }
    }

    private int getTotalTripsForDriver(String userEmail) throws SQLException {
        String query = "SELECT NumberTrips FROM Driver WHERE DriverEmail = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("NumberTrips");
            }
        }
        return 0;
    }

    private float getAverageRatingForDriver(String userEmail) throws SQLException {
        String query = "SELECT AVG(DriverRating) AS avg_rating FROM Order WHERE DriverEmail = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getFloat("avg_rating");
            }
        }
        return 0.0f;
    }

    private void getMostDeliveredRestaurantsForDriver(String userEmail) throws SQLException {
        String query = "SELECT r.Name, COUNT(*) AS delivery_count " +
                       "FROM Order o " +
                       "JOIN Restaurant r ON o.RID = r.RID " +
                       "WHERE o.DriverEmail = ? " +
                       "GROUP BY r.Name " +
                       "ORDER BY delivery_count DESC " +
                       "LIMIT 5";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String restaurantName = rs.getString("Name");
                int deliveryCount = rs.getInt("delivery_count");
                System.out.println("- " + restaurantName + ": " + deliveryCount + " deliveries");
            }
        }
    }
}