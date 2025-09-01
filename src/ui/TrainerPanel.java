package ui;
import db.DB;


import ui.TableUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TrainerPanel extends JPanel {
    private JTable table;
    private JTextField nameField, specField, phoneField, maxField;
    private JTextField memberIdField, trainerIdField, specReassignField, specAutoField, memberIdAutoField;

    public TrainerPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        // --------- TOP: Actions ---------
        JPanel top = new JPanel(new GridLayout(3, 1, 5, 5));

        // Add trainer
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(new JLabel("Name:"));
        nameField = new JTextField(10);
        addPanel.add(nameField);

        addPanel.add(new JLabel("Spec:"));
        specField = new JTextField(8);
        addPanel.add(specField);

        addPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField(10);
        addPanel.add(phoneField);

        addPanel.add(new JLabel("Max:"));
        maxField = new JTextField(5);
        addPanel.add(maxField);

        JButton addBtn = new JButton("Add Trainer");
        addBtn.addActionListener(e -> addTrainer());
        addPanel.add(addBtn);
        top.add(addPanel);

        // Reassign trainer
        JPanel reassignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reassignPanel.add(new JLabel("Member ID:"));
        memberIdField = new JTextField(5);
        reassignPanel.add(memberIdField);

        reassignPanel.add(new JLabel("New Trainer ID:"));
        trainerIdField = new JTextField(5);
        reassignPanel.add(trainerIdField);

        reassignPanel.add(new JLabel("Enforce Spec (Y/N):"));
        specReassignField = new JTextField(2);
        reassignPanel.add(specReassignField);

        JButton reBtn = new JButton("Reassign");
        reBtn.addActionListener(e -> reassignTrainer());
        reassignPanel.add(reBtn);
        top.add(reassignPanel);

        // Auto-assign
        JPanel autoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        autoPanel.add(new JLabel("Member ID:"));
        memberIdAutoField = new JTextField(5);
        autoPanel.add(memberIdAutoField);

        autoPanel.add(new JLabel("Specialization:"));
        specAutoField = new JTextField(8);
        autoPanel.add(specAutoField);

        JButton autoBtn = new JButton("Auto-Assign");
        autoBtn.addActionListener(e -> autoAssignTrainer());
        autoPanel.add(autoBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showPanel("dashboard"));
        autoPanel.add(backBtn);

        top.add(autoPanel);

        add(top, BorderLayout.NORTH);

        // --------- CENTER: Trainer Table ---------
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --------- Load data initially ---------
        refreshTable();
    }

    private void refreshTable() {
        try (Connection c = DB.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM vw_trainer_load")) {
            TableUtils.fill(table, rs);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void addTrainer() {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO trainer (name, specialization, phone, max_members) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, nameField.getText());
            ps.setString(2, specField.getText());
            ps.setString(3, phoneField.getText());
            ps.setInt(4, Integer.parseInt(maxField.getText()));
            ps.executeUpdate();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void reassignTrainer() {
        try (Connection c = DB.get();
             CallableStatement cs = c.prepareCall("{call reassign_trainer(?, ?, ?)}")) {
            cs.setInt(1, Integer.parseInt(memberIdField.getText()));
            cs.setInt(2, Integer.parseInt(trainerIdField.getText()));
            cs.setString(3, specReassignField.getText().trim().isEmpty() ? "N" : specReassignField.getText().toUpperCase());
            cs.execute();
            JOptionPane.showMessageDialog(this, "Trainer reassigned successfully.");
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void autoAssignTrainer() {
        try (Connection c = DB.get();
             CallableStatement cs = c.prepareCall("{call auto_assign_trainer_for_member(?, ?)}")) {
            cs.setInt(1, Integer.parseInt(memberIdAutoField.getText()));
            cs.setString(2, specAutoField.getText());
            cs.execute();
            JOptionPane.showMessageDialog(this, "Trainer auto-assigned successfully.");
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
