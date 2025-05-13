package VRS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Signup extends JFrame implements ActionListener {
    JLabel l1, l2, l3;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton backButton, signUpButton;
    Font f1, f2;

    public Signup() {
        super("Sign Up - Rentify");
        setLocation(400, 300);
        setSize(530, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f1 = new Font("Arial", Font.BOLD, 25);
        f2 = new Font("Arial", Font.BOLD, 18);

        l1 = new JLabel("Create Your Account");
        l2 = new JLabel("Username:");
        l3 = new JLabel("Password:");

        l1.setHorizontalAlignment(JLabel.CENTER);
        l1.setFont(f1);
        l2.setFont(f2);
        l3.setFont(f2);

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        usernameField.setFont(f2);
        passwordField.setFont(f2);

        backButton = new JButton("Back");
        signUpButton = new JButton("Sign Up");

        backButton.setFont(f2);
        signUpButton.setFont(f2);

        backButton.addActionListener(this);
        signUpButton.addActionListener(this);

        // Panel with GridBagLayout for padding and alignment
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); // Padding around each component
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(l2, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(l3, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(backButton, gbc);

        gbc.gridx = 1;
        formPanel.add(signUpButton, gbc);

        setLayout(new BorderLayout(20, 20));
        add(l1, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signUpButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                ConnectionClass obj = new ConnectionClass();
                // Uncomment below lines when ready to implement DB logic
                // String hashedPassword = PasswordHasher.hashPassword(password);
                // String query = "INSERT INTO login (username, password) VALUES ('" + username + "', '" + hashedPassword + "')";
                // obj.stm.executeUpdate(query);
                // JOptionPane.showMessageDialog(null, "Account Created Successfully!");
                // this.setVisible(false);
                // new Login().setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        } else if (e.getSource() == backButton) {
            new WelcomePage().setVisible(true);
            this.setVisible(false);
        }
    }
}
