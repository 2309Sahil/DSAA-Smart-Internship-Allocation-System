package ui;

import javax.swing.*;
import java.awt.*;

public class AppLauncher extends JFrame {

    public AppLauncher() {
        setTitle("Smart Internship Allocation System");
        setSize(500, 400);
        setLocationRelativeTo(null); // Centers the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JLabel headerLabel = new JLabel("Welcome to the System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerLabel.setForeground(new Color(30, 41, 59)); // Slate 800
        headerLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Good spacing between buttons
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create the two main routing buttons
        JButton adminBtn = createStyledButton("Login as Admin", new Color(15, 23, 42), Color.WHITE); // Dark Navy
        JButton studentBtn = createStyledButton("Login as Student", new Color(59, 130, 246), Color.WHITE); // Bright Blue

        // Actions: Where do the buttons take you?
        adminBtn.addActionListener(e -> {
            dispose(); // Close launcher
            new MainDashboard(); // Open Admin Panel
        });

        studentBtn.addActionListener(e -> {
            // We don't close the launcher here, just pop open the login box over it
            new StudentLogin(); 
        });

        gbc.gridy = 0; 
        centerPanel.add(adminBtn, gbc);
        
        gbc.gridy = 1; 
        centerPanel.add(studentBtn, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // ===== FOOTER =====
        JLabel footerLabel = new JLabel("Powered by Max-Heap Allocation Engine", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(148, 163, 184));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(footerLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(280, 55)); // Big, clickable buttons
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}