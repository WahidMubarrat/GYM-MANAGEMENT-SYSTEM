package ui;
import db.DB;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class PaymentPanel extends JPanel {
    private JTextField txtMember, txtAmount;
    private JTable table;
    private MainFrame frame;

    public PaymentPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0,2));
        txtMember = new JTextField();
        txtAmount = new JTextField();

        form.add(new JLabel("Member ID:")); form.add(txtMember);
        form.add(new JLabel("Amount:")); form.add(txtAmount);

        JButton btnPay = new JButton("Record Payment");
        btnPay.addActionListener(this::recordPayment);
        form.add(btnPay);

        JButton btnBack = new JButton("⬅ Back");
        btnBack.addActionListener(e -> frame.showPanel("dashboard"));
        form.add(btnBack);

        add(form, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnLoad = new JButton("View Payments");
        btnLoad.addActionListener(e -> loadPayments());
        add(btnLoad, BorderLayout.SOUTH);
    }

    private void recordPayment(ActionEvent e) {
        try (Connection con = DB.get();
             CallableStatement cs = con.prepareCall("{call ADD_PAYMENT(?,?)}")) {
            cs.setInt(1, Integer.parseInt(txtMember.getText()));
            cs.setDouble(2, Double.parseDouble(txtAmount.getText()));
            cs.execute();

            JOptionPane.showMessageDialog(this, "✅ Payment recorded!");
            loadPayments();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void loadPayments() {
        try (Connection con = DB.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Payment")) {
            TableUtils.fill(table, rs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
