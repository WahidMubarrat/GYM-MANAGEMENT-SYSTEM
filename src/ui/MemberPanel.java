package ui;


import db.DB;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberPanel extends JPanel {

    private final MainFrame frame;

    // form fields
    private JTextField txtMemberId;
    private JTextField txtName;
    private JTextField txtAge;
    private JComboBox<String> cbGender;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JComboBox<PlanItem> cbPlan;
    private JCheckBox chkAutoAssign;
    private JComboBox<String> cbSpec;           // used when auto-assign
    private JComboBox<TrainerItem> cbTrainer;   // used when manual assign
    private JFormattedTextField txtStartDate;   // yyyy-mm-dd

    // table
    private JTable tblMembers;

    // buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnBack, btnRefresh;

    public MemberPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10,10));

        add(buildTopForm(), BorderLayout.NORTH);
        add(buildCenterTable(), BorderLayout.CENTER);
        add(buildBottomButtons(), BorderLayout.SOUTH);

        // load combos and table
        refreshLookups();
        refreshTable();

        // wire dynamic visibility
        toggleAssignMode();
        chkAutoAssign.addActionListener(e -> toggleAssignMode());
    }

    /* ---------------- UI BUILDERS ---------------- */

    private JPanel buildTopForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Member Details"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int r = 0;

        // row 0: Member ID (read-only for edit)
        gc.gridx = 0; gc.gridy = r; p.add(new JLabel("Member ID (for edit):"), gc);
        txtMemberId = new JTextField();
        txtMemberId.setEditable(false);
        gc.gridx = 1; gc.gridy = r; p.add(txtMemberId, gc);

        // row 1: Name, Age
        r++;
        gc.gridx = 0; gc.gridy = r; p.add(new JLabel("Name:"), gc);
        txtName = new JTextField();
        gc.gridx = 1; gc.gridy = r; p.add(txtName, gc);

        gc.gridx = 2; gc.gridy = r; p.add(new JLabel("Age:"), gc);
        txtAge = new JTextField();
        gc.gridx = 3; gc.gridy = r; p.add(txtAge, gc);

        // row 2: Gender, Phone
        r++;
        gc.gridx = 0; gc.gridy = r; p.add(new JLabel("Gender:"), gc);
        cbGender = new JComboBox<>(new String[]{"Male","Female","Other"});
        gc.gridx = 1; gc.gridy = r; p.add(cbGender, gc);

        gc.gridx = 2; gc.gridy = r; p.add(new JLabel("Phone:"), gc);
        txtPhone = new JTextField();
        gc.gridx = 3; gc.gridy = r; p.add(txtPhone, gc);

        // row 3: Address (span)
        r++;
        gc.gridx = 0; gc.gridy = r; p.add(new JLabel("Address:"), gc);
        txtAddress = new JTextField();
        gc.gridwidth = 3;
        gc.gridx = 1; gc.gridy = r; p.add(txtAddress, gc);
        gc.gridwidth = 1;

        // row 4: Plan, Start Date
        r++;
        gc.gridx = 0; gc.gridy = r; p.add(new JLabel("Plan:"), gc);
        cbPlan = new JComboBox<>();
        gc.gridx = 1; gc.gridy = r; p.add(cbPlan, gc);

        gc.gridx = 2; gc.gridy = r; p.add(new JLabel("Start Date (yyyy-mm-dd):"), gc);
        txtStartDate = new JFormattedTextField(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE.toString());
        txtStartDate.setText(LocalDate.now().toString());
        gc.gridx = 3; gc.gridy = r; p.add(txtStartDate, gc);

        // row 5: assign mode
        r++;
        chkAutoAssign = new JCheckBox("Auto-assign trainer by specialization");
        chkAutoAssign.setSelected(true);
        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 4; p.add(chkAutoAssign, gc);
        gc.gridwidth = 1;

        // row 6: specialization + trainer (shown conditionally)
        r++;
        gc.gridx = 0; gc.gridy = r; p.add(new JLabel("Specialization:"), gc);
        cbSpec = new JComboBox<>();
        gc.gridx = 1; gc.gridy = r; p.add(cbSpec, gc);

        gc.gridx = 2; gc.gridy = r; p.add(new JLabel("Trainer (manual):"), gc);
        cbTrainer = new JComboBox<>();
        gc.gridx = 3; gc.gridy = r; p.add(cbTrainer, gc);

        return p;
    }

    private JScrollPane buildCenterTable() {
        tblMembers = new JTable(new DefaultTableModel());
        tblMembers.setFillsViewportHeight(true);
        tblMembers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // load selected row into form
        tblMembers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedRowToForm();
        });

        return new JScrollPane(tblMembers);
    }

    private JPanel buildBottomButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnBack = new JButton("Back");
        btnRefresh = new JButton("Refresh");
        btnClear = new JButton("Clear");
        btnAdd = new JButton("Add Member");
        btnUpdate = new JButton("Update Member");
        btnDelete = new JButton("Delete Member");

        p.add(btnBack);
        p.add(btnRefresh);
        p.add(btnClear);
        p.add(btnAdd);
        p.add(btnUpdate);
        p.add(btnDelete);

        // listeners
        btnBack.addActionListener(e -> frame.showPanel("dashboard"));
        btnRefresh.addActionListener(e -> refreshTable());
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());

        return p;
    }

    /* ---------------- DATA LOADERS ---------------- */

    private void refreshLookups() {
        loadPlans();
        loadSpecializations();
        loadTrainers();
    }

    private void loadPlans() {
        cbPlan.removeAllItems();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT plan_id, plan_name FROM plan ORDER BY plan_id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbPlan.addItem(new PlanItem(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            showError(ex, "Loading plans failed");
        }
    }

    private void loadSpecializations() {
        cbSpec.removeAllItems();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT DISTINCT specialization FROM trainer ORDER BY specialization")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbSpec.addItem(rs.getString(1));
            }
        } catch (SQLException ex) {
            showError(ex, "Loading specializations failed");
        }
    }

    private void loadTrainers() {
        cbTrainer.removeAllItems();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT trainer_id, name FROM trainer ORDER BY trainer_id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbTrainer.addItem(new TrainerItem(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            showError(ex, "Loading trainers failed");
        }
    }

    private void refreshTable() {
        final String sql =
                "SELECT m.member_id, m.name, m.age, m.gender, m.phone, m.address, " +
                "       p.plan_name, m.start_date, m.end_date, m.status, " +
                "       t.name AS trainer_name " +
                "  FROM member m " +
                "  JOIN plan p ON p.plan_id = m.plan_id " +
                "  LEFT JOIN trainer t ON t.trainer_id = m.trainer_id " +
                " ORDER BY m.member_id";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            TableUtils.fill(tblMembers, rs);
        } catch (SQLException ex) {
            showError(ex, "Loading members failed");
        }
    }

    /* ---------------- FORM HELPERS ---------------- */

    private void toggleAssignMode() {
        boolean auto = chkAutoAssign.isSelected();
        cbSpec.setEnabled(auto);
        cbTrainer.setEnabled(!auto);
    }

    private void clearForm() {
        txtMemberId.setText("");
        txtName.setText("");
        txtAge.setText("");
        cbGender.setSelectedIndex(0);
        txtPhone.setText("");
        txtAddress.setText("");
        if (cbPlan.getItemCount() > 0) cbPlan.setSelectedIndex(0);
        if (cbSpec.getItemCount() > 0) cbSpec.setSelectedIndex(0);
        if (cbTrainer.getItemCount() > 0) cbTrainer.setSelectedIndex(0);
        txtStartDate.setText(LocalDate.now().toString());
        tblMembers.clearSelection();
    }

    private void loadSelectedRowToForm() {
        int row = tblMembers.getSelectedRow();
        if (row < 0) return;

        txtMemberId.setText(val(row, "MEMBER_ID"));
        txtName.setText(val(row, "NAME"));
        txtAge.setText(val(row, "AGE"));
        cbGender.setSelectedItem(val(row, "GENDER"));
        txtPhone.setText(val(row, "PHONE"));
        txtAddress.setText(val(row, "ADDRESS"));
        txtStartDate.setText(val(row, "START_DATE") != null ? val(row, "START_DATE").substring(0, 10) : LocalDate.now().toString());

        // set plan by name
        String planName = val(row, "PLAN_NAME");
        selectPlanByName(planName);

        // trainer is display-only in table; we won’t auto-set manual mode here
    }

    private String val(int row, String colLabel) {
        int col = findColumn(colLabel);
        if (col < 0) return null;
        Object v = tblMembers.getValueAt(row, col);
        return v == null ? null : v.toString();
        }
    private int findColumn(String colLabel) {
        for (int i = 0; i < tblMembers.getColumnCount(); i++) {
            if (colLabel.equalsIgnoreCase(tblMembers.getColumnName(i))) return i;
        }
        return -1;
    }
    private void selectPlanByName(String planName) {
        if (planName == null) return;
        for (int i = 0; i < cbPlan.getItemCount(); i++) {
            if (cbPlan.getItemAt(i).name.equalsIgnoreCase(planName)) {
                cbPlan.setSelectedIndex(i);
                return;
            }
        }
    }

    private java.sql.Date parseSqlDate(String s) {
        try {
            return java.sql.Date.valueOf(s.trim()); // expects yyyy-mm-dd
        } catch (Exception ex) {
            return null;
        }
    }

    /* ---------------- CRUD ACTIONS ---------------- */

    private void addMember() {
        // basic validation
        if (txtName.getText().trim().isEmpty()) { warn("Name is required"); return; }
        if (txtAge.getText().trim().isEmpty()) { warn("Age is required"); return; }
        PlanItem plan = (PlanItem) cbPlan.getSelectedItem();
        if (plan == null) { warn("Select a plan"); return; }
        java.sql.Date start = parseSqlDate(txtStartDate.getText());
        if (start == null) { warn("Invalid start date. Use yyyy-mm-dd."); return; }

        String name = txtName.getText().trim();
        Integer age;
        try { age = Integer.parseInt(txtAge.getText().trim()); }
        catch (NumberFormatException nfe) { warn("Age must be a number"); return; }

        String gender = (String) cbGender.getSelectedItem();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        boolean auto = chkAutoAssign.isSelected();

        try (Connection c = DB.get()) {
            if (auto) {
                String spec = (String) cbSpec.getSelectedItem();
                if (spec == null || spec.isBlank()) { warn("Choose specialization for auto-assign"); return; }

                try (CallableStatement cs = c.prepareCall("{ call add_member_auto(?, ?, ?, ?, ?, ?, ?, ?) }")) {
                    cs.setString(1, name);
                    cs.setInt(2, age);
                    cs.setString(3, gender);
                    cs.setString(4, phone);
                    cs.setString(5, address);
                    cs.setInt(6, plan.id);
                    cs.setString(7, spec);
                    cs.setDate(8, start);
                    cs.execute();
                }
                info("Member added (auto-assigned trainer).");
            } else {
                TrainerItem tr = (TrainerItem) cbTrainer.getSelectedItem();
                if (tr == null) { warn("Select a trainer"); return; }

                try (CallableStatement cs = c.prepareCall("{ call ADD_MEMBER(?, ?, ?, ?, ?, ?, ?, ?) }")) {
                    cs.setString(1, name);
                    cs.setInt(2, age);
                    cs.setString(3, gender);
                    cs.setString(4, phone);
                    cs.setString(5, address);
                    cs.setInt(6, plan.id);
                    cs.setInt(7, tr.id);
                    cs.setDate(8, start);
                    cs.execute();
                }
                info("Member added (manual trainer).");
            }

            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex, "Add member failed");
        }
    }

    private void updateMember() {
        if (txtMemberId.getText().trim().isEmpty()) { warn("Select a member from the table first"); return; }

        int memberId = Integer.parseInt(txtMemberId.getText().trim());
        String name = txtName.getText().trim();
        if (name.isEmpty()) { warn("Name is required"); return; }
        Integer age;
        try { age = Integer.parseInt(txtAge.getText().trim()); }
        catch (NumberFormatException nfe) { warn("Age must be a number"); return; }

        String gender = (String) cbGender.getSelectedItem();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        PlanItem plan = (PlanItem) cbPlan.getSelectedItem();
        if (plan == null) { warn("Select a plan"); return; }

        // For update, we’ll keep the currently assigned trainer if manual mode is off
        Integer trainerId = null;
        if (!chkAutoAssign.isSelected()) {
            TrainerItem tr = (TrainerItem) cbTrainer.getSelectedItem();
            if (tr == null) { warn("Select a trainer"); return; }
            trainerId = tr.id;
        } else {
            // keep whatever trainer is already in DB: read it quickly
            try (Connection c = DB.get();
                 PreparedStatement ps = c.prepareStatement("SELECT trainer_id FROM member WHERE member_id=?")) {
                ps.setInt(1, memberId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) trainerId = (Integer) rs.getObject(1); // can be null
            } catch (SQLException ex) {
                showError(ex, "Reading current trainer failed");
                return;
            }
        }

        try (Connection c = DB.get();
             CallableStatement cs = c.prepareCall("{ call EDIT_MEMBER(?, ?, ?, ?, ?, ?, ?, ?) }")) {
            cs.setInt(1, memberId);
            cs.setString(2, name);
            cs.setInt(3, age);
            cs.setString(4, gender);
            cs.setString(5, phone);
            cs.setString(6, address);
            cs.setInt(7, plan.id);
            if (trainerId == null) cs.setNull(8, Types.INTEGER); else cs.setInt(8, trainerId);
            cs.execute();
            info("Member updated.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex, "Update member failed");
        }
    }

    private void deleteMember() {
        if (txtMemberId.getText().trim().isEmpty()) { warn("Select a member from the table first"); return; }
        int ans = JOptionPane.showConfirmDialog(this, "Delete this member?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) return;

        int memberId = Integer.parseInt(txtMemberId.getText().trim());
        try (Connection c = DB.get();
             CallableStatement cs = c.prepareCall("{ call DELETE_MEMBER(?) }")) {
            cs.setInt(1, memberId);
            cs.execute();
            info("Member deleted.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex, "Delete member failed");
        }
    }

    /* ---------------- UTIL ---------------- */

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showError(Exception ex, String title) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    /* ---------------- ITEM CLASSES ---------------- */

    private static class PlanItem {
        final int id;
        final String name;
        PlanItem(int id, String name){ this.id = id; this.name = name; }
        @Override public String toString(){ return id + " - " + name; }
    }
    private static class TrainerItem {
        final int id;
        final String name;
        TrainerItem(int id, String name){ this.id = id; this.name = name; }
        @Override public String toString(){ return id + " - " + name; }
    }
}
