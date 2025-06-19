package VRS;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class BookingDetails extends JFrame {

    private JTextField startDateField, returnDateField;
    private JLabel billLabel;
    private JButton proceedButton;
    private int ratePerDay;
    private long totalBill = -1;

    public BookingDetails(int vehicleID) {
        setTitle("Booking Details");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ratePerDay = getRateFromTable(vehicleID);

        // Header
        JLabel title = new JLabel("Vehicle Booking Summary", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Center form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"));
        startDateField = new JTextField();
        formPanel.add(startDateField);

        formPanel.add(new JLabel("Return Date (yyyy-MM-dd):"));
        returnDateField = new JTextField();
        formPanel.add(returnDateField);

        JButton billButton = new JButton("Generate Bill");
        billButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(billButton);

        billLabel = new JLabel("Total Bill: PKR 0", SwingConstants.LEFT);
        billLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(billLabel);

        add(formPanel, BorderLayout.CENTER);

        // Bottom panel with proceed button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        proceedButton = new JButton("Proceed to Payment");
        proceedButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        proceedButton.setEnabled(false); // Only enabled after bill generation
        bottomPanel.add(proceedButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners
        billButton.addActionListener(e -> generateBill());
        proceedButton.addActionListener(e -> openPaymentPage(vehicleID, totalBill));

        setVisible(true);
    }

    private int getRateFromTable(int vehicleID) {
        for (int i = 0; i < VehicleBooking.vehicleTable.getRowCount(); i++) {
            int id = (int) VehicleBooking.vehicleTable.getValueAt(i, 0);
            if (id == vehicleID) {
                return (int) VehicleBooking.vehicleTable.getValueAt(i, 8);
            }
        }
        return 0;
    }

    private void generateBill() {
        try {
            String startStr = startDateField.getText().trim();
            String returnStr = returnDateField.getText().trim();

            LocalDate startDate = LocalDate.parse(startStr);
            LocalDate returnDate = LocalDate.parse(returnStr);

            if (returnDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this,
                        "Return date cannot be before start date.",
                        "Date Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            long days = ChronoUnit.DAYS.between(startDate, returnDate) + 1;
            totalBill = days * ratePerDay;

            billLabel.setText("Total Bill: PKR " + totalBill + " (" + days + " days × " + ratePerDay + ")");
            proceedButton.setEnabled(true);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter dates in yyyy-MM-dd format.",
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Something went wrong: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPaymentPage(int vehicleID, long amount) {
        new PaymentPage(vehicleID, amount);
        dispose(); // Close booking window
    }
}
