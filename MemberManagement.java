

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class MemberManagement {
    private JFrame frame;
    private JTextField nameField, ageField, phoneField, addressField;
    private JComboBox<String> genderBox, planBox, trainerBox;

    public MemberManagement() {
        frame = new JFrame("Member Management");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set layout
        frame.setLayout(new FlowLayout());

        // Form Fields
        nameField = new JTextField(20);
        ageField = new JTextField(20);
        phoneField = new JTextField(20);
        addressField = new JTextField(20);

        genderBox = new JComboBox<>(new String[] {"Male", "Female", "Other"});
        planBox = new JComboBox<>(new String[] {"Weight Loss", "Muscle Gain", "Flexibility"});
        trainerBox = new JComboBox<>(new String[] {"Trainer 1", "Trainer 2", "Trainer 3"});

        // Add action listeners for the buttons
        JButton addBtn = new JButton("Add Member");
        addBtn.addActionListener(e -> addMember());

        JButton editBtn = new JButton("Edit Member");
        JButton deleteBtn = new JButton("Delete Member");

        // Add components to frame
        frame.add(new JLabel("Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Age:"));
        frame.add(ageField);
        frame.add(new JLabel("Gender:"));
        frame.add(genderBox);
        frame.add(new JLabel("Phone:"));
        frame.add(phoneField);
        frame.add(new JLabel("Address:"));
        frame.add(addressField);
        frame.add(new JLabel("Plan:"));
        frame.add(planBox);
        frame.add(new JLabel("Trainer:"));
        frame.add(trainerBox);

        frame.add(addBtn);
        frame.add(editBtn);
        frame.add(deleteBtn);

        frame.setVisible(true);
    }

    // Add member to the database
    private void addMember() {
        Connection conn = Database.connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(frame, 
                "Database connection failed! Please check the database server and try again.", 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String sql = "INSERT INTO members (name, age, gender, phone, address, plan_type, trainer_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, nameField.getText());
                pst.setInt(2, Integer.parseInt(ageField.getText()));
                pst.setString(3, genderBox.getSelectedItem().toString());
                pst.setString(4, phoneField.getText());
                pst.setString(5, addressField.getText());
                pst.setString(6, planBox.getSelectedItem().toString());
                pst.setInt(7, trainerBox.getSelectedIndex() + 1);  // Assume trainer IDs are 1, 2, 3
                pst.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Member added successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, 
                "Database error: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter a valid age (number).", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
