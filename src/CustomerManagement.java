import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class CustomerManagement extends JFrame implements ActionListener {
    JTextField customerIDField, firstNameField, lastNameField, emailField, phoneField,
            addressField, licenseField, nidField, loginIDField, userNameField;
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
                "Customer ID", "First Name", "Last Name", "Email", "Phone Number", "Home Address",
                "Driving License #", "National ID #", "Login ID", "Username"
        };

        JTextField[] fields = {
                customerIDField = new JTextField(), firstNameField = new JTextField(),
                lastNameField = new JTextField(), emailField = new JTextField(),
                phoneField = new JTextField(), addressField = new JTextField(),
                licenseField = new JTextField(), nidField = new JTextField(),
                loginIDField = new JTextField(), userNameField = new JTextField()
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
        addButton.setBounds(50, y, 100, 30);
        addButton.addActionListener(this);
        add(addButton);

        cancelButton = new JButton("Clear");
        cancelButton.setBounds(180, y, 100, 30);
        cancelButton.addActionListener(this);
        add(cancelButton);

        tableModel = new DefaultTableModel(new String[]{
                "CustomerID", "FirstName", "LastName", "Email", "Phone", "Address",
                "DLNumber", "NID", "LoginID", "ImagePath", "Username"
        }, 0);

        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBounds(400, 20, 520, 400);
        add(scrollPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String[] values = {
                    customerIDField.getText(), firstNameField.getText(), lastNameField.getText(),
                    emailField.getText(), phoneField.getText(), addressField.getText(),
                    licenseField.getText(), nidField.getText(), loginIDField.getText(),
                    selectedImagePath, userNameField.getText()
            };

            boolean allFilled = true;
            for (int i = 0; i < values.length; i++) {
                if (i != 9 && values[i].isEmpty()) { // image path is optional
                    allFilled = false;
                    break;
                }
            }

            if (allFilled) {
                tableModel.addRow(values);
                JOptionPane.showMessageDialog(this, "Customer added to table!");
            } else {
                JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            }
        } else if (e.getSource() == cancelButton) {
            customerIDField.setText(""); firstNameField.setText(""); lastNameField.setText("");
            emailField.setText(""); phoneField.setText(""); addressField.setText("");
            licenseField.setText(""); nidField.setText(""); loginIDField.setText("");
            userNameField.setText("");
            selectedImagePath = "";
            imagePathLabel.setText("No image selected");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerManagement().setVisible(true));
    }
}
