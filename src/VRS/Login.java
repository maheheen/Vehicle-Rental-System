package VRS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener {
    JLabel titleLabel, usernameLabel, passwordLabel, iconLabel;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, cancelButton, registerButton;
    JPanel formPanel, loginPanel, cancelPanel, registerPanel, titlePanel, iconPanel;
    Font titleFont, labelFont;

    public Login() {
        super("Vehicle Rental System - Login");
        setSize(600, 400);
        setLocationRelativeTo(null);
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
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        usernameField.setFont(labelFont);
        passwordField.setFont(labelFont);
        usernameField.setMargin(new Insets(5, 10, 5, 10));
        passwordField.setMargin(new Insets(5, 10, 5, 10));

        // Buttons
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        registerButton = new JButton("Register");
        loginButton.setFont(labelFont);
        cancelButton.setFont(labelFont);
        registerButton.setFont(labelFont);
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);
        registerButton.addActionListener(this);


        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("Images/car-icon.png"));
        Image scaledIcon = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        iconLabel = new JLabel(new ImageIcon(scaledIcon));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        iconPanel = new JPanel(new BorderLayout());
        iconPanel.add(iconLabel, BorderLayout.CENTER);


        formPanel = new JPanel(new GridLayout(4, 2, 10, 20)); // 4 rows, 2 columns for labels & fields
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));


        formPanel.add(usernameLabel);
        formPanel.add(usernameField);

        formPanel.add(passwordLabel);
        formPanel.add(passwordField);


        titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        loginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 3)); // Center-aligning the button
        loginPanel.add(loginButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);

        setLayout(new BorderLayout(10, 10));
        add(titlePanel, BorderLayout.NORTH);
        add(iconPanel, BorderLayout.EAST);
        add(formPanel, BorderLayout.CENTER);
        add(loginPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            JOptionPane.showMessageDialog(this, "Login button clicked!");

        } else if (e.getSource() == cancelButton) {
            this.dispose();
        } else if (e.getSource() == registerButton) {
            new Registration().setVisible(true);
        }
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}
