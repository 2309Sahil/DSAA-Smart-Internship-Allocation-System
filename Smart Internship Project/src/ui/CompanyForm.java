package ui;

import service.CompanyService;
import model.Company;

import javax.swing.*;
import java.awt.*;

public class CompanyForm extends JFrame {

    private JTextField nameField;
    private JTextField roleField;
    private JTextField seatsField;
    private JTextField skillsField;
    private JTextField durationField;

    public CompanyForm() {
        setTitle("Add New Company");
        setSize(450, 480);
        setLocationRelativeTo(null); // Centers window on screen
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ===== HEADER =====
        JLabel headerLabel = new JLabel("Company Registration", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(new Color(30, 41, 59)); // Slate 800
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        // ===== FORM PANEL (GridBagLayout prevents stretching) =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Breathing room
        gbc.weightx = 1.0;

        // Initialize styled fields
        nameField = createStyledTextField();
        roleField = createStyledTextField();
        seatsField = createStyledTextField();
        skillsField = createStyledTextField();
        durationField = createStyledTextField();

        // Add fields neatly
        addFormField(formPanel, gbc, "Company Name:", nameField, 0);
        addFormField(formPanel, gbc, "Internship Role:", roleField, 1);
        addFormField(formPanel, gbc, "Available Seats:", seatsField, 2);
        addFormField(formPanel, gbc, "Required Skills:", skillsField, 3);
        addFormField(formPanel, gbc, "Duration (Months):", durationField, 4);

        add(formPanel, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton addBtn = createStyledButton("Add Company", new Color(41, 128, 185), Color.WHITE);
        JButton cancelBtn = createStyledButton("Cancel", new Color(149, 165, 166), Color.WHITE);

        addBtn.addActionListener(e -> registerCompany());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Helper method to add labels and text fields
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField, int row) {
        gbc.gridy = row;
        
        // Label styling
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(71, 85, 105));
        panel.add(label, gbc);

        // TextField placement
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(textField, gbc);
    }

    // Helper method to style text fields
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return field;
    }

    // Helper method to style buttons
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void registerCompany() {
        // UX Improvement: Check for empty fields
        if (nameField.getText().trim().isEmpty() || roleField.getText().trim().isEmpty() || 
            seatsField.getText().trim().isEmpty() || skillsField.getText().trim().isEmpty() || 
            durationField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String name = nameField.getText().trim();
            String role = roleField.getText().trim();
            int seats = Integer.parseInt(seatsField.getText().trim());
            String skills = skillsField.getText().trim();
            String duration = durationField.getText().trim();

            if (seats <= 0) {
                JOptionPane.showMessageDialog(this, "Available seats must be greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Company company = new Company(
                    0, // ID is auto-incremented by DB
                    name,
                    role,
                    seats,
                    skills,
                    duration
            );

            CompanyService companyService = new CompanyService();
            companyService.addCompany(company);

            JOptionPane.showMessageDialog(this, "Company Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close form upon success

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seats must be a whole number (e.g., 5).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}