package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.UUID;

public class CustomerManagement extends JFrame implements ActionListener {
    JTextField firstNameField, lastNameField, emailField, phoneField,
            addressField, licenseField, nidField;
    JButton addButton, updateButton, deleteButton, cancelButton, chooseImageButton, backButton;
    JLabel imagePathLabel;
    JTable customerTable;
    DefaultTableModel tableModel;
    String selectedImagePath = "";
    int selectedLoginId = -1;

    public CustomerManagement() {
        setTitle("Customer Management");
        setSize(1000, 600);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(178, 172, 136, 80));

        // Form Labels and Fields
        String[] labelTexts = {"First Name", "Last Name", "Email", "Phone Number", "Home Address",
                "Driving License #", "National ID #"};
        JTextField[] fields = {
                firstNameField = new JTextField(),
                lastNameField = new JTextField(),
                emailField = new JTextField(),
                phoneField = new JTextField(),
                addressField = new JTextField(),
                licenseField = new JTextField(),
                nidField = new JTextField()
        };

        int y = 20;
        for (int i = 0; i < labelTexts.length; i++) {
            JLabel lbl = new JLabel(labelTexts[i]);
            lbl.setBounds(50, y, 120, 25);
            add(lbl);

            fields[i].setBounds(180, y, 200, 25);
            fields[i].setBorder(BorderFactory.createEmptyBorder());
            add(fields[i]);

            // Add format hint below the Driving License field
            if (i == 5) { // 5 is Driving License #
                JLabel dlHint = new JLabel("Format: 42201-9879905-3#723");
                dlHint.setFont(new Font("Arial", Font.ITALIC, 10));
                dlHint.setForeground(Color.GRAY);
                dlHint.setBounds(180, y + 25, 250, 15);
                add(dlHint);
                y += 15; // Add space for hint label
            }

            y += 40;
        }


        JLabel imageLabel = new JLabel("Profile Image:");
        imageLabel.setBounds(50, y, 120, 25);
        add(imageLabel);

        chooseImageButton = new JButton("Choose Image");
        chooseImageButton.setBounds(180, y, 200, 25);
        chooseImageButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                selectedImagePath = f.getAbsolutePath();
                imagePathLabel.setText(f.getName());
            }
        });
        add(chooseImageButton);

        imagePathLabel = new JLabel("No image selected");
        imagePathLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        imagePathLabel.setBounds(180, y + 25, 200, 20);
        add(imagePathLabel);

        y += 55;

        // Buttons
        addButton     = new JButton("Add");
        updateButton  = new JButton("Update");
        deleteButton  = new JButton("Delete");
        cancelButton  = new JButton("Clear");
        backButton    = new JButton("Back");

        addButton.setBounds(20, y, 80, 30);
        updateButton.setBounds(110, y, 80, 30);
        deleteButton.setBounds(200, y, 80, 30);
        cancelButton.setBounds(290, y, 80, 30);
        backButton.setBounds(20, 500, 80, 30);

        add(addButton); add(updateButton); add(deleteButton); add(cancelButton); add(backButton);

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        cancelButton.addActionListener(this);
        backButton.addActionListener(e -> {
            this.setVisible(false);
            new AdminPortal(2).setVisible(true);
        });

        // Customer Table
        tableModel = new DefaultTableModel(new String[] {
                "FirstName", "LastName", "Email", "Phone", "Address",
                "DLNumber", "NID", "ImagePath", "LoginID"
        }, 0);
        customerTable = new JTable(tableModel);
        JScrollPane sp = new JScrollPane(customerTable);
        sp.setBounds(400, 20, 570, 500);
        add(sp);

        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = customerTable.getSelectedRow();
                firstNameField.setText(tableModel.getValueAt(r, 0).toString());
                lastNameField.setText(tableModel.getValueAt(r, 1).toString());
                emailField.setText(tableModel.getValueAt(r, 2).toString());
                phoneField.setText(tableModel.getValueAt(r, 3).toString());
                addressField.setText(tableModel.getValueAt(r, 4).toString());
                licenseField.setText(tableModel.getValueAt(r, 5).toString());
                nidField.setText(tableModel.getValueAt(r, 6).toString());
                selectedImagePath = tableModel.getValueAt(r, 7).toString();
                imagePathLabel.setText(new File(selectedImagePath).getName());
                selectedLoginId = (int) tableModel.getValueAt(r, 8);
            }
        });

        loadCustomerData();


        // Real-time Enter-key validation for each input field
        firstNameField.addActionListener(e -> {
            String text = firstNameField.getText().trim();
            if (!text.matches("[A-Z][a-zA-Z]*")) {
                JOptionPane.showMessageDialog(this, "First name must start with a capital letter and contain only alphabets.");
                firstNameField.requestFocus();
            } else {
                lastNameField.requestFocus();
            }
        });

        lastNameField.addActionListener(e -> {
            String text = lastNameField.getText().trim();
            if (!text.matches("[A-Z][a-zA-Z]*")) {
                JOptionPane.showMessageDialog(this, "Last name must start with a capital letter and contain only alphabets.");
                lastNameField.requestFocus();
            } else {
                emailField.requestFocus();
            }
        });

        emailField.setToolTipText("Format: example123@gmail.com");
        emailField.addActionListener(e -> {
            String text = emailField.getText().trim();
            if (!text.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                JOptionPane.showMessageDialog(this, "Email must be like firstname.lastname@gmail.com.");
                emailField.requestFocus();

        } else {
                phoneField.requestFocus();
            }
        });

        phoneField.setToolTipText("Format: 03XXXXXXXXX (11 digits)");
        phoneField.addActionListener(e -> {
            String text = phoneField.getText().trim();
            if (!text.matches("03\\d{9}")) {
                JOptionPane.showMessageDialog(this, "Phone must be in format: 03XXXXXXXXX");
                phoneField.requestFocus();
            } else {
                addressField.requestFocus();
            }
        }); // Optional: no strict validation

        addressField.setToolTipText("Cannot be empty");
        addressField.addActionListener(e -> {
            String text = addressField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Address cannot be left empty.");
                addressField.requestFocus();
            } else {
                licenseField.requestFocus();
            }
        }); // Optional

        licenseField.setToolTipText("Format: 42201-9879905-3#723");
        licenseField.addActionListener(e -> {
            String text = licenseField.getText().trim();
            if (!text.matches("\\d{5}-\\d{7}-\\d#\\d{3}")) {
                JOptionPane.showMessageDialog(this, "License must follow format 42201-9879905-3#723.");
                licenseField.requestFocus();
            } else {
                nidField.requestFocus();
            }
        });

        nidField.setToolTipText("Format: 42101-1234567-1");
        nidField.addActionListener(e -> {
            String text = nidField.getText().trim();
            if (!text.matches("\\d{5}-\\d{7}-\\d")) {
                JOptionPane.showMessageDialog(this, "National ID must be in format 42101-1234567-1.");
                nidField.requestFocus();
            } else {
                chooseImageButton.requestFocus();
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection conn = ConnectionClass.getConnection();) {
            if (e.getSource() == addButton) {
                String first = firstNameField.getText().trim();
                String last = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String license = licenseField.getText().trim();
                String nid = nidField.getText().trim();

                if (first.isEmpty() || last.isEmpty() || email.isEmpty() || license.isEmpty() || nid.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all required fields (First, Last, Email, License, NID)");
                    return;
                }

                String username = first.toLowerCase() + "." + last.toLowerCase() + UUID.randomUUID().toString().substring(0, 4);
                String rawPass = UUID.randomUUID().toString().substring(0, 8);
                String hashed = PasswordHasher.hashPassword(rawPass);
                int loginID;

                String filePath = new File(".").getCanonicalPath() + File.separator + "CustomerPasswords.txt";


                try (FileWriter fw = new FileWriter(filePath, true)) {
                    fw.write("Username: " + username + ", Password: " + rawPass + "\n");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to write to CustomerPasswords.txt\n" + ex.getMessage());
                }

                try (PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO UserLogin(Username, PasswordHash, RoleID) VALUES (?, ?, 1)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    pst.setString(1, username);
                    pst.setString(2, hashed);
                    pst.executeUpdate();

                    ResultSet rs = pst.getGeneratedKeys();
                    rs.next();
                    loginID = rs.getInt(1);

                    try (FileWriter fw = new FileWriter(filePath, true)) {
                        fw.write("LoginID: " + loginID + "\n\n");
                    }
                }

                try (PreparedStatement pstC = conn.prepareStatement(
                        "INSERT INTO Customer (FirstName, LastName, Email, PhoneNumber, HomeAddress, " +
                                "DrivingLicenseNumber, NationalIDNumber, CNICImagePath, LoginID) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    pstC.setString(1, first);
                    pstC.setString(2, last);
                    pstC.setString(3, email);
                    pstC.setString(4, phone);
                    pstC.setString(5, address);
                    pstC.setString(6, license);
                    pstC.setString(7, nid);
                    pstC.setString(8, selectedImagePath);
                    pstC.setInt(9, loginID);
                    pstC.executeUpdate();
                }

                JOptionPane.showMessageDialog(this,
                        "Customer added successfully!\n\n🧾 Username: " + username + "\n Password: " + rawPass,
                        "Customer Created", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadCustomerData();

            } else if (e.getSource() == updateButton) {
                if (selectedLoginId == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a customer to update.");
                    return;
                }

                try (PreparedStatement pst = conn.prepareStatement(
                        "UPDATE Customer SET FirstName=?, LastName=?, Email=?, PhoneNumber=?, HomeAddress=?," +
                                "DrivingLicenseNumber=?, NationalIDNumber=?, CNICImagePath=? WHERE LoginID=?")) {
                    pst.setString(1, firstNameField.getText().trim());
                    pst.setString(2, lastNameField.getText().trim());
                    pst.setString(3, emailField.getText().trim());
                    pst.setString(4, phoneField.getText().trim());
                    pst.setString(5, addressField.getText().trim());
                    pst.setString(6, licenseField.getText().trim());
                    pst.setString(7, nidField.getText().trim());
                    pst.setString(8, selectedImagePath);
                    pst.setInt(9, selectedLoginId);
                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Customer updated successfully.");
                clearForm();
                loadCustomerData();

            } else if (e.getSource() == deleteButton) {
                if (selectedLoginId == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this customer?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Customer WHERE LoginID=?")) {
                        ps.setInt(1, selectedLoginId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM UserLogin WHERE LoginID=?")) {
                        ps2.setInt(1, selectedLoginId);
                        ps2.executeUpdate();
                    }
                    JOptionPane.showMessageDialog(this, "Customer deleted.");
                    clearForm();
                    loadCustomerData();
                }

            } else if (e.getSource() == cancelButton) {
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        licenseField.setText("");
        nidField.setText("");
        selectedImagePath = "";
        imagePathLabel.setText("No image selected");
        customerTable.clearSelection();
        selectedLoginId = -1;
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        try (Connection conn = ConnectionClass.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Customer")) {
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("HomeAddress"),
                        rs.getString("DrivingLicenseNumber"),
                        rs.getString("NationalIDNumber"),
                        rs.getString("CNICImagePath"),
                        rs.getInt("LoginID")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerManagement().setVisible(true));
    }
}
