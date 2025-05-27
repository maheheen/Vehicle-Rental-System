import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VehicleBooking extends JFrame {

    JComboBox<String> cbBrand, cbType, cbFuel, cbTrans;
    JTextField txtModel, txtSeats, txtMinRate, txtMaxRate;
    JButton btnSearch, btnBook;
    JTable table;
    DefaultTableModel model;

    public VehicleBooking() {
        setTitle("Vehicle Filter & Booking");
        setSize(905, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Filter Panel
        JPanel filterPanel = new JPanel(new GridLayout(3, 5, 10, 10));
        cbBrand = new JComboBox<>(new String[]{"Any", "Toyota", "Honda", "Kia"});
        cbType = new JComboBox<>(new String[]{"Any", "1", "2", "3"}); // Use actual TypeID
        cbFuel = new JComboBox<>(new String[]{"Any", "1", "2", "3"}); // Use actual FuelTypeID
        cbTrans = new JComboBox<>(new String[]{"Any", "Manual", "Automatic"});
        txtModel = new JTextField();
        txtSeats = new JTextField();
        txtMinRate = new JTextField();
        txtMaxRate = new JTextField();
        btnSearch = new JButton("Search");
        btnBook = new JButton("Book Selected Vehicle");

        filterPanel.add(new JLabel("Brand:")); filterPanel.add(cbBrand);
        filterPanel.add(new JLabel("TypeID:")); filterPanel.add(cbType);
        filterPanel.add(new JLabel("Model:")); filterPanel.add(txtModel);
        filterPanel.add(new JLabel("FuelTypeID:")); filterPanel.add(cbFuel);
        filterPanel.add(new JLabel("Transmission:")); filterPanel.add(cbTrans);
        filterPanel.add(new JLabel("Seating Capacity:")); filterPanel.add(txtSeats);
        filterPanel.add(new JLabel("Min Rate:")); filterPanel.add(txtMinRate);
        filterPanel.add(new JLabel("Max Rate:")); filterPanel.add(txtMaxRate);
        filterPanel.add(btnSearch); filterPanel.add(btnBook);

        add(filterPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        model.setColumnIdentifiers(new String[]{
                "VehicleID", "Brand", "Model", "MakeYear", "SeatingCapacity",
                "TransmissionType", "TypeID", "FuelTypeID", "Rate"
        });

        btnSearch.addActionListener(e -> loadData());
        btnBook.addActionListener(e -> bookSelectedVehicle());

        setVisible(true);
    }

    private void loadData() {
        model.setRowCount(0); // Clear table

        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call FilterVehicles(?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            stmt.setObject(1, getNullable(cbBrand.getSelectedItem().toString()));
            stmt.setObject(2, getNullable(txtModel.getText()));
            stmt.setObject(3, null); // MakeYear - optional
            stmt.setObject(4, parseInt(txtSeats.getText()));
            stmt.setObject(5, getNullable(cbTrans.getSelectedItem().toString()));
            stmt.setObject(6, parseInt(cbType.getSelectedItem().toString()));
            stmt.setObject(7, parseInt(cbFuel.getSelectedItem().toString()));
            stmt.setObject(8, parseInt(txtMinRate.getText()));
            stmt.setObject(9, parseInt(txtMaxRate.getText()));

            ResultSet rs = stmt.executeQuery();
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

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + ex.getMessage());
        }
    }

    private Object getNullable(String val) {
        return val.equals("Any") || val.isBlank() ? null : val;
    }

    private Integer parseInt(String val) {
        try {
            return val.isBlank() ? null : Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void bookSelectedVehicle() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to book.");
            return;
        }

        int vehicleId = (int) model.getValueAt(row, 0);
        int customerId = 1; // Replace with actual logged-in customer ID
        String startDate = JOptionPane.showInputDialog("Enter Start Date (YYYY-MM-DD):");
        String endDate = JOptionPane.showInputDialog("Enter End Date (YYYY-MM-DD):");
        String pickup = JOptionPane.showInputDialog("Pickup Location:");
        String dropoff = JOptionPane.showInputDialog("Dropoff Location:");

        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call CreateRental(?, ?, ?, ?, ?, ?)}");
            stmt.setInt(1, customerId);
            stmt.setInt(2, vehicleId);
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setString(5, pickup);
            stmt.setString(6, dropoff);
            stmt.execute();

            JOptionPane.showMessageDialog(this, "Vehicle booked successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Booking error: " + e.getMessage());
        }
    }

    public class DBConnection {
        public static Connection getConnection() throws Exception {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=VehicleRentalSystem;encrypt=true;trustServerCertificate=true";
            String user = "sa";
            String password = "dblab";
            return DriverManager.getConnection(url, user, password);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(VehicleBooking::new);
    }
}
