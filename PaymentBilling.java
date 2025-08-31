import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PaymentBilling {
    private JFrame frame;

    public PaymentBilling() {
        frame = new JFrame("Payment & Billing");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Layout and components
        frame.setLayout(new FlowLayout());

        JTextField memberIdField = new JTextField(20);
        JTextField amountField = new JTextField(20);
        JComboBox<String> paymentMethodBox = new JComboBox<>(new String[] {"Cash", "Card", "Mobile"});

        JButton recordPaymentBtn = new JButton("Record Payment");
        recordPaymentBtn.addActionListener(e -> recordPayment(memberIdField, amountField, paymentMethodBox));

        // Add components
        frame.add(new JLabel("Member ID:"));
        frame.add(memberIdField);
        frame.add(new JLabel("Amount:"));
        frame.add(amountField);
        frame.add(new JLabel("Payment Method:"));
        frame.add(paymentMethodBox);

        frame.add(recordPaymentBtn);

        frame.setVisible(true);
    }

    private void recordPayment(JTextField memberIdField, JTextField amountField, JComboBox<String> paymentMethodBox) {
        try (Connection conn = Database.connect()) {
            String sql = "INSERT INTO payments (member_id, amount, payment_method, payment_date, due_date) VALUES (?, ?, ?, SYSDATE, SYSDATE + 30)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(memberIdField.getText()));
                pst.setDouble(2, Double.parseDouble(amountField.getText()));
                pst.setString(3, paymentMethodBox.getSelectedItem().toString());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Payment recorded successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
