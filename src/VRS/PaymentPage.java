package VRS;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class PaymentPage extends JFrame {

    private JTextField cardNumberField, nameField, expiryField, cvvField;
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
        setSize(400, 350);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        add(cardNumberField);

        add(new JLabel("Card Holder Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Expiry Date (YYYY-MM-DD):"));
        expiryField = new JTextField();
        add(expiryField);

        add(new JLabel("CVV:"));
        cvvField = new JTextField();
        add(cvvField);

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
        String expiry = expiryField.getText().trim();
        String cvv = cvvField.getText().trim();

        if (cardNumber.isEmpty() || name.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Connection conn = ConnectionClass.getConnection();
            String sql = "INSERT INTO Payment (CustomerID, CardNumber, CardHolderName, ExpiryDate, CVV, Amount, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerID);
            stmt.setString(2, cardNumber);
            stmt.setString(3, name);
            stmt.setDate(4, java.sql.Date.valueOf(expiry));
            stmt.setString(5, cvv);
            stmt.setInt(6, amount);
            stmt.setString(7, "Paid");

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "💳 Payment Successful!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Payment Failed.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
