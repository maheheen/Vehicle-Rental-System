package VRS;

import javax.swing.*;
import java.awt.*;

public class PaymentPage extends JFrame {

    public PaymentPage(int customerID, int vehicleID, int totalBill) {
        setTitle("Payment Page");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel info = new JLabel("<html><center><h2>Payment Summary</h2>" +
                "<p>Customer ID: " + customerID + "</p>" +
                "<p>Vehicle ID: " + vehicleID + "</p>" +
                "<p><b>Total: PKR " + totalBill + "</b></p></center></html>", SwingConstants.CENTER);
        add(info, BorderLayout.CENTER);

        JButton payBtn = new JButton("Pay Now");
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "💳 Payment successful!");
            this.dispose(); // Close PaymentPage
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(payBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
