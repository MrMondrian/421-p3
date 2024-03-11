import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RestaurantAnalytics {

    private Connection conn;

    public RestaurantAnalytics(Connection c) {
        conn = c;
    }

    public void restaurantAnalytics() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        // Get all restaurant names and IDs
        Map<Integer, String> restaurants = getRestaurants(conn);

        // Prompt user to select a restaurant
        System.out.println("Select a restaurant:");
        for (Map.Entry<Integer, String> entry : restaurants.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
        System.out.print("Enter restaurant ID: ");
        int restaurantId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Get restaurant analytics
        System.out.println("\nRestaurant Analytics for " + restaurants.get(restaurantId) + ":");
        getRestaurantAnalytics(conn, restaurantId);

        scanner.close();
    }

    private static Map<Integer, String> getRestaurants(Connection conn) throws SQLException {
        String query = "SELECT RID, Name FROM Restaurant";
        Map<Integer, String> restaurants = new HashMap<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int rid = rs.getInt("RID");
                String name = rs.getString("Name");
                restaurants.put(rid, name);
            }
        }
        return restaurants;
    }

    private static void getRestaurantAnalytics(Connection conn, int restaurantId) throws SQLException {
        // Get total orders for the restaurant
        int totalOrders = getTotalOrders(conn, restaurantId);
        System.out.println("Total Orders: " + totalOrders);

        // Get average order cost for the restaurant
        double avgOrderCost = getAverageOrderCost(conn, restaurantId);
        System.out.println("Average Order Cost: $" + avgOrderCost);

        // Get most popular items for the restaurant
        System.out.println("Most Popular Items:");
        getMostPopularItems(conn, restaurantId);
    }

    private static int getTotalOrders(Connection conn, int restaurantId) throws SQLException {
        String query = "SELECT COUNT(*) AS total_orders FROM Order WHERE RID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, restaurantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_orders");
            }
        }
        return 0;
    }

    private static double getAverageOrderCost(Connection conn, int restaurantId) throws SQLException {
        String query = "SELECT AVG(Cost) AS avg_cost FROM Order WHERE RID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, restaurantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_cost");
            }
        }
        return 0.0;
    }

    private static void getMostPopularItems(Connection conn, int restaurantId) throws SQLException {
        String query = "SELECT i.Name, SUM(oi.Quantity) AS total_quantity " +
                "FROM OrderItems oi " +
                "JOIN Item i ON oi.Name = i.Name AND oi.RID = i.RID " +
                "WHERE oi.RID = ? " +
                "GROUP BY i.Name " +
                "ORDER BY total_quantity DESC " +
                "LIMIT 5";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, restaurantId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String itemName = rs.getString("Name");
                int totalQuantity = rs.getInt("total_quantity");
                System.out.println("- " + itemName + ": " + totalQuantity);
            }
        }
    }
}