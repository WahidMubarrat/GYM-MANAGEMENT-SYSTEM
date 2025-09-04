package ui;


import db.DB;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class WorkoutPanel extends JPanel {
    private JTextField txtMemberId, txtDescription, txtDaysPerWeek;
    private JTextField txtExercise, txtDuration, txtReps;
    private JTable table;
    private int currentWorkoutPlanId = -1; // store last created plan
    private MainFrame parent;

    public WorkoutPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        // ==== TOP FORM (Workout Plan) ====
        JPanel planPanel = new JPanel(new GridLayout(0,2,5,5));
        planPanel.setBorder(BorderFactory.createTitledBorder("Create Workout Plan"));
        planPanel.add(new JLabel("Member ID:"));
        txtMemberId = new JTextField();
        planPanel.add(txtMemberId);

        planPanel.add(new JLabel("Description:"));
        txtDescription = new JTextField();
        planPanel.add(txtDescription);

        planPanel.add(new JLabel("Days per Week:"));
        txtDaysPerWeek = new JTextField();
        planPanel.add(txtDaysPerWeek);

        JButton btnCreatePlan = new JButton("Create Plan");
        btnCreatePlan.addActionListener(e -> createPlan());
        planPanel.add(btnCreatePlan);

        // ==== MIDDLE FORM (Exercise Entry) ====
        JPanel exPanel = new JPanel(new GridLayout(0,2,5,5));
        exPanel.setBorder(BorderFactory.createTitledBorder("Add Exercise"));
        exPanel.add(new JLabel("Exercise Name:"));
        txtExercise = new JTextField();
        exPanel.add(txtExercise);

        exPanel.add(new JLabel("Duration (min):"));
        txtDuration = new JTextField();
        exPanel.add(txtDuration);

        exPanel.add(new JLabel("Repetitions:"));
        txtReps = new JTextField();
        exPanel.add(txtReps);

        JButton btnAddEx = new JButton("Add Exercise");
        btnAddEx.addActionListener(e -> addExercise());
        exPanel.add(btnAddEx);

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.addActionListener(e -> parent.showPanel("dashboard"));
        exPanel.add(btnBack);

        // ==== TABLE (Display Data) ====
        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);

        // Layout
        JPanel top = new JPanel(new GridLayout(1,2));
        top.add(planPanel);
        top.add(exPanel);
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refreshTable();
    }

    // ====== METHODS ======

    private void createPlan() {
        try (Connection c = DB.get()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO Workout_Plan (WorkoutPlan_ID, Member_ID, Description, Days_Per_Week) " +
                "VALUES (seq_workoutplan.NEXTVAL, ?, ?, ?)",
                new String[]{"WorkoutPlan_ID"}
            );
            ps.setInt(1, Integer.parseInt(txtMemberId.getText().trim()));
            ps.setString(2, txtDescription.getText().trim());
            ps.setInt(3, Integer.parseInt(txtDaysPerWeek.getText().trim()));
            ps.executeUpdate();

            // get generated plan id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) currentWorkoutPlanId = rs.getInt(1);

            JOptionPane.showMessageDialog(this, "Workout Plan created (ID = " + currentWorkoutPlanId + ")");
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

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

            // Exercise Name
            String exName = txtExercise.getText().trim();
            if (exName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Exercise name required!");
                return;
            }
            ps.setString(2, exName);

            // Duration
            if (txtDuration.getText().trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, Integer.parseInt(txtDuration.getText().trim()));
            }

            // Reps
            if (txtReps.getText().trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, Integer.parseInt(txtReps.getText().trim()));
            }

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Exercise added successfully!");
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void refreshTable() {
        try (Connection c = DB.get()) {
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT wp.WorkoutPlan_ID, wp.Member_ID, wp.Description, wp.Days_Per_Week, " +
                "w.Exercise_Name, w.Duration_Minutes, w.Repetitions " +
                "FROM Workout_Plan wp LEFT JOIN Work_Out w ON wp.WorkoutPlan_ID = w.WorkoutPlan_ID " +
                "ORDER BY wp.WorkoutPlan_ID"
            );
            TableUtils.fill(table, rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}