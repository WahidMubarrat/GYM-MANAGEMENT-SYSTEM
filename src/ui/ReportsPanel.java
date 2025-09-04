package ui;
import db.DB;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.*;

public class ReportsPanel extends JPanel {
    private MainFrame frame;
    private JTable table;
    private JComboBox<String> revenueGroupBox;
    private JTextField fromDateField, toDateField;

    public ReportsPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // ----- Top Controls -----
        JPanel controls = new JPanel(new GridLayout(3, 2, 10, 10));

        JButton topTrainersBtn = new JButton("Top Trainers");
        topTrainersBtn.addActionListener(this::showTopTrainers);
        controls.add(topTrainersBtn);

        JButton popularPlansBtn = new JButton("Popular Plans");
        popularPlansBtn.addActionListener(this::showPopularPlans);
        controls.add(popularPlansBtn);

        JButton overdueMembersBtn = new JButton("Overdue Members");
        overdueMembersBtn.addActionListener(this::showOverdueMembers);
        controls.add(overdueMembersBtn);

        // Revenue report controls
        revenueGroupBox = new JComboBox<>(new String[]{"DAY", "MONTH", "YEAR"});
        controls.add(revenueGroupBox);

        fromDateField = new JTextField("2025-01-01"); // yyyy-mm-dd
        controls.add(fromDateField);

        JButton revenueBtn = new JButton("Revenue Report");
        revenueBtn.addActionListener(this::showRevenueReport);
        controls.add(revenueBtn);

        add(controls, BorderLayout.NORTH);

        // ----- Table -----
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showPanel("dashboard"));
        add(backBtn, BorderLayout.SOUTH);
    }

    // ----- Actions -----

    private void showTopTrainers(ActionEvent e) {
        try (Connection conn = DB.get();
             CallableStatement cs = conn.prepareCall("{call report_top_trainers(?, ?)}")) {

            cs.setInt(1, 10); // top 10
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(2);
            TableUtils.fill(table, rs);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showPopularPlans(ActionEvent e) {
        try (Connection conn = DB.get();
             CallableStatement cs = conn.prepareCall("{call report_popular_plans(?, ?)}")) {

            cs.setInt(1, 10);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(2);
            TableUtils.fill(table, rs);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showOverdueMembers(ActionEvent e) {
        try (Connection conn = DB.get();
             CallableStatement cs = conn.prepareCall("{call report_overdue_members(?, ?)}")) {

            cs.setInt(1, 1); // min days overdue
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(2);
            TableUtils.fill(table, rs);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showRevenueReport(ActionEvent e) {
        try (Connection conn = DB.get();
             CallableStatement cs = conn.prepareCall("{call report_revenue(?,?,?,?)}")) {

            Date fromDate = Date.valueOf(fromDateField.getText().trim());
            Date toDate = new Date(System.currentTimeMillis()); // today

            cs.setDate(1, fromDate);
            cs.setDate(2, toDate);
            cs.setString(3, revenueGroupBox.getSelectedItem().toString());
            cs.registerOutParameter(4, Types.REF_CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(4);
            TableUtils.fill(table, rs);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
    }
}