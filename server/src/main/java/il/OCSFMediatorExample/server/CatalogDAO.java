package il.OCSFMediatorExample.server;

import il.OCSFMediatorExample.entities.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogDAO {

    public static List<Item> getAllItems() {
        List<Item> catalog = new ArrayList<>();
        String query = "SELECT id, name, category, price, description, image_data FROM catalog";

        try (
                Connection conn = DBConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                catalog.add(new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getBytes("image_data")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching all items: " + e.getMessage());
            e.printStackTrace();
        }

        return catalog;
    }

    public static boolean updateItemPrice(int itemId, double newPrice) {
        String query = "UPDATE catalog SET price = ? WHERE id = ?";
        try (
                Connection conn = DBConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, itemId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error updating item price for ID " + itemId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
