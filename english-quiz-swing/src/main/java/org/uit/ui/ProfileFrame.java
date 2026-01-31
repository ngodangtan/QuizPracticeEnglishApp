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

    public ProfileFrame() {
        setTitle("Profile");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

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

        // Password
        add(new JLabel("New Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        // Update Button
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
        add(updateButton);

        setVisible(true);
    }

    private void updateProfile() {
        String newPassword = new String(passwordField.getPassword());

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty");
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