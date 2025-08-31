
import java.awt.*;
import javax.swing.*;

public class Dashboard {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gym Management System");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window

        // Set up the main layout
        frame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel titleLabel = new JLabel("Gym Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Main Panel with Grid Layout for buttons
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Create styled buttons
        JButton memberBtn = createStyledButton("Member Management");
        JButton trainerBtn = createStyledButton("Trainer Assignment");
        JButton workoutBtn = createStyledButton("Workout Plans");
        JButton paymentBtn = createStyledButton("Payment & Billing");
        JButton reportBtn = createStyledButton("Reports & Analytics");
        JButton exitBtn = createStyledButton("Exit Application");

        // Add action listeners for button clicks
        memberBtn.addActionListener(e -> new MemberManagement());
        trainerBtn.addActionListener(e -> new TrainerAssignment());
        workoutBtn.addActionListener(e -> new WorkoutPlanManagement());
        paymentBtn.addActionListener(e -> new PaymentBilling());
        reportBtn.addActionListener(e -> new ReportsAnalytics());
        exitBtn.addActionListener(e -> System.exit(0));

        // Add buttons to the main panel
        mainPanel.add(memberBtn);
        mainPanel.add(trainerBtn);
        mainPanel.add(workoutBtn);
        mainPanel.add(paymentBtn);
        mainPanel.add(reportBtn);
        mainPanel.add(exitBtn);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(70, 130, 180));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel statusLabel = new JLabel("Ready - Gym Management System v1.0");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Display the frame
        frame.setVisible(true);
    }
    
    // Helper method to create styled buttons
    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 60));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(65, 105, 225));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
        });
        
        return button;
    }
}
