package VRS;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class BookingDetails extends JFrame {

    private JTextField startDateField, returnDateField;
    private JLabel billLabel;
    private JButton proceedButton;

    private int customerID;
    private int vehicleID;
    private int ratePerDay;
    private long totalBill = -1;
    private LocalDate startDate, returnDate;
    private long rentalDays;

    public BookingDetails(int customerID, int vehicleID) {
        this.customerID = customerID;
        this.vehicleID = vehicleID;

        setTitle("Booking Details");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ratePerDay = getRateFromTable(vehicleID);

        JLabel title = new JLabel("Vehicle Booking Summary", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"));
        startDateField = new JTextField();
        formPanel.add(startDateField);

        formPanel.add(new JLabel("Return Date (yyyy-MM-dd):"));
        returnDateField = new JTextField();
        formPanel.add(returnDateField);

        JButton billButton = new JButton("Generate Bill");
        formPanel.add(billButton);

        billLabel = new JLabel("Total Bill: PKR 0", SwingConstants.LEFT);
        billLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(billLabel);

        add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        proceedButton = new JButton("Confirm & Proceed to Payment");
        proceedButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        proceedButton.setEnabled(false);
        bottomPanel.add(proceedButton);
        add(bottomPanel, BorderLayout.SOUTH);

        billButton.addActionListener(e -> generateBill());
        proceedButton.addActionListener(e -> saveBookingToDatabase());

        setVisible(true);
    }

    private int getRateFromTable(int vehicleID) {
        for (int i = 0; i < VehicleBooking.vehicleTable.getRowCount(); i++) {
            int id = (int) VehicleBooking.vehicleTable.getValueAt(i, 0);
            if (id == vehicleID) {
                return (int) VehicleBooking.vehicleTable.getValueAt(i, 8); // Rate column
            }
        }
        return 0;
    }

    private void generateBill() {
        try {
            startDate = LocalDate.parse(startDateField.getText().trim());
            returnDate = LocalDate.parse(returnDateField.getText().trim());

            if (returnDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this, "Return date cannot be before start date.");
                return;
            }

            rentalDays = ChronoUnit.DAYS.between(startDate, returnDate) + 1;
            totalBill = rentalDays * ratePerDay;

            billLabel.setText("Total Bill: PKR " + totalBill + " (" + rentalDays + " days × " + ratePerDay + ")");
            proceedButton.setEnabled(true);

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format! Please enter date as yyyy-MM-dd.",
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBookingToDatabase() {
        Connection conn = ConnectionClass.getConnection();
        String sql = "INSERT INTO Booking (CustomerID, VehicleID, StartDate, ReturnDate, TotalDays, TotalAmount) VALUES (?, ?, ?, ?, ?, ?)";
        String updateVehicle = "UPDATE Vehicle SET Available = 0 WHERE VehicleID = ?";

        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
                PreparedStatement updateStmt = conn.prepareStatement(updateVehicle)
        ) {
            stmt.setInt(1, customerID);
            stmt.setInt(2, vehicleID);
            stmt.setDate(3, java.sql.Date.valueOf(startDate));
            stmt.setDate(4, java.sql.Date.valueOf(returnDate));
            stmt.setInt(5, (int) rentalDays);
            stmt.setInt(6, (int) totalBill);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                updateStmt.setInt(1, vehicleID);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Booking saved successfully!");
                new PaymentPage(customerID, vehicleID, (int) totalBill); // Proceed to payment
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Booking failed to save.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving booking: " + e.getMessage());
        }
    }
}
