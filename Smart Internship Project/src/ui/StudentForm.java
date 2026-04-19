package ui;

import service.StudentService;
import model.Student;

import javax.swing.*;
import java.awt.*;

public class StudentForm extends JFrame {

    private JTextField nameField;
    private JTextField rankField;
    private JTextField cgpaField;
    private JTextField skillsField;
    private JTextField preferenceField;

    public StudentForm() {
        setTitle("Register New Student");
        setSize(450, 480);
        setLocationRelativeTo(null); // Centers window on screen
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ===== HEADER =====
        JLabel headerLabel = new JLabel("Student Registration", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(new Color(30, 41, 59));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        // ===== FORM PANEL (Using GridBagLayout to prevent stretching) =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Adds breathing room between fields
        gbc.weightx = 1.0;

        // Initialize fields with better sizing
        nameField = createStyledTextField();
        rankField = createStyledTextField();
        cgpaField = createStyledTextField();
        skillsField = createStyledTextField();
        preferenceField = createStyledTextField();

        // Add fields to form using helper method
        addFormField(formPanel, gbc, "Full Name:", nameField, 0);
        addFormField(formPanel, gbc, "Rank Position:", rankField, 1);
        addFormField(formPanel, gbc, "CGPA:", cgpaField, 2);
        addFormField(formPanel, gbc, "Skills (comma separated):", skillsField, 3);
        addFormField(formPanel, gbc, "Preferences:", preferenceField, 4);

        add(formPanel, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton registerBtn = createStyledButton("Register Student", new Color(41, 128, 185), Color.WHITE);
        JButton closeBtn = createStyledButton("Cancel", new Color(149, 165, 166), Color.WHITE);

        registerBtn.addActionListener(e -> registerStudent());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(registerBtn);
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Helper method to add labels and text fields neatly
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField, int row) {
        gbc.gridy = row;
        
        // Label
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(71, 85, 105));
        panel.add(label, gbc);

        // TextField
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(textField, gbc);
    }

    // Helper method to style text fields
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Add a slight padding inside the text field
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

    private void registerStudent() {
        // UX Improvement: Check for empty fields before trying to save!
        if (nameField.getText().trim().isEmpty() || rankField.getText().trim().isEmpty() || 
            cgpaField.getText().trim().isEmpty() || skillsField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String name = nameField.getText().trim();
            int rank = Integer.parseInt(rankField.getText().trim());
            double cgpa = Double.parseDouble(cgpaField.getText().trim());
            String skills = skillsField.getText().trim();
            String preferences = preferenceField.getText().trim();

            Student student = new Student(
                    0, // ID is auto-incremented by DB
                    name,
                    rank,
                    cgpa,
                    skills,
                    preferences
            );

            StudentService studentService = new StudentService();
            studentService.addStudent(student);

            JOptionPane.showMessageDialog(this, "Student Registered Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the form after successful registration

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Rank must be a whole number and CGPA must be a decimal.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}