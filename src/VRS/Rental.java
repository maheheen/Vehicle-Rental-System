package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental extends JFrame implements ActionListener {

    JComboBox<String> carIdBox, customerIdBox;
    JTextField customerNameField, rentalFeeField, rentalDateField, dueDateField;
    JLabel availabilityLabel;
    JTable vehicleTable, bookingTable;
    DefaultTableModel tableModel, bookingModel;
    JButton rentButton, cancelButton;

    public Rental() {
        setTitle("Rent Vehicle");
        setSize(1050, 530);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(222, 222, 222));

        JLabel carIdLabel = new JLabel("Car ID:");
        carIdLabel.setBounds(30, 30, 100, 25); add(carIdLabel);
        carIdBox = new JComboBox<>(); carIdBox.setBounds(150, 30, 150, 25); add(carIdBox);

        availabilityLabel = new JLabel("Available?");
        availabilityLabel.setBounds(320, 30, 120, 25); add(availabilityLabel);

        JLabel customerIdLabel = new JLabel("Customer ID:");
        customerIdLabel.setBounds(30, 70, 100, 25); add(customerIdLabel);
        customerIdBox = new JComboBox<>(); customerIdBox.setBounds(150, 70, 150, 25); add(customerIdBox);

        JLabel customerNameLabel = new JLabel("Customer Name:");
        customerNameLabel.setBounds(30, 110, 120, 25); add(customerNameLabel);
        customerNameField = new JTextField(); customerNameField.setBounds(150, 110, 250, 25);
        customerNameField.setEditable(false); add(customerNameField);

        JLabel feeLabel = new JLabel("Rental Fee (per day):");
        feeLabel.setBounds(30, 150, 140, 25); add(feeLabel);
        rentalFeeField = new JTextField(); rentalFeeField.setBounds(180, 150, 100, 25);
        rentalFeeField.setEditable(false); add(rentalFeeField);

        JLabel rentalDateLabel = new JLabel("Rental Date:");
        rentalDateLabel.setBounds(30, 190, 100, 25); add(rentalDateLabel);
        rentalDateField = new JTextField(); rentalDateField.setBounds(150, 190, 150, 25);
        rentalDateField.setToolTipText("Format: YYYY-MM-DD"); add(rentalDateField);

        JLabel dueDateLabel = new JLabel("Due Date:");
        dueDateLabel.setBounds(30, 230, 100, 25); add(dueDateLabel);
        dueDateField = new JTextField(); dueDateField.setBounds(150, 230, 150, 25);
        dueDateField.setToolTipText("Format: YYYY-MM-DD"); add(dueDateField);

        rentButton = new JButton("Rent");
        rentButton.setBounds(100, 280, 100, 30); add(rentButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(220, 280, 100, 30); add(cancelButton);

        rentButton.addActionListener(this);
        cancelButton.addActionListener(e -> {
            this.dispose();  // or this.setVisible(false);
            new AdminPortal(2).setVisible(true);
        });


        tableModel = new DefaultTableModel(new String[]{"VehicleID", "Brand", "Model"}, 0);
        vehicleTable = new JTable(tableModel);
        JScrollPane vehicleScroll = new JScrollPane(vehicleTable);
        vehicleScroll.setBounds(430, 30, 250, 150);
        add(vehicleScroll);

        bookingModel = new DefaultTableModel(new String[]{
                "BookingID", "CustomerID", "LoginID", "VehicleID", "StartDate", "ReturnDate", "TotalDays", "Amount"
        }, 0);
        bookingTable = new JTable(bookingModel);
        JScrollPane bookingScroll = new JScrollPane(bookingTable);
        bookingScroll.setBounds(30, 330, 980, 140);
        add(bookingScroll);

        loadAvailableVehicles();
        loadCustomerIds();
        loadBookingData();

        carIdBox.addActionListener(e -> {
            updateAvailabilityStatus();
            updateRentalFee();
        });
        customerIdBox.addActionListener(e -> loadCustomerName());
    }

    private void loadAvailableVehicles() {
        carIdBox.removeAllItems();
        tableModel.setRowCount(0);
        try (Connection conn = new ConnectionClass().con;
             ResultSet rs = conn.createStatement().executeQuery(
                     "SELECT VehicleID, Brand, Model FROM Vehicle WHERE Available = 1")) {
            while (rs.next()) {
                String id = rs.getString("VehicleID");
                carIdBox.addItem(id);
                tableModel.addRow(new Object[]{id, rs.getString("Brand"), rs.getString("Model")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerIds() {
        customerIdBox.removeAllItems();
        try (Connection conn = new ConnectionClass().con;
             ResultSet rs = conn.createStatement().executeQuery("SELECT LoginID FROM Customer")) {
            while (rs.next()) {
                customerIdBox.addItem(rs.getString("LoginID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerName() {
        String id = (String) customerIdBox.getSelectedItem();
        if (id == null) return;
        try (Connection conn = new ConnectionClass().con;
             PreparedStatement pst = conn.prepareStatement(
                     "SELECT FirstName, LastName FROM Customer WHERE LoginID=?")) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                customerNameField.setText(rs.getString("FirstName") + " " + rs.getString("LastName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAvailabilityStatus() {
        String id = (String) carIdBox.getSelectedItem();
        if (id == null) return;
        try (Connection conn = new ConnectionClass().con;
             PreparedStatement pst = conn.prepareStatement("SELECT Available FROM Vehicle WHERE VehicleID=?")) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                availabilityLabel.setText(rs.getBoolean("Available") ? "✅ Available" : "❌ Not Available");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRentalFee() {
        String id = (String) carIdBox.getSelectedItem();
        if (id == null) return;
        try (Connection conn = new ConnectionClass().con;
             PreparedStatement pst = conn.prepareStatement("SELECT Rate FROM Vehicle WHERE VehicleID=?")) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                rentalFeeField.setText(rs.getString("Rate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCustomerIdFromLoginId(String loginId) throws SQLException {
        try (Connection conn = new ConnectionClass().con;
             PreparedStatement pst = conn.prepareStatement("SELECT CustomerID FROM Customer WHERE LoginID = ?")) {
            pst.setString(1, loginId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("CustomerID");
            } else {
                throw new SQLException("CustomerID not found for LoginID: " + loginId);
            }
        }
    }

    private String getLoginIdFromCustomerId(int customerId) {
        try (Connection conn = new ConnectionClass().con;
             PreparedStatement pst = conn.prepareStatement("SELECT LoginID FROM Customer WHERE CustomerID = ?")) {
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("LoginID");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "N/A";
    }

    private void loadBookingData() {
        bookingModel.setRowCount(0);
        try (Connection conn = new ConnectionClass().con;
             ResultSet rs = conn.createStatement().executeQuery(
                     "SELECT BookingID, CustomerID, VehicleID, StartDate, ReturnDate, TotalDays, TotalAmount FROM Booking")) {
            while (rs.next()) {
                int custId = rs.getInt("CustomerID");
                bookingModel.addRow(new Object[]{
                        rs.getInt("BookingID"),
                        custId,
                        getLoginIdFromCustomerId(custId),
                        rs.getInt("VehicleID"),
                        rs.getDate("StartDate"),
                        rs.getDate("ReturnDate"),
                        rs.getInt("TotalDays"),
                        rs.getInt("TotalAmount")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String vehicleId = (String) carIdBox.getSelectedItem();
        String customerLoginId = (String) customerIdBox.getSelectedItem();
        String rentalFeeText = rentalFeeField.getText();
        String rentalDateText = rentalDateField.getText();
        String dueDateText = dueDateField.getText();

        if (vehicleId == null || customerLoginId == null || rentalFeeText.isEmpty()
                || rentalDateText.isEmpty() || dueDateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Fill all fields.");
            return;
        }

        try {
            LocalDate start = LocalDate.parse(rentalDateText);
            LocalDate end = LocalDate.parse(dueDateText);
            long totalDays = ChronoUnit.DAYS.between(start, end);
            if (totalDays <= 0) {
                JOptionPane.showMessageDialog(this, "❌ Return date must be after rental date.");
                return;
            }

            int rentalFee = Integer.parseInt(rentalFeeText);
            int totalAmount = (int) (rentalFee * totalDays);
            int actualCustomerId = getCustomerIdFromLoginId(customerLoginId);

            try (Connection conn = new ConnectionClass().con) {
                conn.setAutoCommit(false);

                try (PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO Booking (CustomerID, VehicleID, StartDate, ReturnDate, TotalDays, TotalAmount) VALUES (?, ?, ?, ?, ?, ?)")) {
                    pst.setInt(1, actualCustomerId);
                    pst.setString(2, vehicleId);
                    pst.setDate(3, Date.valueOf(start));
                    pst.setDate(4, Date.valueOf(end));
                    pst.setInt(5, (int) totalDays);
                    pst.setInt(6, totalAmount);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst2 = conn.prepareStatement("UPDATE Vehicle SET Available = 0 WHERE VehicleID=?")) {
                    pst2.setString(1, vehicleId);
                    pst2.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "✅ Booking Successful! Total Bill: PKR " + totalAmount);
                loadAvailableVehicles();
                loadBookingData();
                updateAvailabilityStatus();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Rental().setVisible(true));
    }
}
