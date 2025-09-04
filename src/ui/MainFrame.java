package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout card;
    private JPanel container;

    public MainFrame() {
        setTitle("Gym Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        card = new CardLayout();
        container = new JPanel(card);

 
        container.add(new Dashboard(this), "dashboard");
        container.add(new MemberPanel(this), "members");
        container.add(new TrainerPanel(this), "trainers");
        container.add(new WorkoutPanel(this), "workouts");
        container.add(new PaymentPanel(this), "payments");
        container.add(new ReportsPanel(this), "reports");

        setContentPane(container);
        card.show(container, "dashboard");
    }

    public void showPanel(String name) {
        card.show(container, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}