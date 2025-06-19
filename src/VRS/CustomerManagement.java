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
            addressField, licenseField, nidField, userNameField;
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
                "Driving License #", "National ID #", "Username"};
        JTextField[] fields = {
                firstNameField = new JTextField(),
                lastNameField = new JTextField(),
                emailField = new JTextField(),
                phoneField = new JTextField(),
                addressField = new JTextField(),
                licenseField = new JTextField(),
                nidField = new JTextField(),
                userNameField = new JTextField()
        };

        int y = 20;
        for (int i = 0; i < labelTexts.length; i++) {
            JLabel lbl = new JLabel(labelTexts[i]);
            lbl.setBounds(50, y, 120, 25);
            add(lbl);

            fields[i].setBounds(180, y, 200, 25);
            fields[i].setBorder(BorderFactory.createEmptyBorder());
            add(fields[i]);

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
        backButton.setBounds(20, 500, 80, 30); // move to bottom left

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
        tableModel = new DefaultTableModel(new String[]{
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
                userNameField.setText(""); // Don't show username on edit
                selectedLoginId = (int) tableModel.getValueAt(r, 8);
            }
        });

        loadCustomerData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection conn = ConnectionClass.getConnection();) {
            if (e.getSource() == addButton) {
                String username = userNameField.getText().trim();
                if (username.isEmpty()) {
                    throw new IllegalArgumentException("Username is mandatory.");
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) FROM UserLogin WHERE Username = ?")) {
                    ps.setString(1, username);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Username already exists.");
                        return;
                    }
                }

                String rawPass = UUID.randomUUID().toString().substring(0, 8);
                String hashed = PasswordHasher.hashPassword(rawPass);

                int loginID;
                try (PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO UserLogin(Username, PasswordHash, RoleID) VALUES (?, ?, 1)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    pst.setString(1, username);
                    pst.setString(2, hashed);
                    pst.executeUpdate();
                    ResultSet gen = pst.getGeneratedKeys();
                    gen.next();
                    loginID = gen.getInt(1);
                }

                try (PreparedStatement pstC = conn.prepareStatement(
                        "INSERT INTO Customer (FirstName, LastName, Email, PhoneNumber, HomeAddress, " +
                                "DrivingLicenseNumber, NationalIDNumber, CNICImagePath, LoginID) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    pstC.setString(1, firstNameField.getText().trim());
                    pstC.setString(2, lastNameField.getText().trim());
                    pstC.setString(3, emailField.getText().trim());
                    pstC.setString(4, phoneField.getText().trim());
                    pstC.setString(5, addressField.getText().trim());
                    pstC.setString(6, licenseField.getText().trim());
                    pstC.setString(7, nidField.getText().trim());
                    pstC.setString(8, selectedImagePath);
                    pstC.setInt(9, loginID);
                    pstC.executeUpdate();
                }

                savePasswordToFile(username, rawPass);
                JOptionPane.showMessageDialog(this, "Customer added! Password: " + rawPass);
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
        userNameField.setText("");
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
                tableModel.addRow(new Object[]{
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

    private void savePasswordToFile(String username, String password) {
        try (FileWriter fw = new FileWriter("CustomerPasswords.txt", true)) {
            fw.write("Username: " + username + ", Password: " + password + "\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to log password to file: " + e.getMessage(), "File Write Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerManagement().setVisible(true));
    }
}
