package VRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PaymentManagement extends JFrame {
    private JTable paymentTable;
    private JComboBox<String> statusFilter;
    private JButton markAsPaidButton, backButton;
    private Connection con;

    public PaymentManagement() {
        setTitle("Manage Payments");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Filter panel
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Filter by Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Pending", "Paid", "Failed", "Refunded"});
        topPanel.add(statusFilter);

        JButton filterButton = new JButton("Filter");
        topPanel.add(filterButton);

        add(topPanel, BorderLayout.NORTH);

        // Table
        paymentTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with two parts (left for back, right for actions)
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Back button (left side)
        backButton = new JButton("Back");
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(backButton);
        bottomPanel.add(leftPanel, BorderLayout.WEST);

        // Mark as Paid button (right side)
        markAsPaidButton = new JButton("Mark as Paid");
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(markAsPaidButton);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Button listeners
        filterButton.addActionListener(e -> loadPayments());
        markAsPaidButton.addActionListener(e -> markPaymentAsPaid());
        backButton.addActionListener(e -> dispose());  // Closes the current window

        loadPayments();
    }

    private void loadPayments() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "PaymentID", "CustomerID", "Customer Name", "MaskedCard", "Card Holder", "Expiry", "Amount", "Status", "Date"
        });

        try(Connection conn = ConnectionClass.getConnection()) {
//            ConnectionClass connectionClass = new ConnectionClass();
//            con = connectionClass.con;
            String status = statusFilter.getSelectedItem().toString();
            String query = "SELECT * FROM ViewPaymentsWithCustomerInfo";
            if (!status.equals("All")) {
                query += " WHERE Status = ?";
            }

            PreparedStatement ps = con.prepareStatement(query);
            if (!status.equals("All")) {
                ps.setString(1, status);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("PaymentID"),
                        rs.getInt("CustomerID"),
                        rs.getString("CustomerName"),
                        rs.getString("MaskedCardNumber"),
                        rs.getString("CardHolderName"),
                        rs.getInt("ExpiryMonth") + "/" + rs.getInt("ExpiryYear"),
                        rs.getInt("Amount"),
                        rs.getString("Status"),
                        rs.getTimestamp("PaymentDate")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage());
        }

        paymentTable.setModel(model);
    }

    private void markPaymentAsPaid() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment to mark as Paid.");
            return;
        }

        int paymentID = (int) paymentTable.getValueAt(selectedRow, 0);
        String currentStatus = (String) paymentTable.getValueAt(selectedRow, 7);

        if ("Paid".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This payment is already marked as Paid.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Mark PaymentID " + paymentID + " as Paid?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
//            ConnectionClass connectionClass = new ConnectionClass();
//            con = connectionClass.con;
            Connection con = ConnectionClass.getConnection();
            CallableStatement cs = con.prepareCall("{call UpdatePaymentStatus(?, ?)}");
            cs.setInt(1, paymentID);
            cs.setString(2, "Paid");
            cs.execute();

            JOptionPane.showMessageDialog(this, "Payment marked as Paid successfully.");
            loadPayments(); // Refresh table
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating payment: " + ex.getMessage());
        }
    }
}
