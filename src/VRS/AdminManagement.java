package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
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

        // Disable input fields — they’re only for display on row selection
        txtUsername.setEnabled(true);
        txtPassword.setEnabled(true);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Role:"));
        formPanel.add(new JLabel("Admin (default)"));

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
            dispose();
            new AdminPortal(currentUserRoleID).setVisible(true);
        });

        adminTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fillFormFromTable();
            }
        });

        // Role-based permissions
        if (currentUserRoleID != 3) {
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
        if (currentUserRoleID != 3) {
            JOptionPane.showMessageDialog(this, "Only SuperAdmin can add admins.");
            return;
        }

        String username = "admin_" + generateRandomString(5).toLowerCase();
        String rawPassword = generateRandomString(8);
        String hashedPassword = PasswordHasher.hashPassword(rawPassword);
        int roleID = 2;

        try {
            CallableStatement stmt = conn.prepareCall("{ call AddAdmin(?, ?, ?) }");
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setInt(3, roleID);
            stmt.executeUpdate();

            loadAdmins();

            // Show generated credentials
            JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
            JLabel userLabel = new JLabel("Username: " + username);
            JLabel passLabel = new JLabel("Password: " + rawPassword);
            JButton copyBtn = new JButton("Copy Password");

            copyBtn.addActionListener(e -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                        new StringSelection(rawPassword), null);
                JOptionPane.showMessageDialog(this, "Password copied to clipboard.");
            });

            panel.add(userLabel);
            panel.add(passLabel);
            panel.add(copyBtn);

            JOptionPane.showMessageDialog(this, panel, "Admin Created", JOptionPane.INFORMATION_MESSAGE);

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

        String finalPassword;

        if (password.isEmpty()) {
            // Fetch current password from the DB
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT PasswordHash FROM UserLogin WHERE LoginID = ?");
                ps.setInt(1, loginID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    finalPassword = rs.getString("PasswordHash");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not retrieve existing password.");
                    return;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error fetching current password: " + ex.getMessage());
                return;
            }
        } else {
            finalPassword = PasswordHasher.hashPassword(password);
        }

        try {
            CallableStatement stmt = conn.prepareCall("{ call UpdateAdmins(?, ?, ?, ?, ?) }");
            stmt.setInt(1, loginID);
            stmt.setString(2, username);
            stmt.setString(3, finalPassword);
            stmt.setInt(4, 2); // Admin role fixed
            stmt.setInt(5, currentUserRoleID); // For permission check

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
        String username = adminTable.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove admin: " + username + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            CallableStatement stmt = conn.prepareCall("{ call RemoveAdmin(?, ?) }");
            stmt.setInt(1, loginID);
            stmt.setInt(2, currentUserRoleID);

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
            txtPassword.setText(""); // Password not visible for security
        }
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        new AdminManagement(3).setVisible(true); // SuperAdmin
    }
}
