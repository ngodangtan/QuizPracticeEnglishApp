package org.uit.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.uit.ApiClient;
import org.uit.session.Session;

public class ProfileFrame extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JToggleButton showPasswordBtn;
    private JToggleButton showConfirmBtn;

    public ProfileFrame() {
        setTitle("Profile");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        // Name
        add(new JLabel("Name:"));
        nameField = new JTextField(Session.getName());
        nameField.setEditable(false);
        add(nameField);

        // Email
        add(new JLabel("Email:"));
        emailField = new JTextField(Session.getEmail());
        emailField.setEditable(false);
        add(emailField);

        // Recent Score
        add(new JLabel("Recent Score:"));
        JTextField recentScoreField = new JTextField(Session.getRecentScore() != null && !Session.getRecentScore().isEmpty() ? Session.getRecentScore() : "--");
        recentScoreField.setEditable(false);
        add(recentScoreField);

        // Password (with show/hide button)
        add(new JLabel("New Password:"));
        JPanel passPanel = new JPanel(new BorderLayout());
        passwordField = new JPasswordField();
        passPanel.add(passwordField, BorderLayout.CENTER);
        showPasswordBtn = new JToggleButton("Show");
        final char defaultEcho = passwordField.getEchoChar();
        showPasswordBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordBtn.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                    showPasswordBtn.setText("Hide");
                } else {
                    passwordField.setEchoChar(defaultEcho);
                    showPasswordBtn.setText("Show");
                }
            }
        });
        passPanel.add(showPasswordBtn, BorderLayout.EAST);
        add(passPanel);

        // Confirm Password (with show/hide button)
        add(new JLabel("Confirm New Password:"));
        JPanel confirmPanel = new JPanel(new BorderLayout());
        confirmPasswordField = new JPasswordField();
        confirmPanel.add(confirmPasswordField, BorderLayout.CENTER);
        showConfirmBtn = new JToggleButton("Show");
        final char confirmDefaultEcho = confirmPasswordField.getEchoChar();
        showConfirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showConfirmBtn.isSelected()) {
                    confirmPasswordField.setEchoChar((char) 0);
                    showConfirmBtn.setText("Hide");
                } else {
                    confirmPasswordField.setEchoChar(confirmDefaultEcho);
                    showConfirmBtn.setText("Show");
                }
            }
        });
        confirmPanel.add(showConfirmBtn, BorderLayout.EAST);
        add(confirmPanel);

        // Update Button
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
        // Place the update button spanning two columns by adding an empty label first
        add(new JLabel());
        add(updateButton);

        setVisible(true);
    }

    private void updateProfile() {
        String newPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }

        try {
            boolean success = ApiClient.changePassword(
                Session.getUserId(),
                newPassword,
                Session.getToken()
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update password");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error while updating password: " + ex.getMessage()
            );
        }
    }


}