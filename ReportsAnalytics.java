import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReportsAnalytics {
    private JFrame frame;

    public ReportsAnalytics() {
        frame = new JFrame("Reports & Analytics");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Layout and components
        frame.setLayout(new FlowLayout());

        JButton topTrainersBtn = new JButton("Top Trainers");
        JButton popularPlansBtn = new JButton("Popular Plans");
        JButton overdueMembersBtn = new JButton("Overdue Members");
        JButton revenueReportsBtn = new JButton("Revenue Reports");

        topTrainersBtn.addActionListener(e -> showTopTrainers());
        popularPlansBtn.addActionListener(e -> showPopularPlans());
        overdueMembersBtn.addActionListener(e -> showOverdueMembers());
        revenueReportsBtn.addActionListener(e -> showRevenueReports());

        frame.add(topTrainersBtn);
        frame.add(popularPlansBtn);
        frame.add(overdueMembersBtn);
        frame.add(revenueReportsBtn);

        frame.setVisible(true);
    }

    private void showTopTrainers() {
        // SQL to display top trainers
    }

    private void showPopularPlans() {
        // SQL to display popular plans
    }

    private void showOverdueMembers() {
        // SQL to display overdue members
    }

    private void showRevenueReports() {
        // SQL to display revenue reports
    }
}
