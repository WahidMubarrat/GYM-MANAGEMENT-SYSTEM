package ui;


import java.awt.*;
import javax.swing.*;

public class Dashboard extends JPanel {
    public Dashboard(MainFrame frame){
        setLayout(new GridLayout(5,1,10,10));

        JButton b1 = new JButton("Manage Members");
        b1.addActionListener(e -> frame.showPanel("members"));
        add(b1);

        JButton b2 = new JButton("Trainer Assignment");
        b2.addActionListener(e -> frame.showPanel("trainers"));
        add(b2);

        JButton b3 = new JButton("Workout Plans");
        b3.addActionListener(e -> frame.showPanel("workouts"));
        add(b3);

        JButton b4 = new JButton("Payments & Billing");
        b4.addActionListener(e -> frame.showPanel("payments"));
        add(b4);

        JButton b5 = new JButton("Reports & Analytics");
        b5.addActionListener(e -> frame.showPanel("reports"));
        add(b5);

      
    }
}