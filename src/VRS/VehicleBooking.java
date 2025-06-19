package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VehicleBooking extends JFrame {

    public static JTable vehicleTable;
    private JTextField brandField, modelField, yearField, capacityField, minRateField, maxRateField;
    private JComboBox<String> transmissionCombo, fuelTypeCombo, typeIDCombo;
    private JButton searchButton, proceedButton;
    private int selectedVehicleID = -1;
    private int customerID = 1; // Replace with logged-in customer ID if applicable

    public VehicleBooking() {
        setTitle("Vehicle Filter & Booking");
        setSize(1100, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top Panel for Filters
        JPanel filterPanel = new JPanel(new GridLayout(3, 6, 10, 10));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        brandField = new JTextField();
        modelField = new JTextField();
        yearField = new JTextField();
        capacityField = new JTextField();
        minRateField = new JTextField();
        maxRateField = new JTextField();

        transmissionCombo = new JComboBox<>(new String[]{"Any", "Automatic", "Manual"});
        fuelTypeCombo = new JComboBox<>(new String[]{"Any", "1", "2", "3"});  // Adjust if needed
        typeIDCombo = new JComboBox<>(new String[]{"Any", "1", "2", "3"});    // Adjust if needed

        filterPanel.add(new JLabel("Brand:"));        filterPanel.add(brandField);
        filterPanel.add(new JLabel("Model:"));        filterPanel.add(modelField);
        filterPanel.add(new JLabel("Make Year:"));    filterPanel.add(yearField);
        filterPanel.add(new JLabel("Seating Capacity:")); filterPanel.add(capacityField);
        filterPanel.add(new JLabel("Transmission:")); filterPanel.add(transmissionCombo);
        filterPanel.add(new JLabel("Fuel Type ID:")); filterPanel.add(fuelTypeCombo);
        filterPanel.add(new JLabel("Type ID:"));      filterPanel.add(typeIDCombo);
        filterPanel.add(new JLabel("Min Rate:"));     filterPanel.add(minRateField);
        filterPanel.add(new JLabel("Max Rate:"));     filterPanel.add(maxRateField);

        searchButton = new JButton("🔍 Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Vehicle Table
        vehicleTable = new JTable(new DefaultTableModel(new Object[]{
                "VehicleID", "Brand", "Model", "MakeYear", "SeatingCapacity",
                "TransmissionType", "TypeID", "FuelTypeID", "Rate"
        }, 0));
        add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        // Bottom Panel with Proceed Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        proceedButton = new JButton("Proceed to Booking");
        proceedButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        proceedButton.setEnabled(false);
        bottomPanel.add(proceedButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        searchButton.addActionListener(e -> filterVehicles());

        vehicleTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = vehicleTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedVehicleID = (int) vehicleTable.getValueAt(selectedRow, 0);
                    proceedButton.setEnabled(true);
                }
            }
        });

        proceedButton.addActionListener(e -> {
            if (selectedVehicleID != -1) {
                new BookingDetails(customerID, selectedVehicleID);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a vehicle first.");
            }
        });

        setVisible(true);
    }

    private void filterVehicles() {
        Connection conn = ConnectionClass.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            return;
        }

        String sql = "{call FilterVehicles(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            // Read filters
            stmt.setString(1, getNullable(brandField.getText()));
            stmt.setString(2, getNullable(modelField.getText()));
            stmt.setObject(3, parseInteger(yearField.getText()), Types.INTEGER);
            stmt.setObject(4, parseInteger(capacityField.getText()), Types.INTEGER);
            stmt.setString(5, transmissionCombo.getSelectedItem().toString().equals("Any") ? null : transmissionCombo.getSelectedItem().toString());
            stmt.setObject(6, parseIntegerCombo(typeIDCombo), Types.INTEGER);
            stmt.setObject(7, parseIntegerCombo(fuelTypeCombo), Types.INTEGER);
            stmt.setObject(8, parseInteger(minRateField.getText()), Types.INTEGER);
            stmt.setObject(9, parseInteger(maxRateField.getText()), Types.INTEGER);

            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("VehicleID"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getInt("MakeYear"),
                        rs.getInt("SeatingCapacity"),
                        rs.getString("TransmissionType"),
                        rs.getInt("TypeID"),
                        rs.getInt("FuelTypeID"),
                        rs.getInt("Rate")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No vehicles found.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + ex.getMessage());
        }
    }

    private String getNullable(String text) {
        return text.trim().isEmpty() ? null : text.trim();
    }

    private Integer parseInteger(String text) {
        try {
            return text.trim().isEmpty() ? null : Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseIntegerCombo(JComboBox<String> comboBox) {
        String value = comboBox.getSelectedItem().toString();
        return value.equals("Any") ? null : Integer.parseInt(value);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VehicleBooking::new);
    }
}
