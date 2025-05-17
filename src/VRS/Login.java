package VRS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        if(e.getSource() == cancelButton){
            this.setVisible(false);
            new WelcomePage().setVisible(true);
        }

        if (e.getSource() == loginButton) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.");
                return;
            }

            int roleID = getUserRole(username, password);
            if (roleID == 1) {
                this.setVisible(false);
                new AdminPortal().setVisible(true);
            } else if (roleID == 3) {
                this.setVisible(false);
                new CustomerPortal().setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Your username or password is incorrect :(");
            }
        }

    }
    public int getUserRole(String username, String password){
        int roleId = -1;
        try{
        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=VehicleRentalSystem;encrypt=true;trustServerCertificate=true";
        String query = "SELECT RoleId FROM LoginTB WHERE Username = ? AND PasswordHash = ?";
        String dbUser= "sa";
        String dbPass = "dblab";

        Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);

        ResultSet rs = statement.executeQuery();

        if(rs.next()) {
            roleId = rs.getInt("RoleID");
        }
            rs.close();
            statement.close();
            conn.close();

    } catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        return roleId;

    }



    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}
