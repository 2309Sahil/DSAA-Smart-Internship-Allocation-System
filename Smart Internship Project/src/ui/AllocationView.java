package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AllocationView extends JFrame {

    public AllocationView() {
        setTitle("Final Allocation Results");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Internship Allocation Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 41, 59)); // Slate 800
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== TABLE SETUP =====
        String[] columns = {"ID", "Student Name", "Rank", "CGPA", "Company", "Role", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent users from editing the text in the table directly
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(35); // Give the rows room to breathe
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(false); // Remove the ugly default grid lines
        table.setIntercellSpacing(new Dimension(0, 0));

        // Style the Table Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 41, 59));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Apply Custom Rendering (Zebra Striping & Color Coding)
        table.setDefaultRenderer(Object.class, new CustomTableRenderer());

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Company
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Status

        // ===== FETCH DATA =====
        loadTableData(tableModel);

        // Add table to a scroll pane (with a clean white border)
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        // ===== BOTTOM BUTTON =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton closeBtn = new JButton("Close View");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(new Color(149, 165, 166));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Connects to DB and executes the LEFT JOIN query to get the full picture
    private void loadTableData(DefaultTableModel tableModel) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT s.student_id, s.name, s.rank_position, s.cgpa, " +
                           "c.company_name, c.role, a.allocation_status " +
                           "FROM students s " +
                           "LEFT JOIN allocations a ON s.student_id = a.student_id " +
                           "LEFT JOIN companies c ON a.company_id = c.company_id " +
                           "ORDER BY s.rank_position ASC"; // Order by merit!

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getInt("rank_position"),
                        rs.getDouble("cgpa"),
                        rs.getString("company_name") != null ? rs.getString("company_name") : "-",
                        rs.getString("role") != null ? rs.getString("role") : "-",
                        rs.getString("allocation_status") != null ? rs.getString("allocation_status") : "NOT_ALLOCATED"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== CUSTOM RENDERER FOR ZEBRA STRIPING & TEXT COLORS =====
    private class CustomTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // 1. Zebra Striping (Alternate row colors)
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(248, 250, 252)); // Very light Slate
                }
            }

            // 2. Color-code the Status column (Column index 6)
            if (column == 6 && value != null) {
                String status = value.toString();
                if (status.equals("ALLOCATED")) {
                    c.setForeground(new Color(34, 197, 94)); // Emerald Green
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (status.equals("NOT_ALLOCATED")) {
                    c.setForeground(new Color(239, 68, 68)); // Red
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                }
            } else {
                c.setForeground(new Color(51, 65, 85)); // Default text color
                c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }

            // Align text to center for certain columns
            if (column == 0 || column == 2 || column == 3 || column == 6) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return c;
        }
    }
}