

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class TrainerAssignment {
    private JFrame frame;
    private JTextField memberIdField, memberNameField;
    private JComboBox<String> specializationBox, trainerBox, memberBox;
    private JTable assignmentTable;
    private DefaultTableModel tableModel;

    public TrainerAssignment() {
        frame = new JFrame("Trainer Assignment Management");
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("Trainer Assignment Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Assignment Panel (Top)
        JPanel assignmentPanel = createAssignmentPanel();
        mainPanel.add(assignmentPanel, BorderLayout.NORTH);

        // Table Panel (Center)
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(70, 130, 180));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JLabel statusLabel = new JLabel("Assign trainers to members based on specialization and availability");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Load initial data
        loadTrainers();
        loadMembers();
        loadAssignments();

        frame.setVisible(true);
    }

    private JPanel createAssignmentPanel() {
        JPanel assignmentPanel = new JPanel(new BorderLayout());
        assignmentPanel.setBackground(new Color(240, 248, 255));
        
        // Auto Assignment Panel
        JPanel autoPanel = new JPanel(new GridBagLayout());
        autoPanel.setBackground(new Color(240, 248, 255));
        TitledBorder autoBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Auto Assignment",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        autoPanel.setBorder(autoBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Auto assignment components
        specializationBox = createStyledComboBox(new String[]{
            "Weight Loss", "Muscle Gain", "Flexibility", "Cardio Training", "Strength Training"
        });
        memberBox = createStyledComboBox(new String[]{"Select Member..."});

        JButton autoAssignBtn = createStyledButton("Auto Assign", new Color(34, 139, 34));
        autoAssignBtn.addActionListener(e -> autoAssignTrainer());

        // Layout auto assignment components
        addFormRow(autoPanel, gbc, 0, "Specialization:", specializationBox);
        addFormRow(autoPanel, gbc, 1, "Member:", memberBox);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        autoPanel.add(autoAssignBtn, gbc);

        // Manual Assignment Panel
        JPanel manualPanel = new JPanel(new GridBagLayout());
        manualPanel.setBackground(new Color(240, 248, 255));
        TitledBorder manualBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Manual Assignment",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        manualPanel.setBorder(manualBorder);

        // Manual assignment components
        memberIdField = createStyledTextField(15);
        memberNameField = createStyledTextField(15);
        memberNameField.setEditable(false);
        trainerBox = createStyledComboBox(new String[]{"Select Trainer..."});

        JButton searchBtn = createStyledButton("Search", new Color(30, 144, 255));
        JButton manualAssignBtn = createStyledButton("Assign", new Color(255, 140, 0));
        JButton removeAssignBtn = createStyledButton("Remove", new Color(220, 20, 60));

        searchBtn.addActionListener(e -> searchMember());
        manualAssignBtn.addActionListener(e -> manualAssignTrainer());
        removeAssignBtn.addActionListener(e -> removeAssignment());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 8, 8, 8);
        gbc2.anchor = GridBagConstraints.WEST;

        // Layout manual assignment components with search button
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 0;
        gbc2.fill = GridBagConstraints.NONE;
        JLabel memberIdLabel = new JLabel("Member ID:");
        memberIdLabel.setFont(new Font("Arial", Font.BOLD, 13));
        memberIdLabel.setForeground(new Color(50, 50, 50));
        manualPanel.add(memberIdLabel, gbc2);

        gbc2.gridx = 1;
        gbc2.weightx = 1.0;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        manualPanel.add(memberIdField, gbc2);

        gbc2.gridx = 2;
        gbc2.weightx = 0;
        gbc2.fill = GridBagConstraints.NONE;
        gbc2.insets = new Insets(8, 5, 8, 8);
        manualPanel.add(searchBtn, gbc2);

        // Reset insets for other components
        gbc2.insets = new Insets(8, 8, 8, 8);
        addFormRow(manualPanel, gbc2, 1, "Member Name:", memberNameField);
        addFormRow(manualPanel, gbc2, 2, "Trainer:", trainerBox);

        // Button panel for manual assignment actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(manualAssignBtn);
        buttonPanel.add(removeAssignBtn);

        gbc2.gridx = 0;
        gbc2.gridy = 3;
        gbc2.gridwidth = 3;
        gbc2.fill = GridBagConstraints.NONE;
        gbc2.anchor = GridBagConstraints.CENTER;
        gbc2.weightx = 0;
        manualPanel.add(buttonPanel, gbc2);

        // Combine both panels
        JPanel combinedPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        combinedPanel.setBackground(new Color(240, 248, 255));
        combinedPanel.add(autoPanel);
        combinedPanel.add(manualPanel);

        assignmentPanel.add(combinedPanel, BorderLayout.CENTER);
        return assignmentPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));
        
        TitledBorder tableBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Current Trainer Assignments",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        tablePanel.setBorder(tableBorder);

        // Create table
        String[] columnNames = {"Member ID", "Member Name", "Trainer ID", "Trainer Name", "Specialization", "Assignment Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        assignmentTable = new JTable(tableModel);
        assignmentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        assignmentTable.setRowHeight(25);
        assignmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        assignmentTable.getTableHeader().setBackground(new Color(70, 130, 180));
        assignmentTable.getTableHeader().setForeground(Color.WHITE);
        assignmentTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(assignmentTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Table button panel
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        tableButtonPanel.setBackground(new Color(240, 248, 255));
        
        JButton refreshBtn = createStyledButton("Refresh", new Color(30, 144, 255));
        JButton exportBtn = createStyledButton("Export to CSV", new Color(128, 128, 128));
        JButton closeBtn = createStyledButton("Close", new Color(220, 20, 60));

        refreshBtn.addActionListener(e -> loadAssignments());
        exportBtn.addActionListener(e -> exportToCSV());
        closeBtn.addActionListener(e -> frame.dispose());

        tableButtonPanel.add(refreshBtn);
        tableButtonPanel.add(exportBtn);
        tableButtonPanel.add(closeBtn);

        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);
        return tablePanel;
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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        
        // Adjust button size based on text length - more generous sizing
        int buttonWidth;
        if (text.length() > 15) {
            buttonWidth = 180;
        } else if (text.length() > 12) {
            buttonWidth = 160;
        } else if (text.length() > 8) {
            buttonWidth = 120;
        } else {
            buttonWidth = 100;
        }
        button.setPreferredSize(new Dimension(buttonWidth, 35));
        
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

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

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(50, 50, 50));
        panel.add(label, gbc);

        // Component
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private void loadTrainers() {
        trainerBox.removeAllItems();
        trainerBox.addItem("Select Trainer...");
        
        // Add sample trainers for now
        trainerBox.addItem("John Smith - Weight Training");
        trainerBox.addItem("Sarah Johnson - Cardio & Fitness");
        trainerBox.addItem("Mike Wilson - Yoga & Flexibility");
        trainerBox.addItem("Lisa Brown - Strength Training");
        trainerBox.addItem("David Lee - Muscle Gain");
    }

    private void loadMembers() {
        memberBox.removeAllItems();
        memberBox.addItem("Select Member...");
        
        // Add sample members for now
        memberBox.addItem("001 - Alice Johnson");
        memberBox.addItem("002 - Bob Smith");
        memberBox.addItem("003 - Carol Davis");
        memberBox.addItem("004 - David Wilson");
        memberBox.addItem("005 - Emma Brown");
    }

    private void loadAssignments() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add sample assignment data
        Object[][] sampleData = {
            {"001", "Alice Johnson", "T01", "John Smith", "Weight Training", "2025-08-15"},
            {"002", "Bob Smith", "T02", "Sarah Johnson", "Cardio & Fitness", "2025-08-20"},
            {"003", "Carol Davis", "T03", "Mike Wilson", "Yoga & Flexibility", "2025-08-25"},
            {"005", "Emma Brown", "T01", "John Smith", "Weight Training", "2025-08-30"}
        };
        
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
    }

    private void autoAssignTrainer() {
        String selectedMember = (String) memberBox.getSelectedItem();
        String selectedSpecialization = (String) specializationBox.getSelectedItem();
        
        if (selectedMember == null || selectedMember.equals("Select Member...")) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a member first.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Simulate auto assignment
        String message = String.format(
            "Auto-assigned trainer for %s based on %s specialization.\n\nTrainer: %s", 
            selectedMember, 
            selectedSpecialization,
            getTrainerForSpecialization(selectedSpecialization)
        );
        
        JOptionPane.showMessageDialog(frame, message, "Auto Assignment Complete", JOptionPane.INFORMATION_MESSAGE);
        loadAssignments(); // Refresh the table
    }

    private void manualAssignTrainer() {
        String memberId = memberIdField.getText().trim();
        String selectedTrainer = (String) trainerBox.getSelectedItem();
        
        if (memberId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter a Member ID.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedTrainer == null || selectedTrainer.equals("Select Trainer...")) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a trainer.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String message = String.format(
            "Successfully assigned trainer to member.\n\nMember: %s (%s)\nTrainer: %s", 
            memberId, 
            memberNameField.getText(),
            selectedTrainer
        );
        
        JOptionPane.showMessageDialog(frame, message, "Manual Assignment Complete", JOptionPane.INFORMATION_MESSAGE);
        
        // Clear fields
        memberIdField.setText("");
        memberNameField.setText("");
        trainerBox.setSelectedIndex(0);
        
        loadAssignments(); // Refresh the table
    }

    private void searchMember() {
        String memberId = memberIdField.getText().trim();
        
        if (memberId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter a Member ID to search.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Simulate member search
        String[] sampleMembers = {
            "001:Alice Johnson", "002:Bob Smith", "003:Carol Davis", 
            "004:David Wilson", "005:Emma Brown"
        };
        
        for (String member : sampleMembers) {
            if (member.startsWith(memberId + ":")) {
                memberNameField.setText(member.split(":")[1]);
                return;
            }
        }
        
        memberNameField.setText("Member not found");
        JOptionPane.showMessageDialog(frame, 
            "Member with ID '" + memberId + "' not found.", 
            "Member Not Found", 
            JOptionPane.ERROR_MESSAGE);
    }

    private void removeAssignment() {
        int selectedRow = assignmentTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select an assignment from the table to remove.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String memberName = (String) tableModel.getValueAt(selectedRow, 1);
        String trainerName = (String) tableModel.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to remove the assignment?\n\nMember: " + memberName + "\nTrainer: " + trainerName, 
            "Confirm Removal", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(frame, 
                "Assignment removed successfully.", 
                "Assignment Removed", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportToCSV() {
        JOptionPane.showMessageDialog(frame, 
            "Export functionality would save the assignment data to a CSV file.\n\nFeature coming soon!", 
            "Export to CSV", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String getTrainerForSpecialization(String specialization) {
        switch (specialization) {
            case "Weight Loss": return "Sarah Johnson - Cardio & Fitness";
            case "Muscle Gain": return "David Lee - Muscle Gain";
            case "Flexibility": return "Mike Wilson - Yoga & Flexibility";
            case "Cardio Training": return "Sarah Johnson - Cardio & Fitness";
            case "Strength Training": return "Lisa Brown - Strength Training";
            default: return "John Smith - Weight Training";
        }
    }
}
