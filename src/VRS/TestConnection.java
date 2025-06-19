package VRS;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        Connection conn = ConnectionClass.getConnection();
        if (conn != null) {
            System.out.println("Test passed: Connection established.");
        } else {
            System.out.println("Test failed: No connection.");
        }
    }
}
