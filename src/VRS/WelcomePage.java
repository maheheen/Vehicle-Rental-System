package VRS;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WelcomePage extends JFrame {

    // Components for GUI
    JLabel titleLabel, imageLabel;
    JButton loginButton;
    Font titleFont, buttonFont;

    public WelcomePage() {
        super("Vehicle Rental System");  // Title
        setLocation(400, 200);           // Location on screen
        setSize(600, 350);               // Size of window

        titleFont = new Font("Arial", Font.BOLD, 25);
        buttonFont = new Font("Arial", Font.PLAIN, 18);

        // Title Label (Centered at Top)
        titleLabel = new JLabel("Welcome to Vehicle Rental System!");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(titleFont);

        // Image placement
        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("Images/vrsimg.jpeg")); // Ensure this file exists
        Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH); // Resizing
        imageLabel = new JLabel(new ImageIcon(img));

        // Adding only the login button
        loginButton = new JButton("Login");
        loginButton.setFont(buttonFont);
        loginButton.setPreferredSize(new Dimension(120, 40));

        // Panel for Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(loginButton, gbc);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(10, 20, 10, 20);

        // Title at the top
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.gridwidth = 2;
        mainGbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, mainGbc);

        // Button on the Left
        mainGbc.gridx = 0;
        mainGbc.gridy = 1;
        mainGbc.gridwidth = 1;
        mainGbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(buttonPanel, mainGbc);

        // Image on the Right
        mainGbc.gridx = 1;
        mainGbc.gridy = 1;
        mainGbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(imageLabel, mainGbc);

        // Set Main Layout
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        new WelcomePage().setVisible(true);
    }
}
