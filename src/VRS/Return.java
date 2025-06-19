package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Return extends JFrame implements ActionListener {
    JTextField carIdField, customerIdField, returnDateField, elapsedField, fineField;
    JTable bookingTable;
    DefaultTableModel model;
    JButton okButton, cancelButton;

    public Return() {
        setTitle("Return Car");
        setSize(800, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel carIdLabel = new JLabel("Car ID:");
        carIdLabel.setBounds(30, 30, 100, 25); add(carIdLabel);
        carIdField = new JTextField(); carIdField.setBounds(150, 30, 150, 25); add(carIdField);

        JLabel customerIdLabel = new JLabel("Customer ID:");
        customerIdLabel.setBounds(30, 70, 100, 25); add(customerIdLabel);
        customerIdField = new JTextField(); customerIdField.setBounds(150, 70, 150, 25); add(customerIdField);

        JLabel returnDateLabel = new JLabel("Return Date:");
        returnDateLabel.setBounds(30, 110, 100, 25); add(returnDateLabel);
        returnDateField = new JTextField(); returnDateField.setBounds(150, 110, 150, 25);
        returnDateField.setToolTipText("Format: YYYY-MM-DD"); add(returnDateField);

        JLabel elapsedLabel = new JLabel("Days Elapsed:");
        elapsedLabel.setBounds(30, 150, 100, 25); add(elapsedLabel);
        elapsedField = new JTextField(); elapsedField.setBounds(150, 150, 150, 25);
        elapsedField.setEditable(false); add(elapsedField);

        JLabel fineLabel = new JLabel("Fine:");
        fineLabel.setBounds(30, 190, 100, 25); add(fineLabel);
        fineField = new JTextField(); fineField.setBounds(150, 190, 150, 25);
        fineField.setEditable(false); add(fineField);

        okButton = new JButton("Ok");
        okButton.setBounds(50, 230, 100, 30); add(okButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(170, 230, 100, 30); add(cancelButton);

        model = new DefaultTableModel(new String[]{"CustID", "CarID", "ReturnDate", "Elapsed", "Fine"}, 0);
        bookingTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBounds(330, 30, 420, 250);
        add(scrollPane);

        loadBookings();

        okButton.addActionListener(this);
        cancelButton.addActionListener(e -> {
            dispose();
            new AdminPortal(2).setVisible(true);
        });
    }

    private void loadBookings() {
        try (Connection conn = new ConnectionClass().con;
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Booking")) {
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("CustomerID"),
                        rs.getInt("VehicleID"),
                        rs.getDate("ReturnDate"),
                        "", "" // Elapsed, Fine to be calculated
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String carId = carIdField.getText().trim();
        String custId = customerIdField.getText().trim();
        String returnDateText = returnDateField.getText().trim();

        if (carId.isEmpty() || custId.isEmpty() || returnDateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection conn = new ConnectionClass().con) {
            PreparedStatement pst = conn.prepareStatement(
                    "SELECT ReturnDate FROM Booking WHERE VehicleID = ? AND CustomerID = ? ORDER BY BookingID DESC");
            pst.setString(1, carId);
            pst.setString(2, custId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                LocalDate expectedReturn = rs.getDate("ReturnDate").toLocalDate();
                LocalDate actualReturn = LocalDate.parse(returnDateText);
                long daysLate = ChronoUnit.DAYS.between(expectedReturn, actualReturn);

                int fine = 0;
                if (daysLate > 0) {
                    fine = (int) daysLate * 1000;
                }

                elapsedField.setText(String.valueOf(Math.max(0, daysLate)));
                fineField.setText(String.valueOf(fine));

                PreparedStatement updateVehicle = conn.prepareStatement(
                        "UPDATE Vehicle SET Available = 1 WHERE VehicleID = ?");
                updateVehicle.setString(1, carId);
                updateVehicle.executeUpdate();

                JOptionPane.showMessageDialog(this, "Car Returned. Fine: ₹" + fine);
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Booking not found.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Return().setVisible(true));
    }
}
