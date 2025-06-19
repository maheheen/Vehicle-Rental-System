package VRS;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class BookingDetails extends JFrame {

    private JTextField startDateField, returnDateField;
    private JLabel billLabel;
    private int ratePerDay;

    public BookingDetails(int vehicleID) {
        setTitle("Booking Details - Vehicle ID: " + vehicleID);
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Fetch rate from VehicleBooking's table
        ratePerDay = getRateFromTable(vehicleID);

        // UI components
        add(new JLabel("Start Date (yyyy-MM-dd):"));
        startDateField = new JTextField();
        add(startDateField);

        add(new JLabel("Return Date (yyyy-MM-dd):"));
        returnDateField = new JTextField();
        add(returnDateField);

        JButton billButton = new JButton("Generate Bill");
        billButton.addActionListener(e -> generateBill());
        add(billButton);

        billLabel = new JLabel("Total Bill: ");
        billLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(billLabel);

        // Empty labels for spacing
        add(new JLabel());
        add(new JLabel());

        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }

    private int getRateFromTable(int vehicleID) {
        // Loop through VehicleBooking table to find matching vehicle ID and rate
        for (int i = 0; i < VehicleBooking.vehicleTable.getRowCount(); i++) {
            int id = (int) VehicleBooking.vehicleTable.getValueAt(i, 0); // VehicleID
            if (id == vehicleID) {
                return (int) VehicleBooking.vehicleTable.getValueAt(i, 8); // Rate column
            }
        }
        return 0; // Default/fallback
    }

    private void generateBill() {
        try {
            String startStr = startDateField.getText().trim();
            String returnStr = returnDateField.getText().trim();

            // Parse dates in yyyy-MM-dd format
            LocalDate startDate = LocalDate.parse(startStr);
            LocalDate returnDate = LocalDate.parse(returnStr);

            if (returnDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this,
                        "Return date cannot be before start date.",
                        "Invalid Dates",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            long rentalDays = ChronoUnit.DAYS.between(startDate, returnDate) + 1;
            long totalBill = rentalDays * ratePerDay;

            billLabel.setText("Total Bill: PKR " + totalBill + " (" + rentalDays + " days × " + ratePerDay + ")");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format! Please enter date as yyyy-MM-dd (e.g., 2025-08-10).",
                    "Date Format Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Something went wrong: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
