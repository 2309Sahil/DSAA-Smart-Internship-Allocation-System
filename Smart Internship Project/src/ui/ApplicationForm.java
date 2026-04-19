package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;

public class ApplicationForm extends JFrame {

    private int studentId;
    private int companyId;
    
    private JTextField resumeField;
    private JTextArea statementArea;

    public ApplicationForm(int studentId, int companyId, String companyName) {
        this.studentId = studentId;
        this.companyId = companyId;

        setTitle("Apply to " + companyName);
        setSize(450, 400);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Application: " + companyName, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Resume Link section
        JPanel resumePanel = new JPanel(new BorderLayout(5, 5));
        resumePanel.setBackground(Color.WHITE);
        JLabel resumeLabel = new JLabel("Resume Link (Google Drive, GitHub, etc.):");
        resumeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resumeField = new JTextField();
        resumeField.setPreferredSize(new Dimension(0, 30));
        resumePanel.add(resumeLabel, BorderLayout.NORTH);
        resumePanel.add(resumeField, BorderLayout.CENTER);

        // Statement Section
        JPanel statementPanel = new JPanel(new BorderLayout(5, 5));
        statementPanel.setBackground(Color.WHITE);
        JLabel statementLabel = new JLabel("Statement (Why should we hire you?):");
        statementLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statementArea = new JTextArea();
        statementArea.setLineWrap(true);
        statementArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(statementArea);
        statementPanel.add(statementLabel, BorderLayout.NORTH);
        statementPanel.add(scrollPane, BorderLayout.CENTER);

        formPanel.add(resumePanel, BorderLayout.NORTH);
        formPanel.add(statementPanel, BorderLayout.CENTER);

        add(formPanel, BorderLayout.CENTER);

        // Submit Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        
        JButton submitBtn = new JButton("Submit Application");
        submitBtn.setBackground(new Color(34, 197, 94)); // Green
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.addActionListener(e -> submitApplication());
        
        bottomPanel.add(submitBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void submitApplication() {
        String resume = resumeField.getText().trim();
        String statement = statementArea.getText().trim();

        if (resume.isEmpty() || statement.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO applications (student_id, company_id, resume_link, statement) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ps.setInt(2, companyId);
            ps.setString(3, resume);
            ps.setString(4, statement);
            
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Application submitted successfully! Your profile will be prioritized in the next allocation cycle.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close form

        } catch (SQLIntegrityConstraintViolationException ex) {
            // This catches the UNIQUE KEY constraint we added in Step 1!
            JOptionPane.showMessageDialog(this, "You have already applied to this company!", "Duplicate Application", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}