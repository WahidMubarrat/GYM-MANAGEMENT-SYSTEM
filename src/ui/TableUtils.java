package ui;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public final class TableUtils {
    private TableUtils(){}

    public static void fill(JTable table, ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        DefaultTableModel model = new DefaultTableModel();
        for (int i=1;i<=cols;i++) model.addColumn(md.getColumnLabel(i));
        while (rs.next()) {
            Object[] row = new Object[cols];
            for (int i=1;i<=cols;i++) row[i-1] = rs.getObject(i);
            model.addRow(row);
        }
        table.setModel(model);
    }
}
