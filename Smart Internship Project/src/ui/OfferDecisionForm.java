package ui;

import database.DBConnection;
import service.ReallocationService;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OfferDecisionForm extends JFrame {

    private JTextField studentIdField;

    public OfferDecisionForm() {
        setTitle("Manage Student Offers");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ===== HEADER =====
        JLabel headerLabel = new JLabel("Offer Decision Portal", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(new Color(30, 41, 59)); // Slate 800
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel idLabel = new JLabel("Enter Student ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(new Color(71, 85, 105));
        
        studentIdField = new JTextField();
        studentIdField.setPreferredSize(new Dimension(200, 35));
        studentIdField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        studentIdField.setHorizontalAlignment(JTextField.CENTER);
        studentIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        formPanel.add(idLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.6;
        formPanel.add(studentIdField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Green for Accept, Red for Reject
        JButton acceptBtn = createStyledButton("Accept Offer", new Color(34, 197, 94), Color.WHITE);
        JButton rejectBtn = createStyledButton("Reject Offer", new Color(239, 68, 68), Color.WHITE);

        acceptBtn.addActionListener(e -> processDecision(true));
        rejectBtn.addActionListener(e -> processDecision(false));

        buttonPanel.add(acceptBtn);
        buttonPanel.add(rejectBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void processDecision(boolean isAccepted) {
        String idText = studentIdField.getText().trim();
        
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int studentId = Integer.parseInt(idText);

            // 1. Verify the student actually has an ALLOCATED offer right now
            String checkQuery = "SELECT company_id FROM allocations WHERE student_id = ? AND allocation_status = 'ALLOCATED'";
            PreparedStatement psCheck = conn.prepareStatement(checkQuery);
            psCheck.setInt(1, studentId);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "No pending offer found for this Student ID.", 
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int companyId = rs.getInt("company_id");

            if (isAccepted) {
                // If they accept, lock it in
                String acceptQuery = "UPDATE allocations SET allocation_status = 'ACCEPTED' WHERE student_id = ?";
                PreparedStatement psAccept = conn.prepareStatement(acceptQuery);
                psAccept.setInt(1, studentId);
                psAccept.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Offer Accepted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            } else {
                // If they reject, delete their allocation row entirely
                String deleteQuery = "DELETE FROM allocations WHERE student_id = ?";
                PreparedStatement psDelete = conn.prepareStatement(deleteQuery);
                psDelete.setInt(1, studentId);
                psDelete.executeUpdate();

                JOptionPane.showMessageDialog(this, 
                    "Offer Rejected. Triggering Dynamic Reallocation...", 
                    "Offer Rejected", JOptionPane.WARNING_MESSAGE);
                
                // Set the cursor to waiting while the algorithm runs
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                // 2. Trigger the dynamic reallocation algorithm for the newly opened seat
                ReallocationService reallocationService = new ReallocationService();
                reallocationService.reallocateSeat(companyId);

                setCursor(Cursor.getDefaultCursor());
                
                JOptionPane.showMessageDialog(this, 
                    "Reallocation Complete! Check 'View Allocations' to see who got the seat.", 
                    "System Update", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose(); // Close the form when done

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}