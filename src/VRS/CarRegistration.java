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
    JButton addButton, editButton, deleteButton, cancelButton;

    public CarRegistration() {
        setTitle("Car Register");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(178, 172, 136, 80));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(20, 20, 380, 470);
        formPanel.setBackground(Color.LIGHT_GRAY);


        formPanel.add(new JLabel("Car Reg No")).setBounds(20, 20, 100, 25);
        regNoField = new JTextField(); regNoField.setBounds(150, 20, 200, 25); formPanel.add(regNoField);

        formPanel.add(new JLabel("Brand")).setBounds(20, 50, 100, 25);
        brandField = new JTextField(); brandField.setBounds(150, 50, 200, 25); formPanel.add(brandField);

        formPanel.add(new JLabel("Model")).setBounds(20, 110, 100, 25);
        modelField = new JTextField(); modelField.setBounds(150, 110, 200, 25); formPanel.add(modelField);

        formPanel.add(new JLabel("Year")).setBounds(20, 140, 100, 25);
        yearField = new JTextField(); yearField.setBounds(150, 140, 200, 25); formPanel.add(yearField);

        formPanel.add(new JLabel("Seating Capacity")).setBounds(20, 170, 120, 25);
        seatingField = new JTextField(); seatingField.setBounds(150, 170, 200, 25); formPanel.add(seatingField);

        formPanel.add(new JLabel("Transmission")).setBounds(20, 200, 100, 25);
        transmissionBox = new JComboBox<>(new String[]{"Automatic", "Manual"});
        transmissionBox.setBounds(150, 200, 200, 25); formPanel.add(transmissionBox);

        formPanel.add(new JLabel("Fuel Type")).setBounds(20, 230, 100, 25);
        fuelTypeComboBox = new JComboBox<>();
        fuelTypeComboBox.setBounds(150, 230, 200, 25); formPanel.add(fuelTypeComboBox);

        formPanel.add(new JLabel("Vehicle Type")).setBounds(20, 260, 120, 25);
        vehicleTypeComboBox = new JComboBox<>();
        vehicleTypeComboBox.setBounds(150, 260, 200, 25); formPanel.add(vehicleTypeComboBox);

        formPanel.add(new JLabel("Rate")).setBounds(20, 290, 100, 25);
        rateField = new JTextField(); rateField.setBounds(150, 290, 200, 25); formPanel.add(rateField);

        formPanel.add(new JLabel("Available")).setBounds(20, 320, 100, 25);
        availabilityBox = new JComboBox<>(new String[]{"Yes", "No"});
        availabilityBox.setBounds(150, 320, 200, 25); formPanel.add(availabilityBox);

        // Buttons
        addButton = new JButton("Add"); addButton.setBounds(20, 380, 70, 30); formPanel.add(addButton);
        cancelButton = new JButton("Cancel"); cancelButton.setBounds(100, 380, 80, 30); formPanel.add(cancelButton);
        editButton = new JButton("Edit"); editButton.setBounds(190, 380, 70, 30); formPanel.add(editButton);
        deleteButton = new JButton("Delete"); deleteButton.setBounds(270, 380, 75, 30); formPanel.add(deleteButton);

        add(formPanel);


        tableModel = new DefaultTableModel(new String[]{
                "RegNo", "Brand", "Model", "Year", "Seating", "Transmission",
                "FuelTypeID", "VehicleTypeID", "Rate", "Available"
        }, 0);
        carTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(carTable);
        tableScroll.setBounds(420, 20, 400, 470);
        add(tableScroll);


        loadComboBoxes();

        loadVehiclesIntoTable();



        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        cancelButton.addActionListener(this);
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
            String sql = "SELECT RegNumber, Brand, Model, Year, SeatingCapacity, TransmissionType, FuelTypeID, TypeID, Rate, Available FROM Vehicle";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            // Clear existing rows
            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("RegNumber"));
                row.add(rs.getString("Brand"));
                row.add(rs.getString("Model"));
                row.add(rs.getInt("Year"));
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

        String sql = "INSERT INTO Vehicle (RegNumber, Brand, Model, Year, SeatingCapacity, TransmissionType, FuelTypeID, TypeID, Rate, Available) " +
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

            // ✅ Convert "Yes"/"No" into boolean for BIT
            pstmt.setBoolean(10, available.equalsIgnoreCase("Yes"));

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Vehicle saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save vehicle in DB\n" + e.getMessage());
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
            int fuelTypeID = fuelTypeComboBox.getSelectedIndex() + 1;  // +1 assuming IDs start from 1
            int vehicleTypeID = vehicleTypeComboBox.getSelectedIndex() + 1;
            String rate = rateField.getText().trim();
            String available = (String) availabilityBox.getSelectedItem();

            if (regNo.isEmpty() || brand.isEmpty() || model.isEmpty() || year.isEmpty()
                    || seating.isEmpty() || rate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            // Save to DB
            saveVehicleToDB(regNo, brand, model, year, seating, transmission,
                    fuelTypeID, vehicleTypeID, rate, available);

            // Add to table UI
            tableModel.addRow(new Object[]{
                    regNo, brand, model, year, seating, transmission,
                    fuelTypeID, vehicleTypeID, rate, available
            });

            clearForm();

        } else if (e.getSource() == cancelButton) {
            clearForm();

        }       else if (e.getSource() == deleteButton) {
                int selectedRow = carTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String regNo = (String) tableModel.getValueAt(selectedRow, 0);

                    // Delete from DB
                    deleteVehicleFromDB(regNo);

                    // Remove from UI
                    tableModel.removeRow(selectedRow);

                } else {
                    JOptionPane.showMessageDialog(this, "Please select a row to delete.");
                }
            }
            else if (e.getSource() == editButton) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow >= 0) {
                regNoField.setText((String) tableModel.getValueAt(selectedRow, 0));
                brandField.setText((String) tableModel.getValueAt(selectedRow, 1));
                modelField.setText((String) tableModel.getValueAt(selectedRow, 2));
                yearField.setText((String) tableModel.getValueAt(selectedRow, 3));
                seatingField.setText((String) tableModel.getValueAt(selectedRow, 4));
                transmissionBox.setSelectedItem((String) tableModel.getValueAt(selectedRow, 5));
                fuelTypeComboBox.setSelectedIndex(((int) tableModel.getValueAt(selectedRow, 6)) - 1);
                vehicleTypeComboBox.setSelectedIndex(((int) tableModel.getValueAt(selectedRow, 7)) - 1);
                rateField.setText((String) tableModel.getValueAt(selectedRow, 8));
                availabilityBox.setSelectedItem((String) tableModel.getValueAt(selectedRow, 9));
                tableModel.removeRow(selectedRow);
                // You can add DB update code here if needed
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            }
        }
            if(e.getSource() == cancelButton){
                this.setVisible(false);
                new AdminPortal().setVisible(true);
            }
    }

    public static void main(String[] args) {
        new CarRegistration().setVisible(true);
    }
}
