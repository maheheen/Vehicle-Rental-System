package VRS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class Registration extends JFrame implements ActionListener {
    JLabel titleLabel, firstNameLabel, lastNameLabel, phoneNumberLabel, emailLabel, drivingLicenseLabel, NationalIDLabel, uploadImageLabel, passwordLabel, RetypePasswordLabel, addressLabel,usernameLabel;
    JTextField firstNameField, lastNameField, phoneNumberField, EmailField, CNICField, drivingLicenseField, addressField, usernameField;
    JPasswordField passwordField1, passwordField2;
    JButton uploadImage, doneButton, backButton;
    Font titleFont, labelFont;
    File selectedImage = null;

    public Registration() {
        super("Create New Account");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        titleFont = new Font("Sanserif", Font.BOLD, 26);
        labelFont = new Font("SansSerif", Font.PLAIN, 18);
        titleLabel = new JLabel("Registration Form", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        firstNameLabel = new JLabel("First Name");
        lastNameLabel = new JLabel("Last Name");
        phoneNumberLabel = new JLabel("Phone Number");
        emailLabel = new JLabel("Email");
        NationalIDLabel = new JLabel("CNIC Number");
        drivingLicenseLabel = new JLabel("Driving License Number");
        addressLabel = new JLabel("Home Address");
        usernameLabel = new JLabel("Username");
        passwordLabel = new JLabel("Password");
        RetypePasswordLabel = new JLabel("Retype Password");
        uploadImageLabel = new JLabel("Upload CNIC Image");


        firstNameLabel.setFont(labelFont);
        lastNameLabel.setFont(labelFont);
        phoneNumberLabel.setFont(labelFont);
        emailLabel.setFont(labelFont);
        NationalIDLabel.setFont(labelFont);
        usernameLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        RetypePasswordLabel.setFont(labelFont);
        uploadImageLabel.setFont(labelFont);
        drivingLicenseLabel.setFont(labelFont);
        addressLabel.setFont(labelFont);


        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        phoneNumberField = new JTextField(20);
        EmailField = new JTextField(20);
        CNICField = new JTextField(20);
        drivingLicenseField = new JTextField(20);
        addressField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField1 = new JPasswordField(20);
        passwordField2 = new JPasswordField(20);



        firstNameField.setFont(labelFont);
        lastNameField.setFont(labelFont);
        phoneNumberField.setFont(labelFont);
        EmailField.setFont(labelFont);
        CNICField.setFont(labelFont);
        usernameField.setFont(labelFont);
        passwordField1.setFont(labelFont);
        passwordField2.setFont(labelFont);
        drivingLicenseField.setFont(labelFont);
        addressField.setFont(labelFont);

        uploadImage = new JButton("Upload Image");
        doneButton = new JButton("Done");
        backButton = new JButton("Back");

        uploadImage.setFont(labelFont);
        doneButton.setFont(labelFont);
        backButton.setFont(labelFont);


        uploadImage.addActionListener(this);
        doneButton.addActionListener(this);
        backButton.addActionListener(this);


        JPanel formPanel = new JPanel(new GridLayout(11, 2, 10, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);
        formPanel.add(phoneNumberLabel);
        formPanel.add(phoneNumberField);
        formPanel.add(emailLabel);
        formPanel.add(EmailField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(NationalIDLabel);
        formPanel.add(CNICField);
        formPanel.add(drivingLicenseLabel);
        formPanel.add(drivingLicenseField);
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField1);
        formPanel.add(RetypePasswordLabel);
        formPanel.add(passwordField2);
        formPanel.add(uploadImageLabel);
        formPanel.add(uploadImage);
        formPanel.add(doneButton);
        formPanel.add(backButton);


        JPanel titlePanel = new JPanel(new BorderLayout(6,20));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel donePanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        donePanel.add(doneButton, BorderLayout.AFTER_LAST_LINE);
        donePanel.add(backButton, BorderLayout.BEFORE_LINE_BEGINS);

        setLayout(new BorderLayout(10, 20));
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(donePanel, BorderLayout.SOUTH);
        add(donePanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadImage) {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select an Image");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImage = fileChooser.getSelectedFile();
                JOptionPane.showMessageDialog(this, "Image selected: " + selectedImage.getName());
            }
        } else if (e.getSource() == doneButton) {

            if (isFormValid()) {
                JOptionPane.showMessageDialog(this, "Registration form submitted successfully!");
                saveCustomerData();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields and upload an image.");

            }
        } else if (e.getSource() == backButton) {
        this.setVisible(false); // Hides current frame
        new Login().setVisible(true); // Assuming 'Registration' is another JFrame class
    }
    }

    private boolean isFormValid() {

        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                phoneNumberField.getText().isEmpty() || EmailField.getText().isEmpty() ||
                CNICField.getText().isEmpty() || passwordField1.getPassword().length == 0 ||
                passwordField2.getPassword().length == 0 || selectedImage == null) {
            return false;
        }

        if (!String.valueOf(passwordField1.getPassword()).equals(String.valueOf(passwordField2.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return false;
        }

        return true;
    }

    private void saveCustomerData() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=VehicleRentalSystem;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "dblab";

        try {

            File imageDir = new File("images");
            if (!imageDir.exists()) {
                imageDir.mkdir();
            }

            String imagePath = "images/" + selectedImage.getName();
            File imageFile = new File(imagePath);

            try (InputStream in = new FileInputStream(selectedImage);
                 OutputStream out = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }

            try (Connection conn = DriverManager.getConnection(url, user, password)) {

                String sql = "{CALL RegisterCustomer(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                try (CallableStatement stmt = conn.prepareCall(sql)) {

                    stmt.setString(1, firstNameField.getText());
                    stmt.setString(2, lastNameField.getText());
                    stmt.setString(3, EmailField.getText());
                    stmt.setString(4, phoneNumberField.getText());
                    stmt.setString(5, addressField.getText());
                    stmt.setString(6, drivingLicenseField.getText());
                    stmt.setString(7, CNICField.getText());
                    stmt.setString(8, imagePath);
                    stmt.setString(9, String.valueOf(passwordField1.getPassword()));

                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Customer data registered successfully!");
                }
            }
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Registration().setVisible(true);
        new Registration().saveCustomerData();
    }
}