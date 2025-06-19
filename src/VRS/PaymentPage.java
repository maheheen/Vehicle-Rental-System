package VRS;

import javax.swing.*;
import java.awt.*;

public class PaymentPage extends JFrame {

    public PaymentPage(int vehicleID, long amount) {
        setTitle("Payment Page");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel info = new JLabel("<html><center>Proceeding to payment for<br><b>Vehicle ID: " + vehicleID +
                "</b><br>Total Amount: <b>PKR " + amount + "</b></center></html>", SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        add(info, BorderLayout.CENTER);

        JButton payBtn = new JButton("Pay Now");
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(payBtn, BorderLayout.SOUTH);

        setVisible(true);
    }
}
