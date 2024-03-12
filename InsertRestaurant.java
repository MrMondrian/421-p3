import java.sql.*;
import java.util.Scanner;

public class InsertRestaurant {

    private Connection conn;

    public InsertRestaurant(Connection c) {
        conn = c;
    }

    public void insertRestaurant() throws SQLException {

        Scanner scanner = new Scanner(System.in);

        // Prompt for restaurant information
        System.out.print("Enter restaurant name: ");
        String name = scanner.nextLine();

        System.out.print("Enter restaurant address: ");
        String address = scanner.nextLine();

        System.out.print("Enter restaurant rating (0-5): ");
        float rating = scanner.nextFloat();
        scanner.nextLine(); // Consume newline

        // Insert restaurant record
        int restaurantId = insertRestaurant(name, address, rating);

        // Prompt for menu items
        System.out.println("Enter menu items (enter 'done' to finish):");
        String itemName;
        float itemCost;
        while (true) {
            System.out.print("Item name: ");
            itemName = scanner.nextLine();
            if (itemName.equalsIgnoreCase("done")) {
                break;
            }
            System.out.print("Item cost: ");
            itemCost = scanner.nextFloat();
            scanner.nextLine(); // Consume newline
            insertMenuItem(restaurantId, itemName, itemCost);
        }

        System.out.println("Restaurant added successfully!");

    }

    private int insertRestaurant(String name, String address, float rating) throws SQLException {
        String query = "INSERT INTO Restaurant (Name, Address, Rating) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setFloat(3, rating);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private void insertMenuItem(int restaurantId, String itemName, float itemCost) throws SQLException {
        String query = "INSERT INTO Item (Name, RID, Cost) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, itemName);
            stmt.setInt(2, restaurantId);
            stmt.setFloat(3, itemCost);
            stmt.executeUpdate();
        }
    }
}