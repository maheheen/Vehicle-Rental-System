package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ViewAvailableVehicles extends JFrame {

    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    public ViewAvailableVehicles() {
        setTitle("Available Vehicles");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Table setup
        String[] columns = {"Vehicle ID", "Brand", "Model", "Year", "Seats", "Transmission", "Rate"};
        tableModel = new DefaultTableModel(columns, 0);
        vehicleTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add back button at the bottom
        JButton backButton = new JButton("Back");
        backButton.addActionListener((ActionEvent e) -> {
            dispose();
           new CustomerPortal().setVisible(true);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load data
        loadAvailableVehicles();

        setVisible(true);
    }

    private void loadAvailableVehicles() {
        Connection conn = ConnectionClass.getConnection();

        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        String query = "SELECT * FROM ViewAvailableVehicles";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("VehicleID"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getInt("MakeYear"),
                        rs.getInt("SeatingCapacity"),
                        rs.getString("TransmissionType"),
                        rs.getInt("Rate")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching vehicles: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewAvailableVehicles::new);
    }
}
