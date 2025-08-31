
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class WorkoutPlanManagement {
    private JFrame frame;

    public WorkoutPlanManagement() {
        frame = new JFrame("Workout Plan Management");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Layout and components
        frame.setLayout(new FlowLayout());

        JTextField memberIdField = new JTextField(20);
        JTextField planDescriptionField = new JTextField(20);
        JSpinner daysPerWeekSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 7, 1));

        // Add exercise table
        String[] columns = {"Exercise Name", "Duration (min)", "Repetitions"};
        JTable exerciseTable = new JTable(new DefaultTableModel(columns, 0));

        JButton addPlanBtn = new JButton("Create Plan");
        JButton addExerciseBtn = new JButton("Add Exercise");
        JButton updateExerciseBtn = new JButton("Update Exercise");

        addPlanBtn.addActionListener(e -> createWorkoutPlan(memberIdField, planDescriptionField, daysPerWeekSpinner));
        addExerciseBtn.addActionListener(e -> addExerciseToPlan(exerciseTable));
        updateExerciseBtn.addActionListener(e -> updateExercise(exerciseTable));

        // Add components
        frame.add(new JLabel("Member ID:"));
        frame.add(memberIdField);
        frame.add(new JLabel("Plan Description:"));
        frame.add(planDescriptionField);
        frame.add(new JLabel("Days per Week:"));
        frame.add(daysPerWeekSpinner);
        frame.add(new JScrollPane(exerciseTable));

        frame.add(addPlanBtn);
        frame.add(addExerciseBtn);
        frame.add(updateExerciseBtn);

        frame.setVisible(true);
    }

    private void createWorkoutPlan(JTextField memberIdField, JTextField planDescriptionField, JSpinner daysPerWeekSpinner) {
        // SQL to create the plan for the member
    }

    private void addExerciseToPlan(JTable exerciseTable) {
        // SQL to add exercise to workout plan
    }

    private void updateExercise(JTable exerciseTable) {
        // SQL to update exercise in the workout plan
    }
}
