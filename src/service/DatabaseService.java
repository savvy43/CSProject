package service;

import model.Room;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseService {
    
    private static final String DB_URL = "jdbc:sqlite:hotel.db";
    private Connection connection;

    public DatabaseService() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS rooms (
                room_number INTEGER PRIMARY KEY,
                is_available INTEGER NOT NULL,
                guest_name TEXT,
                cleaning_status TEXT DEFAULT 'Clean'
            )
            """;

        String createBalanceTableSQL = """
            CREATE TABLE IF NOT EXISTS cash_balance (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                balance REAL NOT NULL DEFAULT 1000.0
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            stmt.execute(createBalanceTableSQL);
            
            // Initialize balance if not exists
            String checkBalance = "SELECT COUNT(*) FROM cash_balance WHERE id = 1";
            ResultSet rs = stmt.executeQuery(checkBalance);
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO cash_balance (id, balance) VALUES (1, 1000.0)");
            }
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    public void saveRoomStatus(Room room, boolean isAvailable, String guestName, String cleaningStatus) {
        String sql = """
            INSERT OR REPLACE INTO rooms (room_number, is_available, guest_name, cleaning_status)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, room.getRoomNumber());
            pstmt.setInt(2, isAvailable ? 1 : 0);
            pstmt.setString(3, guestName);
            pstmt.setString(4, cleaningStatus);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving room status: " + e.getMessage());
        }
    }

    public Map<Integer, RoomData> loadAllRoomStatuses() {
        Map<Integer, RoomData> roomDataMap = new HashMap<>();
        String sql = "SELECT room_number, is_available, guest_name, cleaning_status FROM rooms";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                boolean isAvailable = rs.getInt("is_available") == 1;
                String guestName = rs.getString("guest_name");
                String cleaningStatus = rs.getString("cleaning_status");
                
                roomDataMap.put(roomNumber, new RoomData(isAvailable, guestName, cleaningStatus));
            }
        } catch (SQLException e) {
            System.err.println("Error loading room statuses: " + e.getMessage());
        }

        return roomDataMap;
    }

    public double getCashBalance() {
        String sql = "SELECT balance FROM cash_balance WHERE id = 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.err.println("Error getting cash balance: " + e.getMessage());
        }
        return 1000.0; // Default
    }

    public void updateCashBalance(double newBalance) {
        String sql = "UPDATE cash_balance SET balance = ? WHERE id = 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating cash balance: " + e.getMessage());
        }
    }

    public void addToCashBalance(double amount) {
        double currentBalance = getCashBalance();
        updateCashBalance(currentBalance + amount);
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }

    public static class RoomData {
        public boolean isAvailable;
        public String guestName;
        public String cleaningStatus;

        public RoomData(boolean isAvailable, String guestName, String cleaningStatus) {
            this.isAvailable = isAvailable;
            this.guestName = guestName;
            this.cleaningStatus = cleaningStatus;
        }
    }
}
