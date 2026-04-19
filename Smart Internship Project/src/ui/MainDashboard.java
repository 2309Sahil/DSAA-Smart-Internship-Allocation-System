package ui;

import database.DBConnection;
import service.AllocationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainDashboard extends JFrame {

    // Modern Color Palette
    private final Color SIDEBAR_BG = new Color(30, 41, 59);      // Slate 800
    private final Color SIDEBAR_HOVER = new Color(51, 65, 85);   // Slate 700
    private final Color HEADER_BG = new Color(15, 23, 42);       // Slate 900
    private final Color CONTENT_BG = new Color(241, 245, 249);   // Slate 100
    private final Color ACCENT_COLOR = new Color(56, 189, 248);  // Light Blue

    public MainDashboard() {
        setTitle("Smart Internship Allocation System");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setPreferredSize(new Dimension(100, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        JLabel title = new JLabel("Smart Internship Allocation Engine");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // ===== SIDEBAR =====
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(7, 1, 0, 5));
        sidePanel.setPreferredSize(new Dimension(250, 0));
        sidePanel.setBackground(SIDEBAR_BG);
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel menuLabel = new JLabel("NAVIGATION");
        menuLabel.setForeground(new Color(148, 163, 184));
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 0));
        sidePanel.add(menuLabel);

        sidePanel.add(createMenuButton("Register Student", () -> new StudentForm()));
        sidePanel.add(createMenuButton("Add Company", () -> new CompanyForm()));
        sidePanel.add(createMenuButton("Run Allocation", this::runAllocation));
        sidePanel.add(createMenuButton("View Allocations", () -> new AllocationView()));
        sidePanel.add(createMenuButton("Manage Offers", () -> new OfferDecisionForm()));
        sidePanel.add(createMenuButton("Exit System", () -> System.exit(0)));

        add(sidePanel, BorderLayout.WEST);

        // ===== MAIN CONTENT AREA =====
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // --- TOP SECTION: 4 Stat Cards ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(CONTENT_BG);
        statsPanel.setPreferredSize(new Dimension(0, 130));

        statsPanel.add(createStatCard("Total Students", fetchCount("SELECT COUNT(*) FROM students"), new Color(59, 130, 246))); // Blue
        statsPanel.add(createStatCard("Total Companies", fetchCount("SELECT COUNT(*) FROM companies"), new Color(139, 92, 246))); // Purple
        statsPanel.add(createStatCard("Allocated Students", fetchCount("SELECT COUNT(*) FROM allocations WHERE allocation_status IN ('ALLOCATED', 'ACCEPTED')"), new Color(34, 197, 94))); // Green
        
        // Unallocated = Total students minus those in the allocations table
        int unallocated = fetchCount("SELECT COUNT(*) FROM students") - fetchCount("SELECT COUNT(*) FROM allocations WHERE allocation_status IN ('ALLOCATED', 'ACCEPTED')");
        statsPanel.add(createStatCard("Unallocated Students", unallocated, new Color(239, 68, 68))); // Red

        contentPanel.add(statsPanel, BorderLayout.NORTH);

        // --- BOTTOM SECTION: 2 Top Lists ---
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        listsPanel.setBackground(CONTENT_BG);

        listsPanel.add(createListPanel("Top 5 Hiring Companies", fetchTopCompanies()));
        listsPanel.add(createListPanel("Top Meritorious Students", fetchTopStudents()));

        contentPanel.add(listsPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    // ===== UI HELPERS =====

    private JButton createMenuButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(SIDEBAR_HOVER);
                btn.setForeground(ACCENT_COLOR);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(Color.WHITE);
            }
        });
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JPanel createStatCard(String title, int count, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, color), // Colored top border
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 116, 139)); // Slate 500

        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        countLabel.setForeground(new Color(15, 23, 42)); // Slate 900

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createListPanel(String title, String htmlContent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel contentLabel = new JLabel("<html>" + htmlContent + "</html>");
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        contentLabel.setVerticalAlignment(SwingConstants.TOP);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentLabel, BorderLayout.CENTER);

        return panel;
    }

    // ===== DATABASE HELPERS =====

    private int fetchCount(String query) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String fetchTopCompanies() {
        StringBuilder sb = new StringBuilder("<table width='100%' cellpadding='8'>");
        String query = "SELECT c.company_name, COUNT(a.student_id) as interns " +
                       "FROM companies c JOIN allocations a ON c.company_id = a.company_id " +
                       "WHERE a.allocation_status IN ('ALLOCATED', 'ACCEPTED') " +
                       "GROUP BY c.company_id ORDER BY interns DESC LIMIT 5";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                sb.append("<tr><td style='color:#334155;'><b>").append(rs.getString("company_name"))
                  .append("</b></td><td align='right' style='color:#0ea5e9;'><b>")
                  .append(rs.getInt("interns")).append(" Interns</b></td></tr>");
            }
            if (sb.toString().equals("<table width='100%' cellpadding='8'>")) {
                return "<p style='color:#94a3b8;'>Run allocation to see data.</p>";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String fetchTopStudents() {
        StringBuilder sb = new StringBuilder("<table width='100%' cellpadding='8'>");
        // Assuming rank_position 1 is the best. Adjust to ORDER BY cgpa DESC if preferred.
        String query = "SELECT name, cgpa FROM students ORDER BY rank_position ASC LIMIT 5";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                sb.append("<tr><td style='color:#334155;'><b>").append(rs.getString("name"))
                  .append("</b></td><td align='right' style='color:#10b981;'><b>")
                  .append(rs.getDouble("cgpa")).append(" CGPA</b></td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("</table>");
        return sb.toString();
    }

    // ===== ALLOCATION RUNNER =====

    private void runAllocation() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            AllocationService allocationService = new AllocationService();
            allocationService.runAllocation();

            setCursor(Cursor.getDefaultCursor());
            
            JOptionPane.showMessageDialog(this,
                    "Algorithm Executed: Internship Allocation Completed Successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the dashboard to pull the new database counts!
            dispose();
            new MainDashboard();

        } catch (Exception e) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this,
                    "Error running allocation algorithm: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}