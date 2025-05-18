package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminManagement extends JFrame {
    private JTable adminTable;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnAdd, btnUpdate, btnRemove, btnBack;

    private Connection conn;
    private int currentUserRoleID; // 2 = Admin, 3 = SuperAdmin

    public AdminManagement(int currentUserRoleID) {
        this.currentUserRoleID = currentUserRoleID;

        setTitle("Admin Management");
        setSize(700, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table
        adminTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(adminTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);

        // Role dropdown removed since only Admin can be added
        formPanel.add(new JLabel("Role:"));
        formPanel.add(new JLabel("Admin (default)")); // Just info

        btnAdd = new JButton("Add Admin");
        btnUpdate = new JButton("Update Admin");
        btnRemove = new JButton("Remove Admin");
        btnBack = new JButton("Back");

        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnRemove);
        formPanel.add(btnBack);

        add(formPanel, BorderLayout.SOUTH);

        // Button listeners
        btnAdd.addActionListener(e -> addAdmin());
        btnUpdate.addActionListener(e -> updateAdmin());
        btnRemove.addActionListener(e -> removeAdmin());
        btnBack.addActionListener(e -> {
            dispose(); // close current window
            new AdminPortal(currentUserRoleID).setVisible(true); // pass the role if needed
        });

        adminTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });

        // Limit permissions based on role
        if (currentUserRoleID != 3) { // Not SuperAdmin
            btnUpdate.setEnabled(false);
            btnRemove.setEnabled(false);
        }

        connectDB();
        loadAdmins();
    }

    private void connectDB() {
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            conn = connectionClass.con;

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Connection object is null.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Connection Failed: " + ex.getMessage());
        }
    }

    private void loadAdmins() {
        try {
            CallableStatement stmt = conn.prepareCall("{ call GetAdminUsers }");
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new String[]{"LoginID", "Username", "RoleID"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("LoginID"),
                        rs.getString("Username"),
                        rs.getInt("RoleID")
                });
            }
            adminTable.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading admins: " + ex.getMessage());
        }
    }

    private void addAdmin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.");
            return;
        }

        String hashedPassword = PasswordHasher.hashPassword(password);
        int roleID = 2; // Admin only

        try {
            CallableStatement stmt = conn.prepareCall("{ call AddAdmin(?, ?, ?) }");
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setInt(3, roleID);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Admin added successfully.");
            loadAdmins();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding admin: " + ex.getMessage());
        }
    }

    private void updateAdmin() {
        if (currentUserRoleID != 3) return;

        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        int loginID = (int) adminTable.getValueAt(selectedRow, 0);
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.");
            return;
        }

        try {
            CallableStatement stmt = conn.prepareCall("{ call UpdateAdmins(?, ?, ?, ?, ?) }");
            stmt.setInt(1, loginID);
            stmt.setString(2, username);
            stmt.setString(3, password.isEmpty() ? null : PasswordHasher.hashPassword(password));
            stmt.setInt(4, 2); // Fixed Admin role
            stmt.setInt(5, currentUserRoleID); // <-- SuperAdmin check

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Admin updated.");
            loadAdmins();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating admin: " + ex.getMessage());
        }
    }


    private void removeAdmin() {
        if (currentUserRoleID != 3) return;

        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to remove.");
            return;
        }

        int loginID = (int) adminTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this admin?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            CallableStatement stmt = conn.prepareCall("{ call RemoveAdmin(?, ?) }");
            stmt.setInt(1, loginID);
            stmt.setInt(2, currentUserRoleID); // pass current role for check

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Admin removed.");
            loadAdmins();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error removing admin: " + ex.getMessage());
        }
    }

    private void fillFormFromTable() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow != -1) {
            txtUsername.setText(adminTable.getValueAt(selectedRow, 1).toString());
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        // For testing, assume logged-in user is SuperAdmin (roleID = 3)
        new AdminManagement(3).setVisible(true);
    }
}
