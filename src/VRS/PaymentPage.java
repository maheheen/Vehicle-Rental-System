package VRS;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class PaymentPage extends JFrame {

    private JTextField cardNumberField, nameField, expiryMonthField, expiryYearField;
    private JLabel amountLabel;
    private JButton payButton;

    private int customerID;
    private int vehicleID;
    private int amount;

    public PaymentPage(int customerID, int vehicleID, int amount) {
        this.customerID = customerID;
        this.vehicleID = vehicleID;
        this.amount = amount;

        setTitle("Enter Payment Details");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        add(cardNumberField);

        add(new JLabel("Card Holder Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Expiry Month (1-12):"));
        expiryMonthField = new JTextField();
        add(expiryMonthField);

        add(new JLabel("Expiry Year (YYYY):"));
        expiryYearField = new JTextField();
        add(expiryYearField);

        add(new JLabel("Amount:"));
        amountLabel = new JLabel("PKR " + amount);
        add(amountLabel);

        payButton = new JButton("Pay Now");
        add(new JLabel()); // spacer
        add(payButton);

        payButton.addActionListener(e -> processPayment());

        setVisible(true);
    }

    private void processPayment() {
        String cardNumber = cardNumberField.getText().trim();
        String name = nameField.getText().trim();
        String expMonthStr = expiryMonthField.getText().trim();
        String expYearStr = expiryYearField.getText().trim();

        if (cardNumber.isEmpty() || name.isEmpty() || expMonthStr.isEmpty() || expYearStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        int expiryMonth, expiryYear;
        try {
            expiryMonth = Integer.parseInt(expMonthStr);
            expiryYear = Integer.parseInt(expYearStr);

            if (expiryMonth < 1 || expiryMonth > 12) {
                throw new IllegalArgumentException("Invalid expiry month.");
            }
            if (expiryYear < java.time.Year.now().getValue()) {
                throw new IllegalArgumentException("Expiry year must be current or future.");
            }
        }  catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Expiry month and year must be numeric.");
        return;
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage() != null ? e.getMessage() : "Invalid expiry date.");
        return;
    }

    String maskedCard = maskCard(cardNumber);
        if (maskedCard == null) {
            JOptionPane.showMessageDialog(this, "Invalid card number. Must be at least 4 digits.");
            return;
        }

        try (Connection conn = ConnectionClass.getConnection()) {
            String sql = "INSERT INTO Payment (CustomerID, MaskedCardNumber, CardHolderName, ExpiryMonth, ExpiryYear, Amount, Status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerID);
            stmt.setString(2, maskedCard);
            stmt.setString(3, name);
            stmt.setInt(4, expiryMonth);
            stmt.setInt(5, expiryYear);
            stmt.setInt(6, amount);
            stmt.setString(7, "Paid");

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "💳 Payment Successful!");
                this.dispose(); // close payment window
                new CustomerPortal(); // open customer portal again


        } else {
                JOptionPane.showMessageDialog(this, "❌ Payment Failed.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private String maskCard(String cardNumber) {
        // Keep only last 4 digits
        if (cardNumber.length() < 4) return null;
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
