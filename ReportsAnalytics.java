import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ReportsAnalytics {
    private JFrame frame;
    private JComboBox<String> reportTypeBox, timePeriodBox, memberFilterBox;
    private JTable reportTable, summaryTable;
    private DefaultTableModel reportTableModel, summaryTableModel;
    private JTextArea reportDetailsArea;
    private JLabel totalMembersLabel, totalRevenueLabel, activeTrainersLabel, pendingPaymentsLabel;

    public ReportsAnalytics() {
        frame = new JFrame("Reports & Analytics Dashboard");
        frame.setSize(1300, 850);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("Reports & Analytics Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Top Panel - Report Generation & Summary Cards
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel - Tables and Charts
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(70, 130, 180));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JLabel statusLabel = new JLabel("Generate comprehensive reports and analyze gym performance metrics");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Load initial data
        loadSummaryData();
        loadDefaultReport();

        frame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));

        // Summary Cards Panel
        JPanel summaryPanel = createSummaryCardsPanel();
        topPanel.add(summaryPanel, BorderLayout.NORTH);

        // Report Generation Panel
        JPanel reportGenPanel = createReportGenerationPanel();
        topPanel.add(reportGenPanel, BorderLayout.CENTER);

        return topPanel;
    }

    private JPanel createSummaryCardsPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setBackground(new Color(240, 248, 255));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Summary Cards
        JPanel membersCard = createSummaryCard("Total Members", "245", new Color(46, 125, 50));
        JPanel revenueCard = createSummaryCard("Monthly Revenue", "$18,450", new Color(56, 142, 60));
        JPanel trainersCard = createSummaryCard("Active Trainers", "12", new Color(67, 160, 71));
        JPanel paymentsCard = createSummaryCard("Pending Payments", "8", new Color(239, 108, 0));

        summaryPanel.add(membersCard);
        summaryPanel.add(revenueCard);
        summaryPanel.add(trainersCard);
        summaryPanel.add(paymentsCard);

        return summaryPanel;
    }

    private JPanel createSummaryCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createReportGenerationPanel() {
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBackground(new Color(240, 248, 255));

        // Report Controls Panel
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(new Color(240, 248, 255));
        TitledBorder controlsBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Report Generation",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        controlsPanel.setBorder(controlsBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Report controls
        reportTypeBox = createStyledComboBox(new String[]{
            "Member Statistics", "Revenue Analysis", "Trainer Performance", 
            "Payment Status", "Workout Plans Usage", "Membership Trends"
        });
        timePeriodBox = createStyledComboBox(new String[]{
            "Last 7 Days", "Last 30 Days", "Last 3 Months", "Last 6 Months", "Last Year", "All Time"
        });
        memberFilterBox = createStyledComboBox(new String[]{
            "All Members", "Active Members", "Inactive Members", "New Members", "Overdue Payments"
        });

        JButton generateBtn = createStyledButton("Generate Report", new Color(34, 139, 34));
        JButton exportBtn = createStyledButton("Export to PDF", new Color(128, 128, 128));
        JButton printBtn = createStyledButton("Print Report", new Color(75, 0, 130));
        JButton refreshBtn = createStyledButton("Refresh Data", new Color(30, 144, 255));

        generateBtn.addActionListener(e -> generateReport());
        exportBtn.addActionListener(e -> exportReport());
        printBtn.addActionListener(e -> printReport());
        refreshBtn.addActionListener(e -> refreshAllData());

        // Layout controls
        addFormRow(controlsPanel, gbc, 0, "Report Type:", reportTypeBox);
        addFormRow(controlsPanel, gbc, 1, "Time Period:", timePeriodBox);
        addFormRow(controlsPanel, gbc, 2, "Filter:", memberFilterBox);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(generateBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(refreshBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        controlsPanel.add(buttonPanel, gbc);

        reportPanel.add(controlsPanel, BorderLayout.CENTER);
        return reportPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 248, 255));

        // Split into left and right panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setResizeWeight(0.7);

        // Left Panel - Report Table
        JPanel leftPanel = createReportTablePanel();
        splitPane.setLeftComponent(leftPanel);

        // Right Panel - Quick Analytics
        JPanel rightPanel = createAnalyticsPanel();
        splitPane.setRightComponent(rightPanel);

        centerPanel.add(splitPane, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createReportTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));
        
        TitledBorder tableBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Report Data",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        tablePanel.setBorder(tableBorder);

        String[] columns = {"ID", "Name", "Type", "Value", "Date", "Status"};
        reportTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportTable = new JTable(reportTableModel);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 11));
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        reportTable.getTableHeader().setBackground(new Color(70, 130, 180));
        reportTable.getTableHeader().setForeground(Color.WHITE);
        reportTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel analyticsPanel = new JPanel(new BorderLayout());
        analyticsPanel.setBackground(new Color(240, 248, 255));
        
        TitledBorder analyticsBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Quick Analytics",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        analyticsPanel.setBorder(analyticsBorder);

        // Top Analytics Summary
        JPanel topAnalyticsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topAnalyticsPanel.setBackground(new Color(240, 248, 255));

        JButton topTrainersBtn = createStyledButton("Top Trainers", new Color(102, 51, 153));
        JButton popularPlansBtn = createStyledButton("Popular Plans", new Color(255, 87, 34));
        JButton trendAnalysisBtn = createStyledButton("Trend Analysis", new Color(0, 150, 136));

        topTrainersBtn.addActionListener(e -> showTopTrainersAnalysis());
        popularPlansBtn.addActionListener(e -> showPopularPlansAnalysis());
        trendAnalysisBtn.addActionListener(e -> showTrendAnalysis());

        topAnalyticsPanel.add(topTrainersBtn);
        topAnalyticsPanel.add(popularPlansBtn);
        topAnalyticsPanel.add(trendAnalysisBtn);

        analyticsPanel.add(topAnalyticsPanel, BorderLayout.NORTH);

        // Summary Table
        String[] summaryColumns = {"Metric", "Value", "Change"};
        summaryTableModel = new DefaultTableModel(summaryColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        summaryTable = new JTable(summaryTableModel);
        summaryTable.setFont(new Font("Arial", Font.PLAIN, 11));
        summaryTable.setRowHeight(25);
        summaryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        summaryTable.getTableHeader().setBackground(new Color(70, 130, 180));
        summaryTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane summaryScrollPane = new JScrollPane(summaryTable);
        summaryScrollPane.setPreferredSize(new Dimension(400, 250));
        analyticsPanel.add(summaryScrollPane, BorderLayout.CENTER);

        // Details Area
        reportDetailsArea = new JTextArea(8, 30);
        reportDetailsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        reportDetailsArea.setBorder(BorderFactory.createTitledBorder("Report Details"));
        reportDetailsArea.setEditable(false);
        reportDetailsArea.setBackground(new Color(250, 250, 250));
        JScrollPane detailsScrollPane = new JScrollPane(reportDetailsArea);
        analyticsPanel.add(detailsScrollPane, BorderLayout.SOUTH);

        return analyticsPanel;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLoweredBevelBorder());
        return comboBox;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        
        // Adjust button size based on text length
        int buttonWidth;
        if (text.length() > 15) {
            buttonWidth = 180;
        } else if (text.length() > 12) {
            buttonWidth = 160;
        } else if (text.length() > 8) {
            buttonWidth = 120;
        } else {
            buttonWidth = 100;
        }
        button.setPreferredSize(new Dimension(buttonWidth, 35));
        
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        // Add hover effect
        Color originalColor = bgColor;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(50, 50, 50));
        panel.add(label, gbc);

        // Component
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private void loadSummaryData() {
        // Load summary statistics into cards
        // This would normally pull from database
        updateSummaryCards();
        
        // Load summary analytics table
        summaryTableModel.setRowCount(0);
        Object[][] summaryData = {
            {"New Members", "23", "+15%"},
            {"Revenue Growth", "$2,300", "+8.5%"},
            {"Trainer Utilization", "87%", "+3%"},
            {"Plan Completion", "92%", "+5%"},
            {"Payment Success", "96%", "+2%"},
            {"Member Retention", "89%", "-1%"}
        };
        
        for (Object[] row : summaryData) {
            summaryTableModel.addRow(row);
        }
    }

    private void updateSummaryCards() {
        // Update summary card values
        // In a real implementation, these would be dynamically calculated
    }

    private void loadDefaultReport() {
        // Load default member statistics report
        reportTableModel.setRowCount(0);
        
        Object[][] defaultData = {
            {"M001", "Alice Johnson", "Member", "Active", "2025-08-15", "Current"},
            {"M002", "Bob Smith", "Member", "Active", "2025-08-20", "Current"},
            {"T001", "John Trainer", "Trainer", "12 Clients", "2025-07-01", "Active"},
            {"P001", "Weight Loss Plan", "Plan", "45 Users", "2025-08-10", "Popular"},
            {"PAY001", "Monthly Payment", "Payment", "$89.99", "2025-09-01", "Paid"},
            {"M003", "Carol Davis", "Member", "Inactive", "2025-06-15", "Overdue"},
            {"T002", "Sarah Coach", "Trainer", "18 Clients", "2025-07-15", "Active"},
            {"P002", "Strength Training", "Plan", "32 Users", "2025-08-05", "Popular"}
        };
        
        for (Object[] row : defaultData) {
            reportTableModel.addRow(row);
        }
        
        reportDetailsArea.setText(
            "Default Report: Member Statistics\n\n" +
            "Total Records: " + defaultData.length + "\n" +
            "Active Members: 5\n" +
            "Active Trainers: 2\n" +
            "Popular Plans: 2\n\n" +
            "This report shows an overview of all gym entities.\n" +
            "Use the filters above to generate specific reports."
        );
    }

    private void generateReport() {
        String reportType = (String) reportTypeBox.getSelectedItem();
        String timePeriod = (String) timePeriodBox.getSelectedItem();
        String filter = (String) memberFilterBox.getSelectedItem();
        
        reportTableModel.setRowCount(0);
        
        switch (reportType) {
            case "Member Statistics" -> generateMemberReport(timePeriod, filter);
            case "Revenue Analysis" -> generateRevenueReport(timePeriod);
            case "Trainer Performance" -> generateTrainerReport(timePeriod);
            case "Payment Status" -> generatePaymentReport(filter);
            case "Workout Plans Usage" -> generateWorkoutReport(timePeriod);
            case "Membership Trends" -> generateTrendReport(timePeriod);
        }
        
        updateReportDetails(reportType, timePeriod, filter);
    }

    private void generateMemberReport(String timePeriod, String filter) {
        Object[][] memberData = {
            {"M001", "Alice Johnson", "Premium", "Active", "2025-08-15", "Current"},
            {"M002", "Bob Smith", "Standard", "Active", "2025-08-20", "Current"},
            {"M003", "Carol Davis", "Premium", "Inactive", "2025-06-15", "Overdue"},
            {"M004", "David Wilson", "Standard", "Active", "2025-08-30", "Current"},
            {"M005", "Emma Brown", "Premium", "Active", "2025-09-01", "Current"}
        };
        
        for (Object[] row : memberData) {
            if (filter.equals("All Members") || 
                (filter.equals("Active Members") && row[3].equals("Active")) ||
                (filter.equals("Inactive Members") && row[3].equals("Inactive"))) {
                reportTableModel.addRow(row);
            }
        }
    }

    private void generateRevenueReport(String timePeriod) {
        Object[][] revenueData = {
            {"R001", "Membership Fees", "Revenue", "$12,450", "2025-08-31", "Monthly"},
            {"R002", "Personal Training", "Revenue", "$3,200", "2025-08-31", "Monthly"},
            {"R003", "Equipment Rental", "Revenue", "$890", "2025-08-31", "Monthly"},
            {"R004", "Late Fees", "Revenue", "$240", "2025-08-31", "Monthly"},
            {"R005", "Merchandise", "Revenue", "$670", "2025-08-31", "Monthly"}
        };
        
        for (Object[] row : revenueData) {
            reportTableModel.addRow(row);
        }
    }

    private void generateTrainerReport(String timePeriod) {
        Object[][] trainerData = {
            {"T001", "John Smith", "Trainer", "18 Clients", "2025-08-31", "Excellent"},
            {"T002", "Sarah Johnson", "Trainer", "15 Clients", "2025-08-31", "Very Good"},
            {"T003", "Mike Wilson", "Trainer", "12 Clients", "2025-08-31", "Good"},
            {"T004", "Lisa Brown", "Trainer", "20 Clients", "2025-08-31", "Outstanding"},
            {"T005", "David Lee", "Trainer", "8 Clients", "2025-08-31", "Satisfactory"}
        };
        
        for (Object[] row : trainerData) {
            reportTableModel.addRow(row);
        }
    }

    private void generatePaymentReport(String filter) {
        Object[][] paymentData = {
            {"PAY001", "Alice Johnson", "Payment", "$89.99", "2025-09-01", "Paid"},
            {"PAY002", "Bob Smith", "Payment", "$120.00", "2025-08-28", "Overdue"},
            {"PAY003", "Carol Davis", "Payment", "$89.99", "2025-08-25", "Pending"},
            {"PAY004", "David Wilson", "Payment", "$150.00", "2025-09-02", "Paid"},
            {"PAY005", "Emma Brown", "Payment", "$89.99", "2025-09-01", "Paid"}
        };
        
        for (Object[] row : paymentData) {
            if (filter.equals("All Members") || 
                (filter.equals("Overdue Payments") && row[5].equals("Overdue"))) {
                reportTableModel.addRow(row);
            }
        }
    }

    private void generateWorkoutReport(String timePeriod) {
        Object[][] workoutData = {
            {"WP001", "Weight Loss Program", "Plan", "45 Users", "2025-08-15", "Very Popular"},
            {"WP002", "Muscle Building", "Plan", "32 Users", "2025-08-20", "Popular"},
            {"WP003", "Flexibility Training", "Plan", "28 Users", "2025-08-25", "Popular"},
            {"WP004", "Cardio Fitness", "Plan", "38 Users", "2025-08-30", "Very Popular"},
            {"WP005", "Strength Training", "Plan", "25 Users", "2025-09-01", "Moderate"}
        };
        
        for (Object[] row : workoutData) {
            reportTableModel.addRow(row);
        }
    }

    private void generateTrendReport(String timePeriod) {
        Object[][] trendData = {
            {"T001", "New Registrations", "Trend", "+15%", "2025-08-31", "Increasing"},
            {"T002", "Revenue Growth", "Trend", "+8.5%", "2025-08-31", "Positive"},
            {"T003", "Member Retention", "Trend", "-2%", "2025-08-31", "Slight Decline"},
            {"T004", "Trainer Utilization", "Trend", "+5%", "2025-08-31", "Improving"},
            {"T005", "Plan Completion", "Trend", "+12%", "2025-08-31", "Excellent"}
        };
        
        for (Object[] row : trendData) {
            reportTableModel.addRow(row);
        }
    }

    private void updateReportDetails(String reportType, String timePeriod, String filter) {
        StringBuilder details = new StringBuilder();
        details.append("Report Generated: ").append(reportType).append("\n");
        details.append("Time Period: ").append(timePeriod).append("\n");
        details.append("Filter: ").append(filter).append("\n");
        details.append("Generated: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n\n");
        
        int rowCount = reportTableModel.getRowCount();
        details.append("Total Records: ").append(rowCount).append("\n\n");
        
        switch (reportType) {
            case "Member Statistics" -> details.append("Summary: Showing member data with activity status and payment information.");
            case "Revenue Analysis" -> details.append("Summary: Revenue breakdown by source and time period. Total monthly revenue tracking.");
            case "Trainer Performance" -> details.append("Summary: Trainer performance metrics including client count and ratings.");
            case "Payment Status" -> details.append("Summary: Payment status overview showing paid, pending, and overdue payments.");
            case "Workout Plans Usage" -> details.append("Summary: Popular workout plans and their usage statistics.");
            case "Membership Trends" -> details.append("Summary: Membership trends showing growth patterns and key metrics.");
        }
        
        reportDetailsArea.setText(details.toString());
    }

    private void showTopTrainersAnalysis() {
        JOptionPane.showMessageDialog(frame,
            "TOP TRAINERS ANALYSIS\n\n" +
            "1. Lisa Brown - 20 clients (Outstanding)\n" +
            "2. John Smith - 18 clients (Excellent)\n" +
            "3. Sarah Johnson - 15 clients (Very Good)\n" +
            "4. Mike Wilson - 12 clients (Good)\n" +
            "5. David Lee - 8 clients (Satisfactory)\n\n" +
            "Performance Metrics:\n" +
            "• Average client satisfaction: 4.6/5\n" +
            "• Client retention rate: 92%\n" +
            "• Plan completion rate: 87%",
            "Top Trainers Analysis",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPopularPlansAnalysis() {
        JOptionPane.showMessageDialog(frame,
            "POPULAR WORKOUT PLANS ANALYSIS\n\n" +
            "1. Weight Loss Program - 45 active users\n" +
            "2. Cardio Fitness - 38 active users\n" +
            "3. Muscle Building - 32 active users\n" +
            "4. Flexibility Training - 28 active users\n" +
            "5. Strength Training - 25 active users\n\n" +
            "Plan Metrics:\n" +
            "• Average completion rate: 89%\n" +
            "• Member satisfaction: 4.4/5\n" +
            "• Most popular time: Evening (6-8 PM)",
            "Popular Plans Analysis",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTrendAnalysis() {
        JOptionPane.showMessageDialog(frame,
            "TREND ANALYSIS SUMMARY\n\n" +
            "Membership Growth:\n" +
            "• New registrations: +15% this month\n" +
            "• Member retention: 89% (slight decline)\n" +
            "• Cancellation rate: 3.2%\n\n" +
            "Financial Trends:\n" +
            "• Revenue growth: +8.5% monthly\n" +
            "• Payment success rate: 96%\n" +
            "• Overdue payments: 4% decrease\n\n" +
            "Operational Trends:\n" +
            "• Trainer utilization: +5% improvement\n" +
            "• Equipment usage: 82% average\n" +
            "• Peak hours: 6-8 PM (78% capacity)",
            "Trend Analysis",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport() {
        String reportType = (String) reportTypeBox.getSelectedItem();
        JOptionPane.showMessageDialog(frame,
            "Export functionality would generate a PDF report.\n\n" +
            "Report: " + reportType + "\n" +
            "Format: PDF with charts and tables\n" +
            "Content: Complete data analysis with visualizations\n\n" +
            "The exported report would include:\n" +
            "• Executive summary\n" +
            "• Detailed data tables\n" +
            "• Statistical analysis\n" +
            "• Trend charts and graphs\n" +
            "• Recommendations\n\n" +
            "Feature coming soon!",
            "Export Report",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void printReport() {
        JOptionPane.showMessageDialog(frame,
            "Print functionality would send the current report to your printer.\n\n" +
            "The printed report includes:\n" +
            "• Report header with date and filters\n" +
            "• Complete data table\n" +
            "• Summary statistics\n" +
            "• Professional formatting\n\n" +
            "Feature coming soon!",
            "Print Report",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshAllData() {
        loadSummaryData();
        loadDefaultReport();
        JOptionPane.showMessageDialog(frame,
            "All data has been refreshed successfully.\n\n" +
            "Updated:\n" +
            "• Summary statistics\n" +
            "• Report data\n" +
            "• Analytics metrics\n" +
            "• Trend information",
            "Data Refreshed",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
