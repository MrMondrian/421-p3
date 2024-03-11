import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDateTime;

public class PlaceOrder {

    private Connection conn;

    public PlaceOrder(Connection c) {
        conn = c;
    }

    public void placeOrder() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Prompt for customer email
        System.out.print("Enter your email: ");
        String customerEmail = scanner.nextLine();

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

        // Retrieve restaurant name and address
        String restaurantName = restaurants.get(restaurantId);
        String restaurantAddress = getRestaurantAddress(conn, restaurantId);

        // Prompt for destination address
        System.out.print("Enter the delivery destination address: ");
        String destination = scanner.nextLine();

        // Prompt for order items
        System.out.println("Enter order items (enter 'done' to finish):");
        String itemName;
        int quantity;
        Map<String, Integer> orderItems = new HashMap<>();
        while (true) {
            System.out.print("Item name: ");
            itemName = scanner.nextLine();
            if (itemName.equalsIgnoreCase("done")) {
                break;
            }
            System.out.print("Quantity: ");
            quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            orderItems.put(itemName, quantity);
        }

        // Get current timestamp
        LocalDateTime placedDateTime = LocalDateTime.now();

        // Insert order record
        int orderId = insertOrder(conn, placedDateTime, customerEmail, restaurantId, destination);

        // Insert order items
        insertOrderItems(conn, orderId, restaurantId, orderItems);

        System.out.println("Order placed successfully!");
        System.out.println("Restaurant: " + restaurantName + " (" + restaurantAddress + ")");
        System.out.println("Delivery Address: " + destination);
        System.out.println("Order Items:");
        for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue());
        }

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

    private static String getRestaurantAddress(Connection conn, int restaurantId) throws SQLException {
        String query = "SELECT Address FROM Restaurant WHERE RID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, restaurantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Address");
            }
        }
        return null;
    }

    private static int insertOrder(Connection conn, LocalDateTime placedDateTime, String customerEmail,
            int restaurantId, String destination) throws SQLException {
        String query = "INSERT INTO Order (PlacedDateTime, Cost, Destination, CustomerEmail, RID) " +
                "VALUES (?, 0.0, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(placedDateTime));
            stmt.setString(2, destination);
            stmt.setString(3, customerEmail);
            stmt.setInt(4, restaurantId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private static void insertOrderItems(Connection conn, int orderId, int restaurantId,
            Map<String, Integer> orderItems) throws SQLException {
        String query = "INSERT INTO OrderItems (OID, RID, Name, Quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, restaurantId);
                stmt.setString(3, entry.getKey());
                stmt.setInt(4, entry.getValue());
                stmt.executeUpdate();
            }
        }
    }
}