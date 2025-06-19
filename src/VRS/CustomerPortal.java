package VRS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerPortal extends JFrame implements ActionListener {

    private JButton viewAvailableVehiclesButton;
    private JButton bookVehicleButton;
    private JButton viewBookingsButton;
    private JButton logoutButton;

    public CustomerPortal() {
        setTitle("Welcome to Rentify");
        setSize(1000, 600); // reasonable window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Heading
        JLabel heading = new JLabel("Welcome to Rentify", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(heading, BorderLayout.NORTH);

        // Left Panel: Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(220, 100));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        viewAvailableVehiclesButton = new JButton("View Available Vehicles");
        bookVehicleButton = new JButton("Book a Vehicle");

        viewBookingsButton = new JButton("View My Bookings");
        logoutButton = new JButton("Logout");

        buttonPanel.add(viewAvailableVehiclesButton);
        buttonPanel.add(bookVehicleButton);
        buttonPanel.add(viewBookingsButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.WEST);

        // Center Panel: Car Images
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            ImageIcon carIcon1 = new ImageIcon("images/bmw.jpg");
            ImageIcon carIcon2 = new ImageIcon("images/ferr.jpg");

            // Resize images
            Image scaledCar1 = carIcon1.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
            Image scaledCar2 = carIcon2.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);

            JLabel carLabel1 = new JLabel(new ImageIcon(scaledCar1));
            JLabel carLabel2 = new JLabel(new ImageIcon(scaledCar2));

            carLabel1.setHorizontalAlignment(JLabel.CENTER);
            carLabel2.setHorizontalAlignment(JLabel.CENTER);

            centerPanel.add(carLabel1);
            centerPanel.add(carLabel2);
        } catch (Exception e) {
            centerPanel.add(new JLabel("Error loading car images."));
        }

        add(centerPanel, BorderLayout.CENTER);

        // Add listeners
        viewAvailableVehiclesButton.addActionListener(this);
        bookVehicleButton.addActionListener(this);
        viewBookingsButton.addActionListener(this);
        logoutButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == viewAvailableVehiclesButton) {
            JOptionPane.showMessageDialog(this, "Displaying available vehicles...");
        } else if (source == bookVehicleButton) {
            new VehicleBooking().setVisible(true);
        } else if (source == viewBookingsButton) {
            JOptionPane.showMessageDialog(this, "Showing your bookings...");
        } else if (source == logoutButton) {
            JOptionPane.showMessageDialog(this, "Logging out...");
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerPortal::new);
    }
}
