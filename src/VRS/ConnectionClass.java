package VRS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {
    public Connection con;

    public ConnectionClass() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=VehicleRentalSystem_Edited;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "dblab";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to SQL Server successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("SQL Server Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }

}