package VRS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminPortal extends JFrame implements ActionListener {

    private JButton btnCarRegistration, btnCustomer, btnRental, btnReturn, btnLogout, AdminManagement;
    private JPanel mainPanel;
    int roleID;

    public AdminPortal(int roleID) {
        this.roleID = roleID;
        setTitle("Car Rental Admin Portal");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel titleLabel = new JLabel("Car Rental Admin Portal", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        //titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel WelcomeLabel = new JLabel("Welcome Rafiq!", JLabel.CENTER);
        WelcomeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        //WelcomeLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        WelcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headingPanel.add(titleLabel);
        headingPanel.add(Box.createVerticalStrut(8));
        headingPanel.add(WelcomeLabel);

        add(headingPanel,BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        btnCarRegistration = new JButton("Car Registration");
        btnCustomer = new JButton("Customer");
        btnRental = new JButton("Rental");
        btnReturn = new JButton("Return");
        btnLogout = new JButton("Logout");
        AdminManagement= new JButton("Admin Management");

        buttonPanel.add(btnCarRegistration);
        buttonPanel.add(btnCustomer);
        buttonPanel.add(AdminManagement);
        buttonPanel.add(btnRental);
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnLogout);

        add(buttonPanel, BorderLayout.CENTER);


        btnCarRegistration.addActionListener(this);
        btnCustomer.addActionListener(this);
        btnRental.addActionListener(this);
        btnReturn.addActionListener(this);
        btnLogout.addActionListener(this);
        AdminManagement.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCarRegistration) {
            this.setVisible(false);
            new CarRegistration().setVisible(true);


        } else if (e.getSource() == btnCustomer) {
            this.setVisible(false);
            new CustomerManagement().setVisible(true);

        } else if (e.getSource() == btnRental) {
            new Rental().setVisible(true);

        } else if (e.getSource() == AdminManagement) {
           new AdminManagement(roleID).setVisible(true);

        }

        else if (e.getSource() == btnReturn) {
            JOptionPane.showMessageDialog(this, "Return section clicked.");


        } else if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.setVisible(false);
                new WelcomePage().setVisible(true);
            }

        }
    }

    public static void main(String[] args) {
        new AdminPortal(3).setVisible(true);
    }
}
