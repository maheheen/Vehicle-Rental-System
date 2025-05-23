package VRS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookVehicle extends JFrame implements ActionListener {

    private JTextField startDateField, endDateField;
    private JButton showVehiclesButton;
    private JTextArea resultArea;

    public BookVehicle() {
        setTitle("Book a Vehicle");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Heading
        JLabel heading = new JLabel("Book Your Vehicle", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(heading, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Start Date (yyyy-mm-dd):"));
        startDateField = new JTextField();
        formPanel.add(startDateField);

        formPanel.add(new JLabel("End Date (yyyy-mm-dd):"));
        endDateField = new JTextField();
        formPanel.add(endDateField);

        formPanel.add(new JLabel()); // Spacer
        showVehiclesButton = new JButton("Show Available Vehicles");
        showVehiclesButton.addActionListener(this);
        formPanel.add(showVehiclesButton);

        add(formPanel, BorderLayout.CENTER);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String start = startDateField.getText().trim();
        String end = endDateField.getText().trim();

        if (start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both start and end dates.");
            return;
        }

        // Simulate available vehicle logic
        String result = "Available vehicles from " + start + " to " + end + ":\n"
                + "- Toyota Corolla\n"
                + "- Honda Civic\n"
                + "- BMW X5";
        resultArea.setText(result);
    }

    // For testing independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookVehicle::new);
    }
}
