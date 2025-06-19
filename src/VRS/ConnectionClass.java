package VRS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=VehicleRentalSystem;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "dblab";
    private static Connection connection;

    // Static method to get the connection
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to SQL Server successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("❌ SQL Server JDBC Driver not found.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Connection to SQL Server failed.");
                e.printStackTrace();
            }
        }
        return connection;
    }
}
