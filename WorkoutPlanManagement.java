
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class WorkoutPlanManagement {
    private JFrame frame;
    private JTextField memberIdField, memberNameField, planNameField, exerciseNameField, durationField, repsField, setsField;
    private JComboBox<String> difficultyBox, exerciseTypeBox, memberPlanBox;
    private JTable workoutTable, exerciseTable;
    private DefaultTableModel workoutTableModel, exerciseTableModel;
    private JSpinner daysPerWeekSpinner;

    public WorkoutPlanManagement() {
        frame = new JFrame("Workout Plan Management");
        frame.setSize(1100, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("Workout Plan Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Top Panel - Plan Management
        JPanel topPanel = createPlanManagementPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel - Tables
        JPanel centerPanel = createTablesPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(70, 130, 180));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JLabel statusLabel = new JLabel("Create and manage personalized workout plans for gym members");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Load initial data
        loadWorkoutPlans();
        loadMembers();

        frame.setVisible(true);
    }

    private JPanel createPlanManagementPanel() {
        JPanel planPanel = new JPanel(new BorderLayout());
        planPanel.setBackground(new Color(240, 248, 255));
        
        // Create Plan Panel
        JPanel createPlanPanel = new JPanel(new GridBagLayout());
        createPlanPanel.setBackground(new Color(240, 248, 255));
        TitledBorder createBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Create New Workout Plan",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        createPlanPanel.setBorder(createBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Create plan components
        memberIdField = createStyledTextField(12);
        memberNameField = createStyledTextField(15);
        memberNameField.setEditable(false);
        planNameField = createStyledTextField(20);
        difficultyBox = createStyledComboBox(new String[]{
            "Beginner", "Intermediate", "Advanced", "Expert"
        });
        daysPerWeekSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 7, 1));
        daysPerWeekSpinner.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton searchMemberBtn = createStyledButton("Search", new Color(30, 144, 255));
        JButton createPlanBtn = createStyledButton("Create Plan", new Color(34, 139, 34));

        searchMemberBtn.addActionListener(e -> searchMember());
        createPlanBtn.addActionListener(e -> createWorkoutPlan());

        // Layout create plan components
        addFormRow(createPlanPanel, gbc, 0, "Member ID:", memberIdField, searchMemberBtn);
        addFormRow(createPlanPanel, gbc, 1, "Member Name:", memberNameField);
        addFormRow(createPlanPanel, gbc, 2, "Plan Name:", planNameField);
        addFormRow(createPlanPanel, gbc, 3, "Difficulty Level:", difficultyBox);
        addFormRow(createPlanPanel, gbc, 4, "Days per Week:", daysPerWeekSpinner);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        createPlanPanel.add(createPlanBtn, gbc);

        // Exercise Management Panel
        JPanel exercisePanel = new JPanel(new GridBagLayout());
        exercisePanel.setBackground(new Color(240, 248, 255));
        TitledBorder exerciseBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Add Exercises to Plan",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        exercisePanel.setBorder(exerciseBorder);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 8, 8, 8);
        gbc2.anchor = GridBagConstraints.WEST;

        // Exercise components
        memberPlanBox = createStyledComboBox(new String[]{"Select Plan..."});
        exerciseNameField = createStyledTextField(15);
        exerciseTypeBox = createStyledComboBox(new String[]{
            "Cardio", "Strength", "Flexibility", "Balance", "HIIT", "Endurance"
        });
        durationField = createStyledTextField(8);
        repsField = createStyledTextField(8);
        setsField = createStyledTextField(8);

        JButton addExerciseBtn = createStyledButton("Add Exercise", new Color(255, 140, 0));
        JButton removeExerciseBtn = createStyledButton("Remove Exercise", new Color(220, 20, 60));

        addExerciseBtn.addActionListener(e -> addExerciseToPlan());
        removeExerciseBtn.addActionListener(e -> removeExercise());

        // Layout exercise components
        addFormRow(exercisePanel, gbc2, 0, "Select Plan:", memberPlanBox);
        addFormRow(exercisePanel, gbc2, 1, "Exercise Name:", exerciseNameField);
        addFormRow(exercisePanel, gbc2, 2, "Exercise Type:", exerciseTypeBox);
        addFormRow(exercisePanel, gbc2, 3, "Duration (min):", durationField);
        addFormRow(exercisePanel, gbc2, 4, "Repetitions:", repsField);
        addFormRow(exercisePanel, gbc2, 5, "Sets:", setsField);

        // Button panel for exercises
        JPanel exerciseButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        exerciseButtonPanel.setBackground(new Color(240, 248, 255));
        exerciseButtonPanel.add(addExerciseBtn);
        exerciseButtonPanel.add(removeExerciseBtn);

        gbc2.gridx = 0;
        gbc2.gridy = 6;
        gbc2.gridwidth = 3;
        gbc2.fill = GridBagConstraints.NONE;
        gbc2.anchor = GridBagConstraints.CENTER;
        exercisePanel.add(exerciseButtonPanel, gbc2);

        // Combine both panels
        JPanel combinedPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        combinedPanel.setBackground(new Color(240, 248, 255));
        combinedPanel.add(createPlanPanel);
        combinedPanel.add(exercisePanel);

        planPanel.add(combinedPanel, BorderLayout.CENTER);
        return planPanel;
    }

    private JPanel createTablesPanel() {
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesPanel.setBackground(new Color(240, 248, 255));

        // Workout Plans Table
        JPanel workoutTablePanel = new JPanel(new BorderLayout());
        workoutTablePanel.setBackground(new Color(240, 248, 255));
        
        TitledBorder workoutBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Current Workout Plans",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        workoutTablePanel.setBorder(workoutBorder);

        String[] workoutColumns = {"Plan ID", "Member Name", "Plan Name", "Difficulty", "Days/Week", "Created Date"};
        workoutTableModel = new DefaultTableModel(workoutColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        workoutTable = new JTable(workoutTableModel);
        workoutTable.setFont(new Font("Arial", Font.PLAIN, 11));
        workoutTable.setRowHeight(25);
        workoutTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        workoutTable.getTableHeader().setBackground(new Color(70, 130, 180));
        workoutTable.getTableHeader().setForeground(Color.WHITE);
        workoutTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane workoutScrollPane = new JScrollPane(workoutTable);
        workoutScrollPane.setPreferredSize(new Dimension(500, 250));
        workoutTablePanel.add(workoutScrollPane, BorderLayout.CENTER);

        // Exercise Details Table
        JPanel exerciseTablePanel = new JPanel(new BorderLayout());
        exerciseTablePanel.setBackground(new Color(240, 248, 255));
        
        TitledBorder exerciseBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Exercise Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        exerciseTablePanel.setBorder(exerciseBorder);

        String[] exerciseColumns = {"Exercise Name", "Type", "Duration", "Reps", "Sets", "Plan ID"};
        exerciseTableModel = new DefaultTableModel(exerciseColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        exerciseTable = new JTable(exerciseTableModel);
        exerciseTable.setFont(new Font("Arial", Font.PLAIN, 11));
        exerciseTable.setRowHeight(25);
        exerciseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        exerciseTable.getTableHeader().setBackground(new Color(70, 130, 180));
        exerciseTable.getTableHeader().setForeground(Color.WHITE);
        exerciseTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane exerciseScrollPane = new JScrollPane(exerciseTable);
        exerciseScrollPane.setPreferredSize(new Dimension(500, 250));
        exerciseTablePanel.add(exerciseScrollPane, BorderLayout.CENTER);

        // Table action buttons
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        tableButtonPanel.setBackground(new Color(240, 248, 255));
        
        JButton refreshBtn = createStyledButton("Refresh", new Color(30, 144, 255));
        JButton exportBtn = createStyledButton("Export Plans", new Color(128, 128, 128));
        JButton deleteBtn = createStyledButton("Delete Plan", new Color(220, 20, 60));
        JButton closeBtn = createStyledButton("Close", new Color(169, 169, 169));

        refreshBtn.addActionListener(e -> loadWorkoutPlans());
        exportBtn.addActionListener(e -> exportPlans());
        deleteBtn.addActionListener(e -> deletePlan());
        closeBtn.addActionListener(e -> frame.dispose());

        tableButtonPanel.add(refreshBtn);
        tableButtonPanel.add(exportBtn);
        tableButtonPanel.add(deleteBtn);
        tableButtonPanel.add(closeBtn);

        tablesPanel.add(workoutTablePanel);
        tablesPanel.add(exerciseTablePanel);

        // Add button panel at the bottom
        JPanel mainTablesPanel = new JPanel(new BorderLayout());
        mainTablesPanel.setBackground(new Color(240, 248, 255));
        mainTablesPanel.add(tablesPanel, BorderLayout.CENTER);
        mainTablesPanel.add(tableButtonPanel, BorderLayout.SOUTH);

        return mainTablesPanel;
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
        
        // Adjust button size based on text length
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

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component, JButton button) {
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

        // Button
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 5, 8, 8);
        panel.add(button, gbc);
        
        // Reset insets
        gbc.insets = new Insets(8, 8, 8, 8);
    }

    private void loadWorkoutPlans() {
        // Clear existing data
        workoutTableModel.setRowCount(0);
        memberPlanBox.removeAllItems();
        memberPlanBox.addItem("Select Plan...");
        
        // Add sample workout plan data
        Object[][] samplePlans = {
            {"WP001", "Alice Johnson", "Weight Loss Program", "Beginner", "4", "2025-08-15"},
            {"WP002", "Bob Smith", "Muscle Building", "Intermediate", "5", "2025-08-20"},
            {"WP003", "Carol Davis", "Flexibility Training", "Beginner", "3", "2025-08-25"},
            {"WP004", "David Wilson", "Strength Training", "Advanced", "6", "2025-08-30"},
            {"WP005", "Emma Brown", "Cardio Fitness", "Intermediate", "4", "2025-09-01"}
        };
        
        for (Object[] row : samplePlans) {
            workoutTableModel.addRow(row);
            memberPlanBox.addItem(row[0] + " - " + row[1] + " (" + row[2] + ")");
        }
        
        loadExercises();
    }

    private void loadExercises() {
        // Clear existing exercise data
        exerciseTableModel.setRowCount(0);
        
        // Add sample exercise data
        Object[][] sampleExercises = {
            {"Push-ups", "Strength", "15", "12", "3", "WP001"},
            {"Treadmill", "Cardio", "30", "-", "1", "WP001"},
            {"Bench Press", "Strength", "20", "8", "4", "WP002"},
            {"Squats", "Strength", "25", "10", "3", "WP002"},
            {"Yoga Poses", "Flexibility", "45", "-", "1", "WP003"},
            {"Deadlifts", "Strength", "30", "6", "4", "WP004"},
            {"Cycling", "Cardio", "40", "-", "1", "WP005"}
        };
        
        for (Object[] row : sampleExercises) {
            exerciseTableModel.addRow(row);
        }
    }

    private void loadMembers() {
        // This would normally load from database
        // For now, it's handled in the search functionality
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
            "004:David Wilson", "005:Emma Brown", "006:Frank Miller"
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

    private void createWorkoutPlan() {
        String memberId = memberIdField.getText().trim();
        String memberName = memberNameField.getText().trim();
        String planName = planNameField.getText().trim();
        String difficulty = (String) difficultyBox.getSelectedItem();
        int daysPerWeek = (Integer) daysPerWeekSpinner.getValue();
        
        if (memberId.isEmpty() || memberName.isEmpty() || planName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please fill in all required fields.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (memberName.equals("Member not found")) {
            JOptionPane.showMessageDialog(frame, 
                "Please search for a valid member first.", 
                "Invalid Member", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String message = String.format(
            "Workout plan created successfully!\n\n" +
            "Member: %s (%s)\n" +
            "Plan Name: %s\n" +
            "Difficulty: %s\n" +
            "Days per Week: %d", 
            memberId, memberName, planName, difficulty, daysPerWeek
        );
        
        JOptionPane.showMessageDialog(frame, message, "Plan Created", JOptionPane.INFORMATION_MESSAGE);
        
        // Clear fields
        memberIdField.setText("");
        memberNameField.setText("");
        planNameField.setText("");
        difficultyBox.setSelectedIndex(0);
        daysPerWeekSpinner.setValue(3);
        
        loadWorkoutPlans(); // Refresh the table
    }

    private void addExerciseToPlan() {
        String selectedPlan = (String) memberPlanBox.getSelectedItem();
        String exerciseName = exerciseNameField.getText().trim();
        String exerciseType = (String) exerciseTypeBox.getSelectedItem();
        String duration = durationField.getText().trim();
        String reps = repsField.getText().trim();
        String sets = setsField.getText().trim();
        
        if (selectedPlan == null || selectedPlan.equals("Select Plan...")) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a workout plan first.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (exerciseName.isEmpty() || duration.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter exercise name and duration.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String message = String.format(
            "Exercise added successfully!\n\n" +
            "Plan: %s\n" +
            "Exercise: %s (%s)\n" +
            "Duration: %s minutes\n" +
            "Reps: %s\n" +
            "Sets: %s", 
            selectedPlan, exerciseName, exerciseType, duration, reps, sets
        );
        
        JOptionPane.showMessageDialog(frame, message, "Exercise Added", JOptionPane.INFORMATION_MESSAGE);
        
        // Clear exercise fields
        exerciseNameField.setText("");
        durationField.setText("");
        repsField.setText("");
        setsField.setText("");
        exerciseTypeBox.setSelectedIndex(0);
        
        loadExercises(); // Refresh the exercise table
    }

    private void removeExercise() {
        int selectedRow = exerciseTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select an exercise from the table to remove.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String exerciseName = (String) exerciseTableModel.getValueAt(selectedRow, 0);
        String planId = (String) exerciseTableModel.getValueAt(selectedRow, 5);
        
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to remove this exercise?\n\nExercise: " + exerciseName + "\nPlan ID: " + planId, 
            "Confirm Removal", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            exerciseTableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(frame, 
                "Exercise removed successfully.", 
                "Exercise Removed", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deletePlan() {
        int selectedRow = workoutTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a workout plan from the table to delete.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String planId = (String) workoutTableModel.getValueAt(selectedRow, 0);
        String memberName = (String) workoutTableModel.getValueAt(selectedRow, 1);
        String planName = (String) workoutTableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to delete this workout plan?\n\n" +
            "Plan ID: " + planId + "\n" +
            "Member: " + memberName + "\n" +
            "Plan: " + planName + "\n\n" +
            "This will also remove all associated exercises.", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            workoutTableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(frame, 
                "Workout plan deleted successfully.", 
                "Plan Deleted", 
                JOptionPane.INFORMATION_MESSAGE);
            loadWorkoutPlans(); // Refresh both tables
        }
    }

    private void exportPlans() {
        JOptionPane.showMessageDialog(frame, 
            "Export functionality would save all workout plans and exercises to a file.\n\n" +
            "This feature will export:\n" +
            "• All workout plans with member details\n" +
            "• Complete exercise lists for each plan\n" +
            "• Plan statistics and summaries\n\n" +
            "Feature coming soon!", 
            "Export Workout Plans", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
