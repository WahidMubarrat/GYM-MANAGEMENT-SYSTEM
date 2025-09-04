package ui;


import db.DB;




import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class WorkoutPanel extends JPanel {
    private JTextField txtMemberId, txtDesc, txtDays;
    private JTextField txtExercise, txtReps, txtDuration;
    private JTable table;
    private int currentWorkoutPlanId = -1; // stores last created plan

    public WorkoutPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // Top Panel: Plan creation
        JPanel planPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        planPanel.setBorder(BorderFactory.createTitledBorder("Create Workout Plan"));

        txtMemberId = new JTextField();
        txtDesc     = new JTextField();
        txtDays     = new JTextField();

        planPanel.add(new JLabel("Member ID:"));
        planPanel.add(txtMemberId);
        planPanel.add(new JLabel("Description:"));
        planPanel.add(txtDesc);
        planPanel.add(new JLabel("Days/Week:"));
        planPanel.add(txtDays);

        JButton btnCreatePlan = new JButton("Create Plan");
        btnCreatePlan.addActionListener(e -> createPlan());
        planPanel.add(btnCreatePlan);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> frame.showPanel("dashboard"));
        planPanel.add(btnBack);

        // Middle Panel: Add exercises
        JPanel exercisePanel = new JPanel(new GridLayout(4, 2, 5, 5));
        exercisePanel.setBorder(BorderFactory.createTitledBorder("Add Exercise"));

        txtExercise = new JTextField();
        txtReps     = new JTextField();
        txtDuration = new JTextField();

        exercisePanel.add(new JLabel("Exercise Name:"));
        exercisePanel.add(txtExercise);
        exercisePanel.add(new JLabel("Repetitions:"));
        exercisePanel.add(txtReps);
        exercisePanel.add(new JLabel("Duration (min):"));
        exercisePanel.add(txtDuration);

        JButton btnAddExercise = new JButton("Add Exercise");
        btnAddExercise.addActionListener(e -> addExercise());
        exercisePanel.add(btnAddExercise);

        // Bottom Panel: Workout table
        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);

        add(planPanel, BorderLayout.NORTH);
        add(exercisePanel, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);
    }

    // Step 1: Create Workout Plan
    private void createPlan() {
        try (Connection c = DB.get()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO Workout_Plan (WorkoutPlan_ID, Member_ID, Description, Days_Per_Week) " +
                "VALUES (seq_workoutplan.NEXTVAL, ?, ?, ?)",
                new String[]{"WorkoutPlan_ID"}
            );
            ps.setInt(1, Integer.parseInt(txtMemberId.getText()));
            ps.setString(2, txtDesc.getText());
            ps.setInt(3, Integer.parseInt(txtDays.getText()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                currentWorkoutPlanId = rs.getInt(1);
                JOptionPane.showMessageDialog(this, "Plan created with ID: " + currentWorkoutPlanId);
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Step 2: Add exercise to current plan
    private void addExercise() {
        if (currentWorkoutPlanId == -1) {
            JOptionPane.showMessageDialog(this, "Create a plan first!");
            return;
        }
        try (Connection c = DB.get()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO Work_Out (WorkOut_ID, WorkoutPlan_ID, Exercise_Name, Duration_Minutes, Repetitions) " +
                "VALUES (seq_workout.NEXTVAL, ?, ?, ?, ?)"
            );
            ps.setInt(1, currentWorkoutPlanId);
            ps.setString(2, txtExercise.getText());
            ps.setObject(3, txtDuration.getText().isEmpty() ? null : Integer.parseInt(txtDuration.getText()));
            ps.setObject(4, txtReps.getText().isEmpty() ? null : Integer.parseInt(txtReps.getText()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Exercise added!");
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Step 3: Show all workout plans + exercises
    private void refreshTable() {
        try (Connection c = DB.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT wp.WorkoutPlan_ID, wp.Member_ID, wp.Description, wp.Days_Per_Week, " +
                 "w.WorkOut_ID, w.Exercise_Name, w.Duration_Minutes, w.Repetitions " +
                 "FROM Workout_Plan wp LEFT JOIN Work_Out w ON wp.WorkoutPlan_ID = w.WorkoutPlan_ID " +
                 "ORDER BY wp.WorkoutPlan_ID")) {
            TableUtils.fill(table, rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
