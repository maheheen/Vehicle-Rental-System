package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VehicleBooking extends JFrame {

    public static JTable vehicleTable;
    private JComboBox<String> brandField, modelField;
    private JTextField yearField, capacityField, minRateField, maxRateField;
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

        brandField = new JComboBox<>();
        modelField = new JComboBox<>();
        yearField = new JTextField();
        capacityField = new JTextField();
        minRateField = new JTextField();
        maxRateField = new JTextField();

        transmissionCombo = new JComboBox<>(new String[]{"Any", "Automatic", "Manual"});
        fuelTypeCombo = new JComboBox<>();
        typeIDCombo = new JComboBox<>();

        filterPanel.add(new JLabel("Brand:"));        filterPanel.add(brandField);
        filterPanel.add(new JLabel("Model:"));        filterPanel.add(modelField);
        filterPanel.add(new JLabel("Make Year:"));    filterPanel.add(yearField);
        filterPanel.add(new JLabel("Seating Capacity:")); filterPanel.add(capacityField);
        filterPanel.add(new JLabel("Transmission:")); filterPanel.add(transmissionCombo);
        filterPanel.add(new JLabel("Fuel Type:"));    filterPanel.add(fuelTypeCombo);
        filterPanel.add(new JLabel("Vehicle Type:")); filterPanel.add(typeIDCombo);
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
                "TransmissionType", "VehicleType", "FuelType", "Rate"
        }, 0));
        add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        // Bottom Panel with Proceed Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        proceedButton = new JButton("Proceed to Booking");
        proceedButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        proceedButton.setEnabled(false);
        bottomPanel.add(proceedButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
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
                new BookingDetails(customerID, selectedVehicleID); // ✅ Go to BookingDetails instead
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a vehicle first.");
            }
        });



        loadDropdowns();
        setVisible(true);
    }

    private void loadDropdowns() {
        brandField.addItem("Any");
        modelField.addItem("Any");
        fuelTypeCombo.addItem("Any");
        typeIDCombo.addItem("Any");

        try (Connection conn = ConnectionClass.getConnection()) {
            Statement stmt = conn.createStatement();

            // Brands
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT Brand FROM Vehicle ORDER BY Brand");
            while (rs.next()) {
                brandField.addItem(rs.getString("Brand"));
            }

            // Fuel Types
            rs = stmt.executeQuery("SELECT FuelTypeID, FuelName FROM FuelType ORDER BY FuelName");
            while (rs.next()) {
                fuelTypeCombo.addItem(rs.getInt("FuelTypeID") + " - " + rs.getString("FuelName"));
            }

            // Vehicle Types
            rs = stmt.executeQuery("SELECT TypeID, TypeName FROM VehicleType ORDER BY TypeName");
            while (rs.next()) {
                typeIDCombo.addItem(rs.getInt("TypeID") + " - " + rs.getString("TypeName"));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        brandField.addActionListener(e -> {
            String brand = brandField.getSelectedItem().toString();
            updateModelDropdown(brand.equals("Any") ? null : brand);
        });

        updateModelDropdown(null);
    }

    private void updateModelDropdown(String brandFilter) {
        modelField.removeAllItems();
        modelField.addItem("Any");

        try (Connection conn = ConnectionClass.getConnection()) {
            String sql = "SELECT DISTINCT Model FROM Vehicle" + (brandFilter != null ? " WHERE Brand = ?" : "");
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (brandFilter != null) stmt.setString(1, brandFilter);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modelField.addItem(rs.getString("Model"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterVehicles() {
        try (Connection conn = ConnectionClass.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!");
                return;
            }

            String sql = "SELECT v.VehicleID, v.Brand, v.Model, v.MakeYear, v.SeatingCapacity, " +
                    "v.TransmissionType, vt.TypeName, ft.FuelName, v.Rate " +
                    "FROM Vehicle v " +
                    "LEFT JOIN FuelType ft ON v.FuelTypeID = ft.FuelTypeID " +
                    "LEFT JOIN VehicleType vt ON v.TypeID = vt.TypeID " +
                    "WHERE (? IS NULL OR v.Brand = ?) " +
                    "AND (? IS NULL OR v.Model = ?) " +
                    "AND (? IS NULL OR v.MakeYear = ?) " +
                    "AND (? IS NULL OR v.SeatingCapacity = ?) " +
                    "AND (? IS NULL OR v.TransmissionType = ?) " +
                    "AND (? IS NULL OR v.TypeID = ?) " +
                    "AND (? IS NULL OR v.FuelTypeID = ?) " +
                    "AND (? IS NULL OR v.Rate >= ?) " +
                    "AND (? IS NULL OR v.Rate <= ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, getNullableCombo(brandField));
                stmt.setString(2, getNullableCombo(brandField));

                stmt.setString(3, getNullableCombo(modelField));
                stmt.setString(4, getNullableCombo(modelField));

                setNullableInt(stmt, 5, yearField.getText());
                setNullableInt(stmt, 6, yearField.getText());

                setNullableInt(stmt, 7, capacityField.getText());
                setNullableInt(stmt, 8, capacityField.getText());

                String transmission = transmissionCombo.getSelectedItem().toString();
                stmt.setString(9, transmission.equals("Any") ? null : transmission);
                stmt.setString(10, transmission.equals("Any") ? null : transmission);

                setNullableComboID(stmt, 11, typeIDCombo);
                setNullableComboID(stmt, 12, typeIDCombo);

                setNullableComboID(stmt, 13, fuelTypeCombo);
                setNullableComboID(stmt, 14, fuelTypeCombo);

                setNullableInt(stmt, 15, minRateField.getText());
                setNullableInt(stmt, 16, minRateField.getText());

                setNullableInt(stmt, 17, maxRateField.getText());
                setNullableInt(stmt, 18, maxRateField.getText());

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
                            rs.getString("TypeName"),
                            rs.getString("FuelName"),
                            rs.getInt("Rate")
                    });
                }

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No vehicles found.");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + ex.getMessage());
        }
    }

    private void setNullableInt(PreparedStatement stmt, int index, String text) throws SQLException {
        if (text.trim().isEmpty()) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, Integer.parseInt(text.trim()));
        }
    }

    private void setNullableComboID(PreparedStatement stmt, int index, JComboBox<String> comboBox) throws SQLException {
        String value = comboBox.getSelectedItem().toString();
        if (value.equals("Any")) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            int id = Integer.parseInt(value.split(" - ")[0]);
            stmt.setInt(index, id);
        }
    }

    private String getNullableCombo(JComboBox<String> comboBox) {
        String val = comboBox.getSelectedItem().toString();
        return val.equals("Any") ? null : val;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VehicleBooking::new);
    }
}
