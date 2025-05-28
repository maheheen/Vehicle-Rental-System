import VRS.ConnectionClass;
import VRS.PasswordHasher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.UUID;

public class CustomerManagement extends JFrame implements ActionListener {
    JTextField firstNameField, lastNameField, emailField, phoneField,
            addressField, licenseField, nidField,  userNameField;
    JButton addButton, cancelButton, chooseImageButton;
    JLabel imagePathLabel;
    JTable customerTable;
    DefaultTableModel tableModel;
    String selectedImagePath = "";

    public CustomerManagement() {
        setTitle("Customer Management");
        setSize(950, 500);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(178, 172, 136, 80));

        String[] labels = {
                 "First Name", "Last Name", "Email", "Phone Number", "Home Address",
                "Driving License #", "National ID #", "Username"
        };

        JTextField[] fields = {
                firstNameField = new JTextField(),
                lastNameField = new JTextField(), emailField = new JTextField(),
                phoneField = new JTextField(), addressField = new JTextField(),
                licenseField = new JTextField(), nidField = new JTextField(),
               userNameField = new JTextField()
        };

        int y = 20;
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            label.setBounds(20, y, 150, 25);
            add(label);

            fields[i].setBounds(180, y, 200, 25);
            fields[i].setBorder(BorderFactory.createEmptyBorder()); // removes border

            add(fields[i]);

            y += 30;
        }

        JLabel imgLabel = new JLabel("Profile Image:");
        imgLabel.setBounds(20, y, 150, 25);
        add(imgLabel);

        chooseImageButton = new JButton("Choose Image");
        chooseImageButton.setBounds(180, y, 200, 25);
        chooseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                selectedImagePath = file.getAbsolutePath();
                imagePathLabel.setText(file.getName());
            }
        });
        add(chooseImageButton);

        imagePathLabel = new JLabel("No image selected");
        imagePathLabel.setBounds(180, y + 25, 200, 20);
        imagePathLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        add(imagePathLabel);

        y += 55;


        addButton = new JButton("Add");
        addButton.setBounds(20, y, 80, 30);
        addButton.addActionListener(this);
        add(addButton);

        cancelButton = new JButton("Clear");
        cancelButton.setBounds(110, y, 80, 30);
        cancelButton.addActionListener(this);
        add(cancelButton);


        JButton updateButton = new JButton("Update");
        updateButton.setBounds(200, y, 80, 30);
        updateButton.addActionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row != -1) {
                try {
                    String email = emailField.getText();
                    ConnectionClass connection = new ConnectionClass();
                    Connection conn = connection.con; // Use your own connection class here
                    String sql = "UPDATE Customer SET FirstName=?, LastName=?, PhoneNumber=?, HomeAddress=?, DrivingLicenseNumber=?, NationalIDNumber=?, ProfileImagePath=?, UserName=? WHERE Email=?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, firstNameField.getText());
                    pst.setString(2, lastNameField.getText());
                    pst.setString(3, phoneField.getText());
                    pst.setString(4, addressField.getText());
                    pst.setString(5, licenseField.getText());
                    pst.setString(6, nidField.getText());
                    pst.setString(7, selectedImagePath);
                    pst.setString(8, userNameField.getText());
                    pst.setString(9, email);
                    pst.executeUpdate();
                    conn.close();
                    loadCustomerData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(updateButton);

// Delete Button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(290, y, 80, 30);
        deleteButton.addActionListener(e -> {
            int row = customerTable.getSelectedRow();
            if (row != -1) {
                String email = emailField.getText();
                try {
                    ConnectionClass connection = new ConnectionClass();
                    Connection conn = connection.con; // Use your own connection class here
                    String sql = "DELETE FROM Customer WHERE Email=?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, email);
                    pst.executeUpdate();
                    conn.close();
                    loadCustomerData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(deleteButton);


        tableModel = new DefaultTableModel(new String[]{
                 "FirstName", "LastName", "Email", "Phone", "Address",
                "DLNumber", "NID", "ImagePath", "Username"
        }, 0);

        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBounds(400, 20, 520, 400);
        add(scrollPane);

        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = customerTable.getSelectedRow();
                firstNameField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                lastNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                phoneField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                addressField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                licenseField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                nidField.setText(tableModel.getValueAt(selectedRow, 6).toString());
                selectedImagePath = tableModel.getValueAt(selectedRow, 7).toString();
                imagePathLabel.setText(new File(selectedImagePath).getName());
                userNameField.setText(tableModel.getValueAt(selectedRow, 8).toString());
            }
        });

        loadCustomerData();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            try {

                String rawPassword = UUID.randomUUID().toString().substring(0, 8); // 8-char password
                String hashedPassword = PasswordHasher.hashPassword(rawPassword);
                String username = userNameField.getText();

                ConnectionClass connectionClass = new ConnectionClass();
                Connection conn = connectionClass.con;

                String loginSQL = "INSERT INTO UserLogin (Username, PasswordHash, RoleID) VALUES (?, ?, 2)";
                PreparedStatement pstLogin = conn.prepareStatement(loginSQL, Statement.RETURN_GENERATED_KEYS);
                pstLogin.setString(1, username);
                pstLogin.setString(2, hashedPassword);
                pstLogin.executeUpdate();

                ResultSet rs = pstLogin.getGeneratedKeys();
                int loginID = -1;
                if (rs.next()) {
                    loginID = rs.getInt(1);
                }


                if (loginID != -1) {
                    String customerSQL = "INSERT INTO Customer (FirstName, LastName, Email, PhoneNumber, HomeAddress, DrivingLicenseNumber, NationalIDNumber, ProfileImagePath, UserName, LoginID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstCustomer = conn.prepareStatement(customerSQL);
                    pstCustomer.setString(1, firstNameField.getText());
                    pstCustomer.setString(2, lastNameField.getText());
                    pstCustomer.setString(3, emailField.getText());
                    pstCustomer.setString(4, phoneField.getText());
                    pstCustomer.setString(5, addressField.getText());
                    pstCustomer.setString(6, licenseField.getText());
                    pstCustomer.setString(7, nidField.getText());
                    pstCustomer.setString(8, selectedImagePath);
                    pstCustomer.setString(9, username);
                    pstCustomer.setInt(10, loginID);
                    pstCustomer.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Customer & Login added.\nPassword: " + rawPassword);
                    loadCustomerData();
                }

                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding customer.");
            }
        } else if (e.getSource() == cancelButton) {
            clearForm();

        }


    }
public void clearForm() {
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
    }



    public void loadCustomerData() {
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection conn = connectionClass.con;
            String query = "SELECT * FROM Customer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("HomeAddress"),
                        rs.getString("DrivingLicenseNumber"),
                        rs.getString("NationalIDNumber"),
                        rs.getString("ProfileImagePath"),
                        rs.getString("UserName")
                });
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerManagement().setVisible(true));
    }
}
