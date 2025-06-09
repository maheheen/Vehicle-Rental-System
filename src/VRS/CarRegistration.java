package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CarRegistration extends JFrame implements ActionListener {

    JTextField regNoField, brandField, modelField, yearField, seatingField, rateField;
    JComboBox<String> transmissionBox, fuelTypeComboBox, vehicleTypeComboBox, availabilityBox;
    JTable carTable;
    DefaultTableModel tableModel;
    JButton addButton, editButton, deleteButton, cancelButton, backButton, updateButton;

    public CarRegistration() {
        setTitle("Car Register");
        setSize(850, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(178, 172, 136, 80));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(20, 20, 380, 470);
        formPanel.setBackground(Color.LIGHT_GRAY);

        formPanel.add(new JLabel("Car Reg No")).setBounds(20, 20, 100, 25);
        regNoField = new JTextField();
        regNoField.setBounds(150, 20, 200, 25);
        formPanel.add(regNoField);

        formPanel.add(new JLabel("Brand")).setBounds(20, 50, 100, 25);
        brandField = new JTextField();
        brandField.setBounds(150, 50, 200, 25);
        formPanel.add(brandField);

        formPanel.add(new JLabel("Model")).setBounds(20, 110, 100, 25);
        modelField = new JTextField();
        modelField.setBounds(150, 110, 200, 25);
        formPanel.add(modelField);

        formPanel.add(new JLabel("Year")).setBounds(20, 140, 100, 25);
        yearField = new JTextField();
        yearField.setBounds(150, 140, 200, 25);
        formPanel.add(yearField);

        formPanel.add(new JLabel("Seating Capacity")).setBounds(20, 170, 120, 25);
        seatingField = new JTextField();
        seatingField.setBounds(150, 170, 200, 25);
        formPanel.add(seatingField);

        formPanel.add(new JLabel("Transmission")).setBounds(20, 200, 100, 25);
        transmissionBox = new JComboBox<>(new String[]{"Automatic", "Manual"});
        transmissionBox.setBounds(150, 200, 200, 25);
        formPanel.add(transmissionBox);

        formPanel.add(new JLabel("Fuel Type")).setBounds(20, 230, 100, 25);
        fuelTypeComboBox = new JComboBox<>();
        fuelTypeComboBox.setBounds(150, 230, 200, 25);
        formPanel.add(fuelTypeComboBox);

        formPanel.add(new JLabel("Vehicle Type")).setBounds(20, 260, 120, 25);
        vehicleTypeComboBox = new JComboBox<>();
        vehicleTypeComboBox.setBounds(150, 260, 200, 25);
        formPanel.add(vehicleTypeComboBox);

        formPanel.add(new JLabel("Rate")).setBounds(20, 290, 100, 25);
        rateField = new JTextField();
        rateField.setBounds(150, 290, 200, 25);
        formPanel.add(rateField);

        formPanel.add(new JLabel("Available")).setBounds(20, 320, 100, 25);
        availabilityBox = new JComboBox<>(new String[]{"Yes", "No"});
        availabilityBox.setBounds(150, 320, 200, 25);
        formPanel.add(availabilityBox);

        // Buttons
        addButton = new JButton("Add");
        addButton.setBounds(20, 380, 70, 30);
        formPanel.add(addButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(100, 380, 80, 30);
        formPanel.add(cancelButton);

        editButton = new JButton("Edit");
        editButton.setBounds(190, 380, 70, 30);
        formPanel.add(editButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(270, 380, 75, 30);
        formPanel.add(deleteButton);

        updateButton = new JButton("Update");
        updateButton.setBounds(150, 420, 80, 30);
        formPanel.add(updateButton);

        backButton = new JButton("Back");
        backButton.setBounds(250, 420, 80, 30);
        formPanel.add(backButton);

        add(formPanel);

        tableModel = new DefaultTableModel(new String[]{
                "RegNo", "Brand", "Model", "Year", "Seating", "Transmission",
                "FuelTypeID", "VehicleTypeID", "Rate", "Available"
        }, 0);
        carTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(carTable);
        tableScroll.setBounds(420, 20, 400, 500);
        add(tableScroll);

        loadComboBoxes();
        loadVehiclesIntoTable();

        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        cancelButton.addActionListener(this);
        backButton.addActionListener(this);
        updateButton.addActionListener(this);
    }

    private void loadComboBoxes() {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection conn = connectionClass.con;

        try {
            Statement stmt = conn.createStatement();

            // Load Fuel Types
            ResultSet rs = stmt.executeQuery("SELECT FuelTypeID, FuelName FROM FuelType");
            Vector<String> fuelTypes = new Vector<>();
            while (rs.next()) {
                fuelTypes.add(rs.getString("FuelName"));
            }
            fuelTypeComboBox.setModel(new DefaultComboBoxModel<>(fuelTypes));
            rs.close();

            // Load Vehicle Types
            rs = stmt.executeQuery("SELECT TypeID, TypeName FROM VehicleType");
            Vector<String> vehicleTypes = new Vector<>();
            while (rs.next()) {
                vehicleTypes.add(rs.getString("TypeName"));
            }
            vehicleTypeComboBox.setModel(new DefaultComboBoxModel<>(vehicleTypes));
            rs.close();

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load FuelType or VehicleType data from DB");
        }
    }

    private void loadVehiclesIntoTable() {
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection conn = connectionClass.con;
            String sql = "SELECT RegNumber, Brand, Model, MakeYear, SeatingCapacity, TransmissionType, FuelTypeID, TypeID, Rate, Available FROM Vehicle";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            // Clear existing rows
            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("RegNumber"));
                row.add(rs.getString("Brand"));
                row.add(rs.getString("Model"));
                row.add(rs.getInt("MakeYear"));
                row.add(rs.getInt("SeatingCapacity"));
                row.add(rs.getString("TransmissionType"));
                row.add(rs.getInt("FuelTypeID"));
                row.add(rs.getInt("TypeID"));
                row.add(rs.getDouble("Rate"));
                row.add(rs.getBoolean("Available") ? "Yes" : "No");

                tableModel.addRow(row);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load vehicles from database.");
        }
    }

    private void saveVehicleToDB(String regNo, String brand, String model,
                                 String year, String seating, String transmission,
                                 int fuelTypeID, int vehicleTypeID, String rate, String available) {

        String sql = "INSERT INTO Vehicle (RegNumber, Brand, Model, MakeYear, SeatingCapacity, TransmissionType, FuelTypeID, TypeID, Rate, Available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection conn = connectionClass.con;
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, regNo);
            pstmt.setString(2, brand);
            pstmt.setString(3, model);
            pstmt.setInt(4, Integer.parseInt(year));
            pstmt.setInt(5, Integer.parseInt(seating));
            pstmt.setString(6, transmission);
            pstmt.setInt(7, fuelTypeID);
            pstmt.setInt(8, vehicleTypeID);
            pstmt.setDouble(9, Double.parseDouble(rate));

            pstmt.setBoolean(10, available.equalsIgnoreCase("Yes"));

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Vehicle saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save vehicle in DB\n" + e.getMessage());
        }
    }

    private void updateVehicleInDB(String regNo, String brand, String model,
                                   String year, String seating, String transmission,
                                   int fuelTypeID, int vehicleTypeID, String rate, String available) {
        String sql = "UPDATE Vehicle SET Brand=?, Model=?, MakeYear=?, SeatingCapacity=?, TransmissionType=?, FuelTypeID=?, TypeID=?, Rate=?, Available=? WHERE RegNumber=?";

        try {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection conn = connectionClass.con;
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, brand);
            pstmt.setString(2, model);
            pstmt.setInt(3, Integer.parseInt(year));
            pstmt.setInt(4, Integer.parseInt(seating));
            pstmt.setString(5, transmission);
            pstmt.setInt(6, fuelTypeID);
            pstmt.setInt(7, vehicleTypeID);
            pstmt.setDouble(8, Double.parseDouble(rate));
            pstmt.setBoolean(9, available.equalsIgnoreCase("Yes"));
            pstmt.setString(10, regNo);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Vehicle updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Vehicle update failed or not found.");
            }

            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update vehicle in DB\n" + e.getMessage());
        }
    }

    private void clearForm() {
        regNoField.setText("");
        brandField.setText("");
        modelField.setText("");
        yearField.setText("");
        seatingField.setText("");
        rateField.setText("");
        transmissionBox.setSelectedIndex(0);
        fuelTypeComboBox.setSelectedIndex(0);
        vehicleTypeComboBox.setSelectedIndex(0);
        availabilityBox.setSelectedIndex(0);
    }

    private void deleteVehicleFromDB(String regNo) {
        String sql = "DELETE FROM Vehicle WHERE RegNumber = ?";

        try {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection conn = connectionClass.con;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, regNo);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No vehicle found with that registration number.");
            }

            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete vehicle from DB\n" + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String regNo = regNoField.getText().trim();
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            String year = yearField.getText().trim();
            String seating = seatingField.getText().trim();
            String transmission = (String) transmissionBox.getSelectedItem();
            int fuelTypeID = fuelTypeComboBox.getSelectedIndex() + 1;
            int vehicleTypeID = vehicleTypeComboBox.getSelectedIndex() + 1;
            String rate = rateField.getText().trim();
            String available = (String) availabilityBox.getSelectedItem();

            if (regNo.isEmpty() || brand.isEmpty() || model.isEmpty() || year.isEmpty()
                    || seating.isEmpty() || rate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            saveVehicleToDB(regNo, brand, model, year, seating, transmission,
                    fuelTypeID, vehicleTypeID, rate, available);

            Vector<Object> row = new Vector<>();
            row.add(regNo);
            row.add(brand);
            row.add(model);
            row.add(Integer.parseInt(year));
            row.add(Integer.parseInt(seating));
            row.add(transmission);
            row.add(fuelTypeID);
            row.add(vehicleTypeID);
            row.add(Double.parseDouble(rate));
            row.add(available);

            tableModel.addRow(row);
            clearForm();

        } else if (e.getSource() == editButton) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow >= 0) {
                regNoField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                brandField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                modelField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                yearField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                seatingField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                transmissionBox.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
                fuelTypeComboBox.setSelectedIndex(Integer.parseInt(tableModel.getValueAt(selectedRow, 6).toString()) - 1);
                vehicleTypeComboBox.setSelectedIndex(Integer.parseInt(tableModel.getValueAt(selectedRow, 7).toString()) - 1);
                rateField.setText(tableModel.getValueAt(selectedRow, 8).toString());
                availabilityBox.setSelectedItem(tableModel.getValueAt(selectedRow, 9).toString());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a vehicle to edit.");
            }

        } else if (e.getSource() == updateButton) {
            String regNo = regNoField.getText().trim();
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            String year = yearField.getText().trim();
            String seating = seatingField.getText().trim();
            String transmission = (String) transmissionBox.getSelectedItem();
            int fuelTypeID = fuelTypeComboBox.getSelectedIndex() + 1;
            int vehicleTypeID = vehicleTypeComboBox.getSelectedIndex() + 1;
            String rate = rateField.getText().trim();
            String available = (String) availabilityBox.getSelectedItem();

            if (regNo.isEmpty() || brand.isEmpty() || model.isEmpty() || year.isEmpty()
                    || seating.isEmpty() || rate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            updateVehicleInDB(regNo, brand, model, year, seating, transmission,
                    fuelTypeID, vehicleTypeID, rate, available);

            int selectedRow = carTable.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.setValueAt(regNo, selectedRow, 0);
                tableModel.setValueAt(brand, selectedRow, 1);
                tableModel.setValueAt(model, selectedRow, 2);
                tableModel.setValueAt(Integer.parseInt(year), selectedRow, 3);
                tableModel.setValueAt(Integer.parseInt(seating), selectedRow, 4);
                tableModel.setValueAt(transmission, selectedRow, 5);
                tableModel.setValueAt(fuelTypeID, selectedRow, 6);
                tableModel.setValueAt(vehicleTypeID, selectedRow, 7);
                tableModel.setValueAt(Double.parseDouble(rate), selectedRow, 8);
                tableModel.setValueAt(available, selectedRow, 9);
            }
            clearForm();

        } else if (e.getSource() == deleteButton) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow >= 0) {
                String regNo = tableModel.getValueAt(selectedRow, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this vehicle?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteVehicleFromDB(regNo);
                    tableModel.removeRow(selectedRow);
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a vehicle to delete.");
            }
        } else if (e.getSource() == cancelButton) {
            clearForm();
        } else if (e.getSource() == backButton) {
            this.setVisible(false);
            new AdminPortal().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CarRegistration().setVisible(true);
        });
    }
}
