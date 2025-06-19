package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CarRegistration extends JFrame implements ActionListener {

    JTextField regNoField, yearField, seatingField, rateField;

    JComboBox<String> brandComboBox, modelComboBox;

    JComboBox<String> transmissionBox, fuelTypeComboBox, vehicleTypeComboBox, availabilityBox;
    JTable carTable;
    DefaultTableModel tableModel;
    JButton addButton, editButton, deleteButton, cancelButton, backButton, updateButton;
    private Connection conn;

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
        regNoField.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (str == null) return;

                // Combine existing and new text
                String text = getText(0, getLength());
                String result = text.substring(0, offset) + str + text.substring(offset);

                // Only allow up to 7 characters (e.g., ABC-123)
                if (result.length() > 7) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                // Allow pattern like ABC, ABC-, ABC-1, ABC-12, ABC-123
                if (!result.matches("[A-Z]{0,3}-?[0-9]{0,3}")) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                // Allow input, force uppercase
                super.insertString(offset, str.toUpperCase(), attr);
            }
        });



        regNoField.setBounds(150, 20, 200, 25);
        formPanel.add(regNoField);


        formPanel.add(new JLabel("Brand")).setBounds(20, 50, 100, 25);
        brandComboBox = new JComboBox<>();
        brandComboBox.setBounds(150, 50, 200, 25);
        formPanel.add(brandComboBox);

        formPanel.add(new JLabel("Model")).setBounds(20, 80, 100, 25);
        modelComboBox = new JComboBox<>();
        modelComboBox.setBounds(150, 80, 200, 25);
        formPanel.add(modelComboBox);

        formPanel.add(new JLabel("Year")).setBounds(20, 110, 100, 25);
        yearField = new JTextField();
        yearField.setBounds(150, 110, 200, 25);
        formPanel.add(yearField);

        formPanel.add(new JLabel("Seating Capacity")).setBounds(20, 150, 120, 25);
        seatingField = new JTextField();
        seatingField.setBounds(150, 150, 200, 25);
        formPanel.add(seatingField);

        formPanel.add(new JLabel("Transmission")).setBounds(20, 180, 100, 25);
        transmissionBox = new JComboBox<>(new String[]{"Automatic", "Manual"});
        transmissionBox.setBounds(150, 180, 200, 25);
        formPanel.add(transmissionBox);

        formPanel.add(new JLabel("Fuel Type")).setBounds(20, 210, 100, 25);
        fuelTypeComboBox = new JComboBox<>();
        fuelTypeComboBox.setBounds(150, 210, 200, 25);
        formPanel.add(fuelTypeComboBox);

        formPanel.add(new JLabel("Vehicle Type")).setBounds(20, 240, 120, 25);
        vehicleTypeComboBox = new JComboBox<>();
        vehicleTypeComboBox.setBounds(150, 240, 200, 25);
        formPanel.add(vehicleTypeComboBox);

        formPanel.add(new JLabel("Rate")).setBounds(20, 270, 270, 25);
        rateField = new JTextField();
        rateField.setBounds(150, 270, 200, 25);
        formPanel.add(rateField);

        formPanel.add(new JLabel("Available")).setBounds(20, 300, 100, 25);
        availabilityBox = new JComboBox<>(new String[]{"Yes", "No"});
        availabilityBox.setBounds(150, 300, 200, 25);
        formPanel.add(availabilityBox);

        // Buttons
        addButton = new JButton("Add");
        addButton.setBounds(20, 360, 70, 30);
        formPanel.add(addButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(100, 360, 80, 30);
        formPanel.add(cancelButton);

        editButton = new JButton("Edit");
        editButton.setBounds(190, 360, 70, 30);
        formPanel.add(editButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(270, 360, 75, 30);
        formPanel.add(deleteButton);

        updateButton = new JButton("Update");
        updateButton.setBounds(150, 410, 80, 30);
        formPanel.add(updateButton);

        backButton = new JButton("Back");
        backButton.setBounds(250, 410, 80, 30);
        formPanel.add(backButton);

        add(formPanel);

        tableModel = new DefaultTableModel(new String[]{
                "RegNo", "Brand", "Model", "Year", "Seating", "Transmission",
                "FuelTypeID", "VehicleTypeID", "Rate", "Available"
        }, 0);

        carTable = new JTable(tableModel);
        carTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Enables horizontal scroll
        carTable.setFillsViewportHeight(true);

        JScrollPane tableScroll = new JScrollPane(carTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setBounds(420, 20, 850, 500); // Increase width here
        add(tableScroll);


        carTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // RegNo
        carTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Brand
        carTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Model
        carTable.getColumnModel().getColumn(3).setPreferredWidth(60);   // Year
        carTable.getColumnModel().getColumn(4).setPreferredWidth(40);   // Seating
        carTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Transmission
        carTable.getColumnModel().getColumn(6).setPreferredWidth(100);  // FuelTypeID
        carTable.getColumnModel().getColumn(7).setPreferredWidth(120);  // VehicleTypeID
        carTable.getColumnModel().getColumn(8).setPreferredWidth(80);   // Rate
        carTable.getColumnModel().getColumn(9).setPreferredWidth(80);   // Available




        loadComboBoxes();
        loadVehiclesIntoTable();

        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        cancelButton.addActionListener(this);
        backButton.addActionListener(this);
        updateButton.addActionListener(this);

        regNoField.addActionListener(e -> brandComboBox.requestFocus());
        brandComboBox.addActionListener(e -> modelComboBox.requestFocus());
        modelComboBox.addActionListener(e -> yearField.requestFocus());
        yearField.addActionListener(e -> seatingField.requestFocus());
        seatingField.addActionListener(e -> transmissionBox.requestFocus());
        transmissionBox.addActionListener(e -> fuelTypeComboBox.requestFocus());
        fuelTypeComboBox.addActionListener(e -> vehicleTypeComboBox.requestFocus());
        vehicleTypeComboBox.addActionListener(e -> rateField.requestFocus());
        rateField.addActionListener(e -> availabilityBox.requestFocus());
        availabilityBox.addActionListener(e -> addButton.requestFocus());

    }

    private void loadComboBoxes() {

        // Manually set brands
        String[] brands = {
                "Toyota", "Honda", "Suzuki", "Kia", "Hyundai",
                "Nissan", "Daihatsu", "Chevrolet", "MG"
        };
        brandComboBox.setModel(new DefaultComboBoxModel<>(brands));


        // Manually set fuel types
        fuelTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "Petrol", "Diesel", "Hybrid", "Electric"
        }));

        // Manually set vehicle types
        vehicleTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "Sedan", "Hatchback", "SUV", "Van", "Pickup Truck"
        }));

        // Load models when a brand is selected
        brandComboBox.addActionListener(e -> {
            String selectedBrand = (String) brandComboBox.getSelectedItem();
            if (selectedBrand != null) {
                loadModelsForBrand(selectedBrand);
            }
        });

        // Trigger initial model load
        if (brandComboBox.getItemCount() > 0) {
            brandComboBox.setSelectedIndex(0);
        }
    }

    private void loadModelsForBrand(String brand) {
        modelComboBox.removeAllItems();

        switch (brand) {
            case "Toyota":
                modelComboBox.addItem("Corolla");
                modelComboBox.addItem("Yaris");
                modelComboBox.addItem("Hiace");
                modelComboBox.addItem("Fortuner");
                modelComboBox.addItem("Land Cruiser");
                break;
            case "Honda":
                modelComboBox.addItem("Civic");
                modelComboBox.addItem("City");
                modelComboBox.addItem("BR-V");
                modelComboBox.addItem("Vezel");
                modelComboBox.addItem("Accord");
                modelComboBox.addItem("Elan");
                break;
            case "Suzuki":
                modelComboBox.addItem("WagonR");
                modelComboBox.addItem("Alto");
                modelComboBox.addItem("Cultus");
                modelComboBox.addItem("Mehran");
                modelComboBox.addItem("Swift");
                modelComboBox.addItem("Baleno");
                break;
            case "Kia":
                modelComboBox.addItem("Sportage");
                modelComboBox.addItem("Stonic");
                modelComboBox.addItem("Picanto");
                break;
            case "Hyundai":
                modelComboBox.addItem("Tucson");
                modelComboBox.addItem("Elantra");
                modelComboBox.addItem("Sonata");
                break;
            case "Nissan":
                modelComboBox.addItem("Dayz");
                modelComboBox.addItem("Sunny");
                break;
            case "Daihatsu":
                modelComboBox.addItem("Mira");
                break;
            case "Chevrolet":
                modelComboBox.addItem("Spark");
                break;
            case "MG":
                modelComboBox.addItem("HS");
                modelComboBox.addItem("ZS EV");
                break;
            default:
                modelComboBox.addItem("Unknown Model");
        }
    }

    private void loadVehiclesIntoTable() {
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection conn = ConnectionClass.getConnection();
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
            Connection conn = ConnectionClass.getConnection();
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
            Connection conn = ConnectionClass.getConnection();;
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
        brandComboBox.setSelectedIndex(0);
        modelComboBox.setSelectedIndex(0);
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
            Connection conn = ConnectionClass.getConnection();;
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
            String brand = (String) brandComboBox.getSelectedItem();
            String model = (String) modelComboBox.getSelectedItem();
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

            try {
                ConnectionClass connectionClass = new ConnectionClass();
                Connection conn = connectionClass.con;

                // Check if RegNumber already exists
                PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM Vehicle WHERE RegNumber = ?");
                checkStmt.setString(1, regNo);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "A vehicle with this registration number already exists!");
                    rs.close();
                    checkStmt.close();
                    conn.close();
                    return;
                }

                rs.close();
                checkStmt.close();

                // If not exists, then insert
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
                conn.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error checking for duplicate vehicle.");
            }

        } else if (e.getSource() == editButton) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow >= 0) {
                regNoField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                brandComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 1).toString());
                modelComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
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
            String brand = (String) brandComboBox.getSelectedItem();
            String model = (String) modelComboBox.getSelectedItem();
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
            new AdminPortal(2).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CarRegistration().setVisible(true);
        });
    }
}
