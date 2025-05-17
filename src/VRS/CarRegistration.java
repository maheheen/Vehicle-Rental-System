package VRS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class CarRegistration extends JFrame implements ActionListener {

    JTextField regNoField, makeField, modelField;
    JComboBox<String> availabilityBox;
    JTable carTable;
    DefaultTableModel tableModel;
    JButton addButton, editButton, deleteButton, cancelButton;

    public CarRegistration() {
        setTitle("CarRegister");
        setSize(750, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(178, 172, 136, 80)); // Blue with transparency


        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(20, 20, 320, 300);
        formPanel.setBorder(BorderFactory.createEmptyBorder());
        formPanel.setBackground(Color.LIGHT_GRAY);

        JLabel regNoLabel = new JLabel("Car Reg No");
        regNoLabel.setBounds(20, 20, 100, 25);
        formPanel.add(regNoLabel);

        regNoField = new JTextField();
        regNoField.setBounds(130, 20, 150, 25);
        formPanel.add(regNoField);

        JLabel makeLabel = new JLabel("Make");
        makeLabel.setBounds(20, 60, 100, 25);
        formPanel.add(makeLabel);

        makeField = new JTextField();
        makeField.setBounds(130, 60, 150, 25);
        formPanel.add(makeField);

        JLabel modelLabel = new JLabel("Model");
        modelLabel.setBounds(20, 100, 100, 25);
        formPanel.add(modelLabel);

        modelField = new JTextField();
        modelField.setBounds(130, 100, 150, 25);
        formPanel.add(modelField);

        JLabel availabilityLabel = new JLabel("Available");
        availabilityLabel.setBounds(20, 140, 100, 25);
        formPanel.add(availabilityLabel);

        availabilityBox = new JComboBox<>(new String[]{"Yes", "No"});
        availabilityBox.setBounds(130, 140, 150, 25);
        formPanel.add(availabilityBox);

        addButton = new JButton("Add");
        addButton.setBounds(10, 190, 70, 30);
        formPanel.add(addButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(83, 190, 80, 30);
        formPanel.add(cancelButton);

        editButton = new JButton("Edit");
        editButton.setBounds(166, 190, 70, 30);
        formPanel.add(editButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(238, 190, 75, 30);
        formPanel.add(deleteButton);


        tableModel = new DefaultTableModel(new String[]{"CarRegNo", "Make", "Model", "Available"}, 0);
        carTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(carTable);
        tableScroll.setBounds(360, 20, 360, 300);
        add(tableScroll);

        add(formPanel);


        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }

    private void clearForm() {
        regNoField.setText("");
        makeField.setText("");
        modelField.setText("");
        availabilityBox.setSelectedIndex(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String regNo = regNoField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String available = (String) availabilityBox.getSelectedItem();

            if (regNo.isEmpty() || make.isEmpty() || model.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            tableModel.addRow(new Object[]{regNo, make, model, available});
            clearForm();
        } else if (e.getSource() == cancelButton) {
            clearForm();
        } else if (e.getSource() == deleteButton) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        } else if (e.getSource() == editButton) {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow >= 0) {
                regNoField.setText((String) tableModel.getValueAt(selectedRow, 0));
                makeField.setText((String) tableModel.getValueAt(selectedRow, 1));
                modelField.setText((String) tableModel.getValueAt(selectedRow, 2));
                availabilityBox.setSelectedItem((String) tableModel.getValueAt(selectedRow, 3));
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            }
        }
    }

    public static void main(String[] args) {
        new CarRegistration().setVisible(true);
    }
}
