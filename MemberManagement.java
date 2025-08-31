
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class MemberManagement {
    private JFrame frame;
    private JTextField nameField, ageField, phoneField, addressField;
    private JComboBox<String> genderBox, planBox, trainerBox;

    public MemberManagement() {
        frame = new JFrame("Member Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("Member Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(70, 130, 180));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JLabel statusLabel = new JLabel("Fill in member details and click Add Member");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Member Information", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Arial", Font.BOLD, 16), 
            new Color(70, 130, 180)
        );
        formPanel.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize form fields with better styling
        nameField = createStyledTextField(20);
        ageField = createStyledTextField(20);
        phoneField = createStyledTextField(20);
        addressField = createStyledTextField(20);

        genderBox = createStyledComboBox(new String[]{"Male", "Female", "Other"});
        planBox = createStyledComboBox(new String[]{"Weight Loss", "Muscle Gain", "Flexibility", "General Fitness"});
        trainerBox = createStyledComboBox(new String[]{"John Smith", "Sarah Johnson", "Mike Wilson", "Lisa Brown"});

        // Add components with proper layout
        addFormRow(formPanel, gbc, 0, "Full Name:", nameField);
        addFormRow(formPanel, gbc, 1, "Age:", ageField);
        addFormRow(formPanel, gbc, 2, "Gender:", genderBox);
        addFormRow(formPanel, gbc, 3, "Phone Number:", phoneField);
        addFormRow(formPanel, gbc, 4, "Address:", addressField);
        addFormRow(formPanel, gbc, 5, "Fitness Plan:", planBox);
        addFormRow(formPanel, gbc, 6, "Assigned Trainer:", trainerBox);

        return formPanel;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLoweredBevelBorder());
        return comboBox;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 50));
        panel.add(label, gbc);

        // Component
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton addBtn = createStyledButton("Add Member", new Color(34, 139, 34));
        JButton viewBtn = createStyledButton("View Members", new Color(30, 144, 255));
        JButton clearBtn = createStyledButton("Clear Fields", new Color(255, 140, 0));
        JButton closeBtn = createStyledButton("Close", new Color(220, 20, 60));

        // Add action listeners
        addBtn.addActionListener(e -> addMember());
        viewBtn.addActionListener(e -> viewMembers());
        clearBtn.addActionListener(e -> clearFields());
        closeBtn.addActionListener(e -> frame.dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(closeBtn);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        Color originalColor = bgColor;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void viewMembers() {
        // Create a new window to display members
        JFrame viewFrame = new JFrame("View Members");
        viewFrame.setSize(900, 500);
        viewFrame.setLocationRelativeTo(frame);
        viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create table model and table
        String[] columnNames = {"ID", "Name", "Age", "Gender", "Phone", "Address", "Plan", "Trainer"};
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        
        viewFrame.add(scrollPane, BorderLayout.CENTER);
        
        // Add close button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = createStyledButton("Close", new Color(220, 20, 60));
        closeButton.addActionListener(e -> viewFrame.dispose());
        buttonPanel.add(closeButton);
        viewFrame.add(buttonPanel, BorderLayout.SOUTH);
        
        viewFrame.setVisible(true);
        
        // Load data would go here - placeholder for now
        JOptionPane.showMessageDialog(viewFrame, 
            "Member viewing functionality will load data from database here.", 
            "Info", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        phoneField.setText("");
        addressField.setText("");
        genderBox.setSelectedIndex(0);
        planBox.setSelectedIndex(0);
        trainerBox.setSelectedIndex(0);
        
        // Focus on first field
        nameField.requestFocus();
        
        JOptionPane.showMessageDialog(frame, 
            "All fields have been cleared.", 
            "Fields Cleared", 
            JOptionPane.INFORMATION_MESSAGE);
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
