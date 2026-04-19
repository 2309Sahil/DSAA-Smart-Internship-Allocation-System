package ui;

import database.DBConnection;
import service.ReallocationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDashboard extends JFrame {

    private int studentId;
    private String studentName;
    private int allocatedCompanyId = -1;
    private boolean hasSecuredInternship = false; // Tracks the "One Internship Rule"

    public StudentDashboard(int studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;

        setTitle("Student Portal - " + studentName);
        setSize(800, 550); // Made window slightly wider to fit the job board
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(241, 245, 249));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42)); 
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + studentName);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // ==== TABS ====
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        // Tab 1: Offer Status
        tabbedPane.addTab("My Offer Status", createStatusPanel());
        
        // Tab 2: Opportunities Board
        tabbedPane.addTab("Opportunities Board", createJobBoardPanel());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    // --- TAB 1: STATUS PANEL ---
    private JPanel createStatusPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel("Internship Offer Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 41, 59));
        card.add(titleLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT a.allocation_status, c.company_id, c.company_name, c.role " +
                           "FROM allocations a JOIN companies c ON a.company_id = c.company_id WHERE a.student_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("allocation_status");
                String companyName = rs.getString("company_name");
                String role = rs.getString("role");
                this.allocatedCompanyId = rs.getInt("company_id");

                if (status.equals("ALLOCATED") || status.equals("ACCEPTED")) {
                    this.hasSecuredInternship = true; // They have an offer, trigger One Internship Rule!
                }

                if (status.equals("ALLOCATED")) {
                    infoPanel.add(createStyledLabel("Status: PENDING DECISION", new Color(234, 179, 8))); 
                    infoPanel.add(createStyledLabel("Company: " + companyName, Color.BLACK));
                    infoPanel.add(createStyledLabel("Role: " + role, Color.BLACK));
                    card.add(createActionButtons(), BorderLayout.SOUTH);
                } else if (status.equals("ACCEPTED")) {
                    infoPanel.add(createStyledLabel("Status: SECURED!", new Color(34, 197, 94))); 
                    infoPanel.add(createStyledLabel("Company: " + companyName, Color.BLACK));
                    infoPanel.add(createStyledLabel("Role: " + role, Color.BLACK));
                }
            } else {
                infoPanel.add(createStyledLabel("Status: NO CURRENT OFFERS", new Color(239, 68, 68)));
                infoPanel.add(new JLabel("Browse the Opportunities Board to apply for roles!"));
            }
        } catch (Exception e) {
            infoPanel.add(new JLabel("Error loading status..."));
        }

        card.add(infoPanel, BorderLayout.CENTER);
        contentPanel.add(card, BorderLayout.CENTER);
        return contentPanel;
    }

    // --- TAB 2: JOB BOARD PANEL ---
    private JPanel createJobBoardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Setup a beautiful table to show companies
        String[] columns = {"Company ID", "Company Name", "Role", "Duration", "Seats Left", "Apply"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(30, 41, 59));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT company_id, company_name, role, duration, seats FROM companies WHERE seats > 0";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("company_id"),
                        rs.getString("company_name"),
                        rs.getString("role"),
                        rs.getString("duration") + " months",
                        rs.getInt("seats"),
                        "Click to Apply ->"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Apply Area
        JPanel applyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        applyPanel.setBackground(Color.WHITE);
        applyPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel instruction = new JLabel("Select a company from the list and click Apply: ");
        instruction.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        
        JButton applyBtn = new JButton("Apply for Selected Role");
        applyBtn.setBackground(new Color(59, 130, 246));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        applyBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a company from the table first.", "Select a Role", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ONE INTERNSHIP RULE CHECK
            if (hasSecuredInternship) {
                JOptionPane.showMessageDialog(this, "One Internship Rule: You already have a pending or accepted offer! You cannot apply for more.", "Rule Violation", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int companyId = (int) table.getValueAt(selectedRow, 0);
            String companyName = (String) table.getValueAt(selectedRow, 1);
            
            // Open the Application Form!
            new ApplicationForm(studentId, companyId, companyName);
        });

        applyPanel.add(instruction);
        applyPanel.add(applyBtn);
        panel.add(applyPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createStyledLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(color);
        return label;
    }

    private JPanel createActionButtons() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnPanel.setBackground(Color.WHITE);
        JButton acceptBtn = new JButton("Accept Offer");
        acceptBtn.setBackground(new Color(34, 197, 94));
        acceptBtn.setForeground(Color.WHITE);
        JButton rejectBtn = new JButton("Reject Offer");
        rejectBtn.setBackground(new Color(239, 68, 68));
        rejectBtn.setForeground(Color.WHITE);

        acceptBtn.addActionListener(e -> handleDecision(true));
        rejectBtn.addActionListener(e -> handleDecision(false));
        btnPanel.add(acceptBtn); btnPanel.add(rejectBtn);
        return btnPanel;
    }

    private void handleDecision(boolean isAccepted) {
        try (Connection conn = DBConnection.getConnection()) {
            if (isAccepted) {
                PreparedStatement ps = conn.prepareStatement("UPDATE allocations SET allocation_status = 'ACCEPTED' WHERE student_id = ?");
                ps.setInt(1, studentId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Offer Accepted! Congratulations!");
            } else {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM allocations WHERE student_id = ?");
                ps.setInt(1, studentId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Offer Rejected. Returning seat to pool...");
                ReallocationService rs = new ReallocationService();
                rs.reallocateSeat(allocatedCompanyId);
            }
            dispose();
            new StudentDashboard(studentId, studentName);
        } catch (Exception e) { e.printStackTrace(); }
    }
}