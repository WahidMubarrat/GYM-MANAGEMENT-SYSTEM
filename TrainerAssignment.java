

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TrainerAssignment {
    private JFrame frame;

    public TrainerAssignment() {
        frame = new JFrame("Trainer Assignment");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Components and layout
        frame.setLayout(new FlowLayout());

        JComboBox<String> specializationBox = new JComboBox<>(new String[] {"Weight Loss", "Muscle Gain"});
        JTextField memberIdField = new JTextField(20);
        JButton autoAssignBtn = new JButton("Auto Assign Trainer");
        JButton manualAssignBtn = new JButton("Manual Assign Trainer");

        autoAssignBtn.addActionListener(e -> autoAssignTrainer(memberIdField.getText()));
        manualAssignBtn.addActionListener(e -> manualAssignTrainer(memberIdField.getText()));

        frame.add(specializationBox);
        frame.add(new JLabel("Member ID:"));
        frame.add(memberIdField);
        frame.add(autoAssignBtn);
        frame.add(manualAssignBtn);

        frame.setVisible(true);
    }

    // Auto-assign trainer based on specialization
    private void autoAssignTrainer(String memberId) {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT trainer_id FROM trainers WHERE specialization = 'Weight Loss' FETCH FIRST 1 ROWS ONLY";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    int trainerId = rs.getInt("trainer_id");
                    // Assign trainer to member (update query)
                    String updateSql = "UPDATE members SET trainer_id = ? WHERE member_id = ?";
                    try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
                        updatePst.setInt(1, trainerId);
                        updatePst.setInt(2, Integer.parseInt(memberId));
                        updatePst.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Manual assign trainer logic
    private void manualAssignTrainer(String memberId) {
        // Similar to auto-assign, but user selects the trainer from a dropdown
    }
}
