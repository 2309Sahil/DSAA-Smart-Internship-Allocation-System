package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentLogin extends JFrame {

    private JTextField idField;

    public StudentLogin() {
        setTitle("Student Portal Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Student Login", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(new Color(30, 41, 59));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Input Panel
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        centerPanel.setBackground(Color.WHITE);
        
        JLabel idLabel = new JLabel("Enter Student ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(new Color(71, 85, 105));
        
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(150, 32));
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idField.setHorizontalAlignment(JTextField.CENTER);

        centerPanel.add(idLabel);
        centerPanel.add(idField);
        add(centerPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(new Color(59, 130, 246)); // Blue
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addActionListener(e -> attemptLogin());
        bottomPanel.add(loginBtn);

        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void attemptLogin() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Student ID.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int studentId = Integer.parseInt(idText);
            String query = "SELECT name FROM students WHERE student_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String studentName = rs.getString("name");
                JOptionPane.showMessageDialog(this, "Welcome, " + studentName + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close login window
                new StudentDashboard(studentId, studentName); // Open Student Dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}