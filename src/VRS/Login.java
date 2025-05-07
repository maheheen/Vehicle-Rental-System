package VRS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener {
    JLabel titleLabel, usernameLabel, passwordLabel, iconLabel;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, cancelButton;
    JPanel formPanel, titlePanel, iconPanel;
    Font titleFont, labelFont;

    public Login() {
        super("Vehicle Rental System - Login");
        setSize(600, 300);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Fonts
        titleFont = new Font("SansSerif", Font.BOLD, 26);
        labelFont = new Font("SansSerif", Font.PLAIN, 18);

        // Title
        titleLabel = new JLabel("Welcome to Vehicle Rental System", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        usernameLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);

        // Text fields
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        usernameField.setFont(labelFont);
        passwordField.setFont(labelFont);
        usernameField.setMargin(new Insets(5, 10, 5, 10));
        passwordField.setMargin(new Insets(5, 10, 5, 10));

        // Buttons
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        loginButton.setFont(labelFont);
        cancelButton.setFont(labelFont);
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // Icon (if available)
        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("Images/car-icon.png"));
        Image scaledIcon = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        iconLabel = new JLabel(new ImageIcon(scaledIcon));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        iconPanel = new JPanel(new BorderLayout());
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        // Form layout
        formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(cancelButton);

        // Title panel
        titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Final layout
        setLayout(new BorderLayout(10, 10));
        add(titlePanel, BorderLayout.NORTH);
        add(iconPanel, BorderLayout.EAST);
        add(formPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            JOptionPane.showMessageDialog(this, "Login button clicked!");
            // You can add navigation logic here later
        } else if (e.getSource() == cancelButton) {
            this.dispose(); // Close the window
        }
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}
