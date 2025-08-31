import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PaymentBilling {
    private JFrame frame;
    private JTextField memberIdField, memberNameField, amountField, invoiceIdField, searchMemberField;
    private JComboBox<String> paymentMethodBox, memberPaymentBox, billingPeriodBox;
    private JTable paymentTable, invoiceTable;
    private DefaultTableModel paymentTableModel, invoiceTableModel;
    private JTextArea paymentNotesArea;

    public PaymentBilling() {
        frame = new JFrame("Payment & Billing Management");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("Payment & Billing Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Top Panel - Payment & Invoice Management
        JPanel topPanel = createPaymentManagementPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel - Tables
        JPanel centerPanel = createTablesPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(70, 130, 180));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JLabel statusLabel = new JLabel("Manage member payments, generate invoices, and track billing history");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Load initial data
        loadPayments();
        loadInvoices();
        loadMembers();

        frame.setVisible(true);
    }

    private JPanel createPaymentManagementPanel() {
        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBackground(new Color(240, 248, 255));
        
        // Payment Recording Panel
        JPanel recordPaymentPanel = new JPanel(new GridBagLayout());
        recordPaymentPanel.setBackground(new Color(240, 248, 255));
        TitledBorder paymentBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Record Payment",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        recordPaymentPanel.setBorder(paymentBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Payment recording components
        memberIdField = createStyledTextField(12);
        memberNameField = createStyledTextField(15);
        memberNameField.setEditable(false);
        amountField = createStyledTextField(10);
        paymentMethodBox = createStyledComboBox(new String[]{
            "Cash", "Credit Card", "Debit Card", "Bank Transfer", "Mobile Payment", "Cheque"
        });
        paymentNotesArea = new JTextArea(3, 20);
        paymentNotesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        paymentNotesArea.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane notesScrollPane = new JScrollPane(paymentNotesArea);

        JButton searchPaymentBtn = createStyledButton("Search", new Color(30, 144, 255));
        JButton recordPaymentBtn = createStyledButton("Record Payment", new Color(34, 139, 34));
        JButton clearPaymentBtn = createStyledButton("Clear", new Color(128, 128, 128));

        searchPaymentBtn.addActionListener(e -> searchMemberForPayment());
        recordPaymentBtn.addActionListener(e -> recordPayment());
        clearPaymentBtn.addActionListener(e -> clearPaymentFields());

        // Layout payment recording components
        addFormRow(recordPaymentPanel, gbc, 0, "Member ID:", memberIdField, searchPaymentBtn);
        addFormRow(recordPaymentPanel, gbc, 1, "Member Name:", memberNameField);
        addFormRow(recordPaymentPanel, gbc, 2, "Amount ($):", amountField);
        addFormRow(recordPaymentPanel, gbc, 3, "Payment Method:", paymentMethodBox);
        addFormRow(recordPaymentPanel, gbc, 4, "Notes:", notesScrollPane);

        // Payment button panel
        JPanel paymentButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        paymentButtonPanel.setBackground(new Color(240, 248, 255));
        paymentButtonPanel.add(recordPaymentBtn);
        paymentButtonPanel.add(clearPaymentBtn);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        recordPaymentPanel.add(paymentButtonPanel, gbc);

        // Invoice Generation Panel
        JPanel invoicePanel = new JPanel(new GridBagLayout());
        invoicePanel.setBackground(new Color(240, 248, 255));
        TitledBorder invoiceBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Generate Invoice",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        invoicePanel.setBorder(invoiceBorder);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 8, 8, 8);
        gbc2.anchor = GridBagConstraints.WEST;

        // Invoice components
        searchMemberField = createStyledTextField(12);
        memberPaymentBox = createStyledComboBox(new String[]{"Select Member..."});
        billingPeriodBox = createStyledComboBox(new String[]{
            "Monthly", "Quarterly", "Semi-Annual", "Annual"
        });

        JButton searchInvoiceBtn = createStyledButton("Search", new Color(30, 144, 255));
        JButton generateInvoiceBtn = createStyledButton("Generate Invoice", new Color(255, 140, 0));
        JButton viewInvoiceBtn = createStyledButton("View Invoice", new Color(128, 0, 128));

        searchInvoiceBtn.addActionListener(e -> searchMemberForInvoice());
        generateInvoiceBtn.addActionListener(e -> generateInvoice());
        viewInvoiceBtn.addActionListener(e -> viewInvoice());

        // Layout invoice components
        addFormRow(invoicePanel, gbc2, 0, "Search Member:", searchMemberField, searchInvoiceBtn);
        addFormRow(invoicePanel, gbc2, 1, "Select Member:", memberPaymentBox);
        addFormRow(invoicePanel, gbc2, 2, "Billing Period:", billingPeriodBox);

        // Invoice button panel
        JPanel invoiceButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        invoiceButtonPanel.setBackground(new Color(240, 248, 255));
        invoiceButtonPanel.add(generateInvoiceBtn);
        invoiceButtonPanel.add(viewInvoiceBtn);

        gbc2.gridx = 0;
        gbc2.gridy = 3;
        gbc2.gridwidth = 3;
        gbc2.fill = GridBagConstraints.NONE;
        gbc2.anchor = GridBagConstraints.CENTER;
        invoicePanel.add(invoiceButtonPanel, gbc2);

        // Combine both panels
        JPanel combinedPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        combinedPanel.setBackground(new Color(240, 248, 255));
        combinedPanel.add(recordPaymentPanel);
        combinedPanel.add(invoicePanel);

        paymentPanel.add(combinedPanel, BorderLayout.CENTER);
        return paymentPanel;
    }

    private JPanel createTablesPanel() {
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesPanel.setBackground(new Color(240, 248, 255));

        // Payment History Table
        JPanel paymentTablePanel = new JPanel(new BorderLayout());
        paymentTablePanel.setBackground(new Color(240, 248, 255));
        
        TitledBorder paymentBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Payment History",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        paymentTablePanel.setBorder(paymentBorder);

        String[] paymentColumns = {"Payment ID", "Member Name", "Amount", "Method", "Date", "Status"};
        paymentTableModel = new DefaultTableModel(paymentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paymentTable = new JTable(paymentTableModel);
        paymentTable.setFont(new Font("Arial", Font.PLAIN, 11));
        paymentTable.setRowHeight(25);
        paymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        paymentTable.getTableHeader().setBackground(new Color(70, 130, 180));
        paymentTable.getTableHeader().setForeground(Color.WHITE);
        paymentTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane paymentScrollPane = new JScrollPane(paymentTable);
        paymentScrollPane.setPreferredSize(new Dimension(500, 250));
        paymentTablePanel.add(paymentScrollPane, BorderLayout.CENTER);

        // Invoice Table
        JPanel invoiceTablePanel = new JPanel(new BorderLayout());
        invoiceTablePanel.setBackground(new Color(240, 248, 255));
        
        TitledBorder invoiceBorder = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Invoice History",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 180)
        );
        invoiceTablePanel.setBorder(invoiceBorder);

        String[] invoiceColumns = {"Invoice ID", "Member Name", "Amount", "Due Date", "Status", "Period"};
        invoiceTableModel = new DefaultTableModel(invoiceColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        invoiceTable = new JTable(invoiceTableModel);
        invoiceTable.setFont(new Font("Arial", Font.PLAIN, 11));
        invoiceTable.setRowHeight(25);
        invoiceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        invoiceTable.getTableHeader().setBackground(new Color(70, 130, 180));
        invoiceTable.getTableHeader().setForeground(Color.WHITE);
        invoiceTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane invoiceScrollPane = new JScrollPane(invoiceTable);
        invoiceScrollPane.setPreferredSize(new Dimension(500, 250));
        invoiceTablePanel.add(invoiceScrollPane, BorderLayout.CENTER);

        // Table action buttons
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        tableButtonPanel.setBackground(new Color(240, 248, 255));
        
        JButton refreshBtn = createStyledButton("Refresh", new Color(30, 144, 255));
        JButton exportBtn = createStyledButton("Export Report", new Color(128, 128, 128));
        JButton printInvoiceBtn = createStyledButton("Print Invoice", new Color(75, 0, 130));
        JButton closeBtn = createStyledButton("Close", new Color(220, 20, 60));

        refreshBtn.addActionListener(e -> refreshTables());
        exportBtn.addActionListener(e -> exportReport());
        printInvoiceBtn.addActionListener(e -> printInvoice());
        closeBtn.addActionListener(e -> frame.dispose());

        tableButtonPanel.add(refreshBtn);
        tableButtonPanel.add(exportBtn);
        tableButtonPanel.add(printInvoiceBtn);
        tableButtonPanel.add(closeBtn);

        tablesPanel.add(paymentTablePanel);
        tablesPanel.add(invoiceTablePanel);

        // Add button panel at the bottom
        JPanel mainTablesPanel = new JPanel(new BorderLayout());
        mainTablesPanel.setBackground(new Color(240, 248, 255));
        mainTablesPanel.add(tablesPanel, BorderLayout.CENTER);
        mainTablesPanel.add(tableButtonPanel, BorderLayout.SOUTH);

        return mainTablesPanel;
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

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component, JButton button) {
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

        // Button
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 5, 8, 8);
        panel.add(button, gbc);
        
        // Reset insets
        gbc.insets = new Insets(8, 8, 8, 8);
    }

    private void loadPayments() {
        // Clear existing data
        paymentTableModel.setRowCount(0);
        
        // Add sample payment data
        Object[][] samplePayments = {
            {"PAY001", "Alice Johnson", "$89.99", "Credit Card", "2025-08-15", "Completed"},
            {"PAY002", "Bob Smith", "$120.00", "Cash", "2025-08-20", "Completed"},
            {"PAY003", "Carol Davis", "$75.50", "Bank Transfer", "2025-08-25", "Completed"},
            {"PAY004", "David Wilson", "$150.00", "Credit Card", "2025-08-28", "Pending"},
            {"PAY005", "Emma Brown", "$95.00", "Mobile Payment", "2025-09-01", "Completed"}
        };
        
        for (Object[] row : samplePayments) {
            paymentTableModel.addRow(row);
        }
    }

    private void loadInvoices() {
        // Clear existing data
        invoiceTableModel.setRowCount(0);
        
        // Add sample invoice data
        Object[][] sampleInvoices = {
            {"INV001", "Alice Johnson", "$89.99", "2025-09-15", "Paid", "Monthly"},
            {"INV002", "Bob Smith", "$360.00", "2025-09-20", "Overdue", "Quarterly"},
            {"INV003", "Carol Davis", "$180.00", "2025-09-25", "Pending", "Semi-Annual"},
            {"INV004", "David Wilson", "$150.00", "2025-09-30", "Pending", "Monthly"},
            {"INV005", "Emma Brown", "$720.00", "2025-10-01", "Pending", "Annual"}
        };
        
        for (Object[] row : sampleInvoices) {
            invoiceTableModel.addRow(row);
        }
    }

    private void loadMembers() {
        memberPaymentBox.removeAllItems();
        memberPaymentBox.addItem("Select Member...");
        
        // Add sample members
        String[] sampleMembers = {
            "001 - Alice Johnson",
            "002 - Bob Smith", 
            "003 - Carol Davis",
            "004 - David Wilson",
            "005 - Emma Brown",
            "006 - Frank Miller"
        };
        
        for (String member : sampleMembers) {
            memberPaymentBox.addItem(member);
        }
    }

    private void searchMemberForPayment() {
        String memberId = memberIdField.getText().trim();
        
        if (memberId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter a Member ID to search.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Simulate member search
        String[] sampleMembers = {
            "001:Alice Johnson", "002:Bob Smith", "003:Carol Davis", 
            "004:David Wilson", "005:Emma Brown", "006:Frank Miller"
        };
        
        for (String member : sampleMembers) {
            if (member.startsWith(memberId + ":")) {
                memberNameField.setText(member.split(":")[1]);
                return;
            }
        }
        
        memberNameField.setText("Member not found");
        JOptionPane.showMessageDialog(frame, 
            "Member with ID '" + memberId + "' not found.", 
            "Member Not Found", 
            JOptionPane.ERROR_MESSAGE);
    }

    private void recordPayment() {
        String memberId = memberIdField.getText().trim();
        String memberName = memberNameField.getText().trim();
        String amount = amountField.getText().trim();
        String paymentMethod = (String) paymentMethodBox.getSelectedItem();
        String notes = paymentNotesArea.getText().trim();
        
        if (memberId.isEmpty() || memberName.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please fill in all required fields.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (memberName.equals("Member not found")) {
            JOptionPane.showMessageDialog(frame, 
                "Please search for a valid member first.", 
                "Invalid Member", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                JOptionPane.showMessageDialog(frame, 
                    "Please enter a valid amount greater than 0.", 
                    "Invalid Amount", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter a valid numeric amount.", 
                "Invalid Amount", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String message = String.format(
            "Payment recorded successfully!\n\n" +
            "Member: %s (%s)\n" +
            "Amount: $%s\n" +
            "Payment Method: %s\n" +
            "Date: %s\n" +
            "Notes: %s", 
            memberId, memberName, amount, paymentMethod, 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            notes.isEmpty() ? "None" : notes
        );
        
        JOptionPane.showMessageDialog(frame, message, "Payment Recorded", JOptionPane.INFORMATION_MESSAGE);
        
        clearPaymentFields();
        loadPayments(); // Refresh the table
    }

    private void clearPaymentFields() {
        memberIdField.setText("");
        memberNameField.setText("");
        amountField.setText("");
        paymentMethodBox.setSelectedIndex(0);
        paymentNotesArea.setText("");
    }

    private void searchMemberForInvoice() {
        String searchText = searchMemberField.getText().trim();
        
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter a Member ID or name to search.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Simulate search and populate dropdown
        String[] sampleMembers = {
            "001:Alice Johnson", "002:Bob Smith", "003:Carol Davis", 
            "004:David Wilson", "005:Emma Brown", "006:Frank Miller"
        };
        
        memberPaymentBox.removeAllItems();
        memberPaymentBox.addItem("Select Member...");
        
        boolean found = false;
        for (String member : sampleMembers) {
            String[] parts = member.split(":");
            if (parts[0].contains(searchText) || parts[1].toLowerCase().contains(searchText.toLowerCase())) {
                memberPaymentBox.addItem(parts[0] + " - " + parts[1]);
                found = true;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(frame, 
                "No members found matching '" + searchText + "'.", 
                "No Results", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Search completed. Please select a member from the dropdown.", 
                "Search Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void generateInvoice() {
        String selectedMember = (String) memberPaymentBox.getSelectedItem();
        String billingPeriod = (String) billingPeriodBox.getSelectedItem();
        
        if (selectedMember == null || selectedMember.equals("Select Member...")) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a member first.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Calculate amount based on billing period
        double amount = switch (billingPeriod) {
            case "Monthly" -> 89.99;
            case "Quarterly" -> 249.99;
            case "Semi-Annual" -> 479.99;
            case "Annual" -> 899.99;
            default -> 89.99;
        };
        
        String message = String.format(
            "Invoice generated successfully!\n\n" +
            "Member: %s\n" +
            "Billing Period: %s\n" +
            "Amount: $%.2f\n" +
            "Due Date: %s\n" +
            "Invoice ID: INV%06d", 
            selectedMember, billingPeriod, amount,
            LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            (int)(Math.random() * 999999)
        );
        
        JOptionPane.showMessageDialog(frame, message, "Invoice Generated", JOptionPane.INFORMATION_MESSAGE);
        loadInvoices(); // Refresh the table
    }

    private void viewInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select an invoice from the table to view.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String invoiceId = (String) invoiceTableModel.getValueAt(selectedRow, 0);
        String memberName = (String) invoiceTableModel.getValueAt(selectedRow, 1);
        String amount = (String) invoiceTableModel.getValueAt(selectedRow, 2);
        String dueDate = (String) invoiceTableModel.getValueAt(selectedRow, 3);
        String status = (String) invoiceTableModel.getValueAt(selectedRow, 4);
        String period = (String) invoiceTableModel.getValueAt(selectedRow, 5);
        
        String invoiceDetails = String.format(
            "INVOICE DETAILS\n\n" +
            "Invoice ID: %s\n" +
            "Member: %s\n" +
            "Amount: %s\n" +
            "Due Date: %s\n" +
            "Status: %s\n" +
            "Billing Period: %s\n\n" +
            "This invoice includes membership fees for the selected billing period.\n" +
            "Please ensure payment is made by the due date to avoid late fees.", 
            invoiceId, memberName, amount, dueDate, status, period
        );
        
        JOptionPane.showMessageDialog(frame, invoiceDetails, "Invoice Details - " + invoiceId, JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshTables() {
        loadPayments();
        loadInvoices();
        JOptionPane.showMessageDialog(frame, 
            "Payment and invoice data refreshed successfully.", 
            "Data Refreshed", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport() {
        JOptionPane.showMessageDialog(frame, 
            "Export functionality would generate a comprehensive financial report.\n\n" +
            "The report would include:\n" +
            "• Payment history summary\n" +
            "• Outstanding invoices\n" +
            "• Revenue analysis\n" +
            "• Member payment patterns\n" +
            "• Overdue accounts report\n\n" +
            "Feature coming soon!", 
            "Export Financial Report", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void printInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select an invoice from the table to print.", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String invoiceId = (String) invoiceTableModel.getValueAt(selectedRow, 0);
        String memberName = (String) invoiceTableModel.getValueAt(selectedRow, 1);
        
        JOptionPane.showMessageDialog(frame, 
            "Print functionality would send the selected invoice to your default printer.\n\n" +
            "Invoice: " + invoiceId + "\n" +
            "Member: " + memberName + "\n\n" +
            "The printed invoice would include:\n" +
            "• Complete member details\n" +
            "• Itemized billing information\n" +
            "• Payment instructions\n" +
            "• Terms and conditions\n\n" +
            "Feature coming soon!", 
            "Print Invoice", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
