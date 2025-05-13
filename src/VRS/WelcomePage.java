package VRS;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WelcomePage extends JFrame {

    JLabel titleLabel, imageLabel;
    JButton loginButton, signupButton;
    Font titleFont, buttonFont;

    public WelcomePage() {
        super("Vehicle Rental System");
        setLocation(400, 200);
        setSize(600, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        titleFont = new Font("Arial", Font.BOLD, 25);
        buttonFont = new Font("Arial", Font.PLAIN, 18);

        titleLabel = new JLabel("Welcome to Vehicle Rental System!");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(titleFont);

        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("Images/vrsimg.jpeg"));
        Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
        imageLabel = new JLabel(new ImageIcon(img));

        // Create Sign Up button
        signupButton = new JButton("Sign Up");
        signupButton.setFont(buttonFont);
        signupButton.setPreferredSize(new Dimension(120, 40));

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Signup().setVisible(true);
                setVisible(false);
            }
        });

        loginButton = new JButton("Login");
        loginButton.setFont(buttonFont);
        loginButton.setPreferredSize(new Dimension(120, 40));


        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Login().setVisible(true);
                setVisible(false);
            }
        });

        // Panel for buttons - stacked vertically
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        gbc.gridy = 0;
        buttonPanel.add(signupButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(loginButton, gbc);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(10, 20, 10, 20);

        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.gridwidth = 2;
        mainGbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, mainGbc);

        mainGbc.gridx = 0;
        mainGbc.gridy = 1;
        mainGbc.gridwidth = 1;
        mainGbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(buttonPanel, mainGbc);

        mainGbc.gridx = 1;
        mainGbc.gridy = 1;
        mainGbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(imageLabel, mainGbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        new WelcomePage().setVisible(true);
    }
}
