package ui;
import db.DB;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.*;

public class PaymentPanel extends JPanel {
    private MainFrame frame;

    private JTextField memberIdField, amountField, notesField;
    private JComboBox<String> methodBox;
    private JTable billingTable;

    public PaymentPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // ----- Form Panel -----
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));

        form.add(new JLabel("Member ID:"));
        memberIdField = new JTextField();
        form.add(memberIdField);

        form.add(new JLabel("Amount:"));
        amountField = new JTextField();
        form.add(amountField);

        form.add(new JLabel("Method:"));
        methodBox = new JComboBox<>(new String[]{"Cash", "Card", "MobileBanking"});
        form.add(methodBox);

        form.add(new JLabel("Notes:"));
        notesField = new JTextField();
        form.add(notesField);

        JButton recordBtn = new JButton("Record Payment");
        recordBtn.addActionListener(this::recordPayment);
        form.add(recordBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.showPanel("dashboard"));
        form.add(backBtn);

        add(form, BorderLayout.NORTH);

        // ----- Table -----
        billingTable = new JTable();
        add(new JScrollPane(billingTable), BorderLayout.CENTER);

        // Initial load
        refreshBilling();
    }

    private void recordPayment(ActionEvent e) {
        try (Connection conn = DB.get();
             CallableStatement cs = conn.prepareCall("{call record_payment(?,?,?,?)}")) {

            cs.setInt(1, Integer.parseInt(memberIdField.getText().trim()));
            cs.setDouble(2, Double.parseDouble(amountField.getText().trim()));
            cs.setString(3, methodBox.getSelectedItem().toString()); // dropdown value
            cs.setString(4, notesField.getText().trim().isEmpty() ? null : notesField.getText().trim());

            cs.execute();
            JOptionPane.showMessageDialog(this, "Payment recorded successfully!");
            refreshBilling();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshBilling() {
        try (Connection conn = DB.get();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM vw_member_billing")) {

            TableUtils.fill(billingTable, rs);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}