package VRS;

import javax.swing.*;
import java.awt.*;

public class PaymentPage extends JFrame {

    public PaymentPage(int customerID, int vehicleID, int totalBill) {
        setTitle("Payment Page");
        setSize(400, 300);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("<html><center>Pay your bill: <br><b>PKR " + totalBill + "</b></center></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);

        JButton payButton = new JButton("Pay Now");
        payButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Payment successful!");
            this.dispose(); // close payment window
        });

        add(payButton, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
