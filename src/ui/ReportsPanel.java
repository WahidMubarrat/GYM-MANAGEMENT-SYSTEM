package ui;
import db.DB;


import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReportsPanel extends JPanel {
    private JTable table;
    private MainFrame frame;

    public ReportsPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton btnTopTrainers = new JButton("Top Trainers");
        JButton btnPopularPlans = new JButton("Popular Plans");
        JButton btnOverdue = new JButton("Overdue Members");
        JButton btnRevenue = new JButton("Revenue Report");
        JButton btnBack = new JButton("⬅ Back");

        btnTopTrainers.addActionListener(e -> runQuery("SELECT * FROM vw_top_trainers"));
        btnPopularPlans.addActionListener(e -> runQuery("SELECT * FROM vw_popular_plans"));
        btnOverdue.addActionListener(e -> runQuery("SELECT * FROM vw_overdue_members"));
        btnRevenue.addActionListener(e -> runQuery("SELECT * FROM vw_revenue_report"));
        btnBack.addActionListener(e -> frame.showPanel("dashboard"));

        top.add(btnTopTrainers);
        top.add(btnPopularPlans);
        top.add(btnOverdue);
        top.add(btnRevenue);
        top.add(btnBack);

        add(top, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void runQuery(String sql) {
        try (Connection con = DB.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            TableUtils.fill(table, rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
