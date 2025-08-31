
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gym Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the layout
        frame.setLayout(new FlowLayout());

        // Title
        JLabel titleLabel = new JLabel("Gym Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(titleLabel);

        // Buttons
        JButton memberBtn = new JButton("Member Management");
        JButton trainerBtn = new JButton("Trainer Assignment");
        JButton workoutBtn = new JButton("Workout Plans");
        JButton paymentBtn = new JButton("Payment & Billing");
        JButton reportBtn = new JButton("Reports & Analytics");

        // Add action listeners for button clicks
        memberBtn.addActionListener(e -> new MemberManagement());
        trainerBtn.addActionListener(e -> new TrainerAssignment());
        workoutBtn.addActionListener(e -> new WorkoutPlanManagement());
        paymentBtn.addActionListener(e -> new PaymentBilling());
        reportBtn.addActionListener(e -> new ReportsAnalytics());

        // Add buttons to the frame
        frame.add(memberBtn);
        frame.add(trainerBtn);
        frame.add(workoutBtn);
        frame.add(paymentBtn);
        frame.add(reportBtn);

        // Display the frame
        frame.setVisible(true);
    }
}
