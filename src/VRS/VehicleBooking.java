package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VehicleBooking extends JFrame {

    private JTable vehicleTable;
    private JTextField brandField, modelField, yearField, capacityField, minRateField, maxRateField;
    private JComboBox<String> transmissionCombo, fuelTypeCombo, typeIDCombo;
    private JButton searchButton;

    public VehicleBooking() {
        setTitle("Vehicle Filter & Booking");
        setSize(1000, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Filter panel
        JPanel filterPanel = new JPanel(new GridLayout(3, 6, 5, 5));

        brandField = new JTextField();
        modelField = new JTextField();
        yearField = new JTextField();
        capacityField = new JTextField();
        minRateField = new JTextField();
        maxRateField = new JTextField();

        transmissionCombo = new JComboBox<>(new String[]{"Any", "Automatic", "Manual"});
        fuelTypeCombo = new JComboBox<>(new String[]{"Any", "1", "2", "3"});  // Update as per DB
        typeIDCombo = new JComboBox<>(new String[]{"Any", "1", "2", "3"});    // Update as per DB

        filterPanel.add(new JLabel("Brand:"));
        filterPanel.add(brandField);
        filterPanel.add(new JLabel("Model:"));
        filterPanel.add(modelField);
        filterPanel.add(new JLabel("Make Year:"));
        filterPanel.add(yearField);

        filterPanel.add(new JLabel("Seating Capacity:"));
        filterPanel.add(capacityField);
        filterPanel.add(new JLabel("Transmission:"));
        filterPanel.add(transmissionCombo);
        filterPanel.add(new JLabel("FuelTypeID:"));
        filterPanel.add(fuelTypeCombo);

        filterPanel.add(new JLabel("TypeID:"));
        filterPanel.add(typeIDCombo);
        filterPanel.add(new JLabel("Min Rate:"));
        filterPanel.add(minRateField);
        filterPanel.add(new JLabel("Max Rate:"));
        filterPanel.add(maxRateField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> filterVehicles());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Table
        vehicleTable = new JTable(new DefaultTableModel(new Object[]{
                "VehicleID", "Brand", "Model", "MakeYear", "SeatingCapacity",
                "TransmissionType", "TypeID", "FuelTypeID", "Rate"
        }, 0));
        add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        setVisible(true);
    }

    private void filterVehicles() {
        ConnectionClass connectionClass= new ConnectionClass();
        Connection conn = connectionClass.con;

        String sql = "{call FilterVehicles(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            // GUI input values
            String brand = brandField.getText().trim();
            String Model = modelField.getText().trim();
            String yearText = yearField.getText().trim();
            String capacityText = capacityField.getText().trim();
            String transmission = transmissionCombo.getSelectedItem().toString();
            String fuelText = fuelTypeCombo.getSelectedItem().toString();
            String typeText = typeIDCombo.getSelectedItem().toString();
            String minRateText = minRateField.getText().trim();
            String maxRateText = maxRateField.getText().trim();

            // Set procedure parameters
            stmt.setString(1, brand.isEmpty() ? null : brand);
            stmt.setString(2, Model.isEmpty() ? null : Model);
            stmt.setObject(3, yearText.isEmpty() ? null : Integer.parseInt(yearText), Types.INTEGER);
            stmt.setObject(4, capacityText.isEmpty() ? null : Integer.parseInt(capacityText), Types.INTEGER);
            stmt.setString(5, transmission.equalsIgnoreCase("Any") ? null : transmission);
            stmt.setObject(6, typeText.equalsIgnoreCase("Any") ? null : Integer.parseInt(typeText), Types.INTEGER);
            stmt.setObject(7, fuelText.equalsIgnoreCase("Any") ? null : Integer.parseInt(fuelText), Types.INTEGER);
            stmt.setObject(8, minRateText.isEmpty() ? null : Integer.parseInt(minRateText), Types.INTEGER);
            stmt.setObject(9, maxRateText.isEmpty() ? null : Integer.parseInt(maxRateText), Types.INTEGER);

            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) vehicleTable.getModel();
            model.setRowCount(0); // Clear old results

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

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VehicleBooking::new);
    }
}