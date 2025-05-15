package VRS;

import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        ConnectionClass connectionTest = new ConnectionClass();
        if (connectionTest.con != null) {
            System.out.println("Test passed: Connection established.");
        } else {
            System.out.println("Test failed: No connection.");
        }
    }
}